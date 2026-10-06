"""tools/judge.py의 실행·시간 제한 회귀 테스트."""
import argparse
import contextlib
import io
import os
import shutil
import sys
import tempfile
import time
import unittest
from pathlib import Path
from unittest import mock

try:
    from tools import judge
except ImportError:  # tools/를 discovery 시작점으로 실행한 경우
    import judge


class JudgeExecutionTest(unittest.TestCase):
    def run_solution(self, suffix: str, name: str, source: str, expected: str, limit: float):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            solution = root / f"{name}{suffix}"
            solution.write_text(source, encoding="utf-8")
            case = root / "01.in"
            case.write_text("\n", encoding="utf-8")
            case.with_suffix(".out").write_text(expected, encoding="utf-8")

            output = io.StringIO()
            started = time.monotonic()
            with contextlib.redirect_stdout(output):
                status = judge.judge(solution, name, [case], "검증", limit)
            return status, output.getvalue(), time.monotonic() - started

    def test_fast_python_solution_passes(self):
        status, output, _ = self.run_solution(
            ".py", "FastPython", "print('answer')\n", "answer\n", 1.0,
        )
        self.assertEqual(0, status)
        self.assertIn("전체 통과", output)

    def test_python_solution_just_under_limit_passes(self):
        status, _, _ = self.run_solution(
            ".py", "UnderLimit",
            "import time\ntime.sleep(0.05)\nprint('answer')\n",
            "answer\n", 1.0,
        )
        self.assertEqual(0, status)

    def test_python_solution_over_limit_is_tle(self):
        status, output, elapsed = self.run_solution(
            ".py", "OverLimit",
            "import time\nprint('answer', flush=True)\ntime.sleep(5)\n",
            "answer\n", 0.2,
        )
        self.assertEqual(1, status)
        self.assertIn("시간 초과", output)
        self.assertLess(elapsed, 3.0)

    def test_nonzero_exit_reports_stderr_as_runtime_error(self):
        status, output, _ = self.run_solution(
            ".py", "RuntimeError",
            "import sys\nsys.stderr.write('failure detail\\n')\nsys.exit(3)\n",
            "", 1.0,
        )
        self.assertEqual(1, status)
        self.assertIn("런타임 에러", output)
        self.assertIn("failure detail", output)

    def test_wrong_stdout_is_not_hidden_by_stderr(self):
        status, output, _ = self.run_solution(
            ".py", "WrongOutput",
            "import sys\nprint('wrong')\nsys.stderr.write('diagnostic\\n')\n",
            "answer\n", 1.0,
        )
        self.assertEqual(1, status)
        self.assertIn("오답", output)
        self.assertIn("wrong", output)

    def test_timeout_kills_descendant_process(self):
        with tempfile.TemporaryDirectory() as tmp:
            marker = Path(tmp) / "child-survived.txt"
            child_code = (
                "import pathlib,time; "
                "time.sleep(1.5); "
                f"pathlib.Path({str(marker)!r}).write_text('alive')"
            )
            source = (
                "import subprocess,sys,time\n"
                f"subprocess.Popen([sys.executable, '-c', {child_code!r}])\n"
                "time.sleep(0.3)\n"
                "while True: time.sleep(1)\n"
            )
            status, output, _ = self.run_solution(
                ".py", "ProcessTree", source, "", 0.8,
            )
            self.assertEqual(1, status)
            self.assertIn("시간 초과", output)
            time.sleep(1.0)
            self.assertFalse(marker.exists(), "시간 초과한 풀이의 자식 프로세스가 남아 있음")

    @unittest.skipUnless(shutil.which("javac") and shutil.which("java"), "JDK가 없음")
    def test_fast_java_solution_passes(self):
        source = """public class FastJava {
    public static void main(String[] args) {
        System.out.println("answer");
    }
}
"""
        status, output, _ = self.run_solution(
            ".java", "FastJava", source, "answer\n", 2.0,
        )
        self.assertEqual(0, status)
        self.assertIn("전체 통과", output)

    @unittest.skipUnless(shutil.which("javac") and shutil.which("java"), "JDK가 없음")
    def test_infinite_java_solution_is_tle(self):
        source = """public class SlowJava {
    public static void main(String[] args) throws Exception {
        while (true) Thread.sleep(1000);
    }
}
"""
        status, output, elapsed = self.run_solution(
            ".java", "SlowJava", source, "", 0.3,
        )
        self.assertEqual(1, status)
        self.assertIn("시간 초과", output)
        self.assertLess(elapsed, 4.0)


class TimeLimitValidationTest(unittest.TestCase):
    def test_rejects_zero_negative_and_non_finite_limits(self):
        for value in ("0", "-1", "nan", "inf"):
            with self.subTest(value=value):
                with self.assertRaises(argparse.ArgumentTypeError):
                    judge.positive_seconds(value)

    def test_accepts_positive_limit(self):
        self.assertEqual(0.25, judge.positive_seconds("0.25"))


class TimeLimitResolutionTest(unittest.TestCase):
    def test_problem_metadata_has_priority_over_environment(self):
        with tempfile.TemporaryDirectory() as tmp:
            tc_dir = Path(tmp)
            (tc_dir / "time_limit.txt").write_text("2\n", encoding="utf-8")
            with mock.patch.dict(os.environ, {"JUDGE_TIME_LIMIT_SECONDS": "8"}):
                self.assertEqual(2.0, judge.resolve_time_limit(tc_dir, None))

    def test_samples_use_problem_root_metadata(self):
        with tempfile.TemporaryDirectory() as tmp:
            tc_dir = Path(tmp)
            (tc_dir / "samples").mkdir()
            (tc_dir / "time_limit.txt").write_text("1.5", encoding="utf-8")
            (tc_dir / "samples" / "time_limit.txt").write_text("99", encoding="utf-8")
            self.assertEqual(1.5, judge.resolve_time_limit(tc_dir, None))

    def test_cli_override_has_priority_over_invalid_metadata(self):
        with tempfile.TemporaryDirectory() as tmp:
            tc_dir = Path(tmp)
            (tc_dir / "time_limit.txt").write_text("invalid", encoding="utf-8")
            self.assertEqual(0.25, judge.resolve_time_limit(tc_dir, 0.25))

    def test_environment_is_fallback_when_metadata_is_absent(self):
        with tempfile.TemporaryDirectory() as tmp:
            with mock.patch.dict(os.environ, {"JUDGE_TIME_LIMIT_SECONDS": "3.5"}):
                self.assertEqual(3.5, judge.resolve_time_limit(Path(tmp), None))

    def test_default_is_ten_seconds(self):
        with tempfile.TemporaryDirectory() as tmp:
            with mock.patch.dict(os.environ, {}, clear=True):
                self.assertEqual(10.0, judge.resolve_time_limit(Path(tmp), None))

    def test_invalid_metadata_is_configuration_error(self):
        with tempfile.TemporaryDirectory() as tmp:
            tc_dir = Path(tmp)
            metadata = tc_dir / "time_limit.txt"
            metadata.write_text("NaN", encoding="utf-8")
            with self.assertRaisesRegex(judge.TimeLimitConfigError, "time_limit.txt"):
                judge.resolve_time_limit(tc_dir, None)

    def test_invalid_metadata_makes_main_unjudgeable(self):
        with tempfile.TemporaryDirectory() as tmp:
            root = Path(tmp)
            solution_dir = root / "week99"
            solution_dir.mkdir()
            (solution_dir / "InvalidLimit.py").write_text(
                "print('answer')\n", encoding="utf-8"
            )
            tc_dir = root / "testcases" / "week99" / "InvalidLimit"
            tc_dir.mkdir(parents=True)
            (tc_dir / "01.in").write_text("\n", encoding="utf-8")
            (tc_dir / "01.out").write_text("answer\n", encoding="utf-8")
            (tc_dir / "time_limit.txt").write_text("invalid", encoding="utf-8")

            output = io.StringIO()
            argv = ["judge.py", "InvalidLimit", "--lang", "python"]
            with mock.patch.object(sys, "argv", argv):
                with contextlib.redirect_stdout(output):
                    status = judge.main(root)
            self.assertEqual(2, status)
            self.assertIn("[채점 불가] 시간 제한 설정 오류", output.getvalue())

    def test_invalid_environment_is_configuration_error(self):
        with tempfile.TemporaryDirectory() as tmp:
            with mock.patch.dict(os.environ, {"JUDGE_TIME_LIMIT_SECONDS": "zero"}):
                with self.assertRaisesRegex(
                    judge.TimeLimitConfigError, "JUDGE_TIME_LIMIT_SECONDS"
                ):
                    judge.resolve_time_limit(Path(tmp), None)


if __name__ == "__main__":
    unittest.main()
