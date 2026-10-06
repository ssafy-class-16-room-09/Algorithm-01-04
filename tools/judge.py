"""테스트케이스 기반 채점기 (로컬 / CI 공용).

사용법:
    python tools/judge.py <파일명(클래스명)> [--set full|samples] [--lang java|python] [--time-limit 초]

- 풀이 파일: week*/ 와 custom/(자체 문제) 아래에서 <파일명>.java 또는 <파일명>.py 를 찾는다.
  둘 다 있으면(main 브랜치 등) 각각 채점하고, --lang 으로 하나만 고를 수 있다.
- 검증 케이스(--set full, 기본): testcases/weekNN/<파일명>/*.in 과 같은 이름의 *.out 쌍.
  자체 문제는 testcases/custom/<파일명>/.
- 예제 케이스(--set samples): testcases/weekNN/<파일명>/samples/*.in 과 *.out 쌍.
- 시간 제한: CLI --time-limit > 문제 폴더 time_limit.txt > 환경 변수
  JUDGE_TIME_LIMIT_SECONDS > 10초 순서로 결정한다.
- 종료 코드: 전체 통과 0, 실패 1, 채점 불가(파일·케이스 없음 등) 2.
"""
import argparse
import math
import os
import signal
import subprocess
import sys
import tempfile
from pathlib import Path

TRUNCATE = 800  # 실패 시 보여줄 출력 최대 길이
TERMINATION_GRACE = 2.0  # 시간 초과 프로세스 트리를 정리하며 기다릴 최대 시간
DEFAULT_TIME_LIMIT = 10.0


class TimeLimitConfigError(ValueError):
    """메타데이터나 환경 변수의 시간 제한 값이 잘못됨."""


def normalize(text: str) -> str:
    """줄 끝 공백과 마지막 개행 차이는 무시하고 비교한다."""
    return "\n".join(line.rstrip() for line in text.strip().splitlines())


def clip(text: str) -> str:
    text = text.strip()
    return text if len(text) <= TRUNCATE else text[:TRUNCATE] + "\n... (생략)"


def positive_seconds(value: str) -> float:
    """argparse용 양의 유한 시간 값 검사."""
    try:
        seconds = float(value)
    except ValueError as exc:
        raise argparse.ArgumentTypeError("숫자를 입력해야 함") from exc
    if not math.isfinite(seconds) or seconds <= 0:
        raise argparse.ArgumentTypeError("0보다 큰 유한한 값이어야 함")
    return seconds


def configured_seconds(value: str, source: str) -> float:
    """메타데이터·환경 변수의 양의 유한 시간 값을 읽는다."""
    try:
        return positive_seconds(value.strip())
    except argparse.ArgumentTypeError as exc:
        raise TimeLimitConfigError(
            f"{source}: '{value.strip()}' — 초 단위의 0보다 큰 유한한 수여야 함"
        ) from exc


def resolve_time_limit(tc_dir: Path, cli_override) -> float:
    """CLI > 문제 메타데이터 > 환경 변수 > 기본값 순서로 제한 시간을 결정한다."""
    if cli_override is not None:
        return cli_override

    metadata = tc_dir / "time_limit.txt"
    if metadata.exists():
        try:
            value = metadata.read_text(encoding="utf-8")
        except OSError as exc:
            raise TimeLimitConfigError(f"{metadata}: 읽을 수 없음 ({exc})") from exc
        return configured_seconds(value, str(metadata))

    env_value = os.environ.get("JUDGE_TIME_LIMIT_SECONDS")
    if env_value is not None and env_value.strip():
        return configured_seconds(env_value, "환경 변수 JUDGE_TIME_LIMIT_SECONDS")
    return DEFAULT_TIME_LIMIT


def terminate_process_tree(proc: subprocess.Popen) -> None:
    """시간 초과한 풀이와 그 풀이가 만든 자식 프로세스를 함께 종료한다."""
    if os.name == "nt":
        # CREATE_NEW_PROCESS_GROUP만으로는 강제 종료가 되지 않으므로 taskkill /T를 쓴다.
        try:
            subprocess.run(
                ["taskkill", "/PID", str(proc.pid), "/T", "/F"],
                stdout=subprocess.DEVNULL,
                stderr=subprocess.DEVNULL,
                timeout=TERMINATION_GRACE,
                check=False,
            )
        except (OSError, subprocess.TimeoutExpired):
            pass
    else:
        # start_new_session=True로 만든 풀이 전용 프로세스 그룹 전체를 종료한다.
        try:
            os.killpg(proc.pid, signal.SIGKILL)
        except ProcessLookupError:
            pass

    if proc.poll() is None:
        proc.kill()


def run_case(run_cmd, input_file: Path, time_limit: float):
    """풀이를 제한 시간 안에서 실행한다. 반환값은 (실행 결과, 시간 초과 여부)."""
    popen_options = {}
    if os.name == "nt":
        popen_options["creationflags"] = subprocess.CREATE_NEW_PROCESS_GROUP
    else:
        popen_options["start_new_session"] = True

    with input_file.open("r", encoding="utf-8") as case_input:
        proc = subprocess.Popen(
            run_cmd,
            stdin=case_input,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            encoding="utf-8",
            **popen_options,
        )
        try:
            stdout, stderr = proc.communicate(timeout=time_limit)
        except subprocess.TimeoutExpired:
            terminate_process_tree(proc)
            try:
                # 종료된 프로세스의 파이프를 회수하되 여기서 다시 무한 대기하지 않는다.
                proc.communicate(timeout=TERMINATION_GRACE)
            except subprocess.TimeoutExpired:
                terminate_process_tree(proc)
                for stream in (proc.stdout, proc.stderr):
                    if stream is not None and not stream.closed:
                        stream.close()
                try:
                    proc.wait(timeout=TERMINATION_GRACE)
                except subprocess.TimeoutExpired:
                    pass
            return None, True

    return subprocess.CompletedProcess(run_cmd, proc.returncode, stdout, stderr), False


def judge(solution: Path, name: str, cases, label: str, time_limit: float) -> int:
    """풀이 파일 하나를 채점한다. 종료 코드 규약은 모듈 설명 참고."""
    lang_tag = "Java" if solution.suffix == ".java" else "Python"
    print(f"[{label} 채점 · {lang_tag}] {name} — 케이스 {len(cases)}개")

    with tempfile.TemporaryDirectory() as build_dir:
        if solution.suffix == ".java":
            compile_result = subprocess.run(
                ["javac", "-encoding", "UTF-8", "-d", build_dir, str(solution)],
                capture_output=True, text=True,
            )
            if compile_result.returncode != 0:
                print("[컴파일 에러]")
                print(compile_result.stderr.strip())
                return 1
            run_cmd = ["java", "-Dfile.encoding=UTF-8", "-cp", build_dir, name]
        else:
            run_cmd = [sys.executable, str(solution)]

        passed = 0
        judged = 0
        for case in cases:
            expected_file = case.with_suffix(".out")
            if not expected_file.exists():
                print(f"  [warn] {case.name}: 짝이 되는 .out 파일이 없어 건너뜀")
                continue
            judged += 1
            run, timed_out = run_case(run_cmd, case, time_limit)
            if timed_out:
                print(f"  ❌ {case.stem}: 시간 초과 ({time_limit}초)")
                continue

            assert run is not None
            if run.returncode != 0:
                print(f"  ❌ {case.stem}: 런타임 에러")
                print("     " + clip(run.stderr).replace("\n", "\n     "))
                continue

            expected = normalize(expected_file.read_text(encoding="utf-8"))
            actual = normalize(run.stdout)
            if actual == expected:
                passed += 1
                print(f"  ✅ {case.stem}: 통과")
            else:
                print(f"  ❌ {case.stem}: 오답")
                print(f"     [기대]\n     " + clip(expected).replace("\n", "\n     "))
                print(f"     [출력]\n     " + clip(actual).replace("\n", "\n     "))

    print()
    if judged == 0:
        print("[채점 불가] 유효한 테스트케이스 쌍(.in/.out)이 없음")
        return 2
    if passed == judged:
        print(f"🎉 {name} [{label} · {lang_tag}]: {passed}/{judged} 전체 통과")
        return 0
    print(f"💥 {name} [{label} · {lang_tag}]: {passed}/{judged} 통과")
    return 1


def main(root=None) -> int:
    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    except AttributeError:
        pass

    parser = argparse.ArgumentParser()
    parser.add_argument("name", help="파일명(=public 클래스명), 확장자 제외")
    parser.add_argument(
        "--set", choices=["full", "samples"], default="full", dest="case_set",
        help="full=검증 테스트케이스(기본), samples=예제만",
    )
    parser.add_argument(
        "--lang", choices=["java", "python"], default=None,
        help="풀이가 두 언어로 다 있을 때 하나만 채점 (기본: 있는 것 전부)",
    )
    parser.add_argument(
        "--time-limit", type=positive_seconds, default=None,
        help="케이스당 제한 시간 수동 지정(문제 메타데이터보다 우선)",
    )
    args = parser.parse_args()
    name = args.name
    label = "예제" if args.case_set == "samples" else "검증"

    root = Path(__file__).resolve().parent.parent if root is None else Path(root)

    exts = {"java": [".java"], "python": [".py"]}.get(args.lang, [".java", ".py"])
    solutions = []
    for ext in exts:
        # 실행 파일(<문제명>Test.java 등)은 이름이 다르므로 자연히 제외된다
        dirs = [*root.glob("week*"), root / "custom"]
        found = sorted(f for d in dirs if d.is_dir() for f in d.rglob(f"{name}{ext}"))
        if found:
            solutions.append(found[0])
    if not solutions:
        want = " 또는 ".join(f"{name}{e}" for e in exts)
        print(f"[채점 불가] week*/ 와 custom/ 아래에서 {want} 를 찾지 못함")
        return 2

    tc_root = root / "testcases"
    tc_dir = next((d for d in tc_root.glob(f"*/{name}") if d.is_dir()), None)
    if tc_dir is None:
        print(f"[채점 불가] testcases/weekNN/{name}/ (자체 문제는 testcases/custom/{name}/) 폴더가 없음")
        print("  → main에 테스트케이스를 올린 뒤 'Generate problem files' 액션으로 동기화했는지 확인")
        return 2

    try:
        time_limit = resolve_time_limit(tc_dir, args.time_limit)
    except TimeLimitConfigError as exc:
        print(f"[채점 불가] 시간 제한 설정 오류: {exc}")
        return 2

    case_dir = tc_dir / "samples" if args.case_set == "samples" else tc_dir
    cases = sorted(case_dir.glob("*.in"))
    if not cases:
        if args.case_set == "samples":
            print(f"[채점 불가] 등록된 예제가 없음")
            print(f"  → {tc_dir / 'samples'} 에 01.in / 01.out 쌍을 추가하면 예제로 채점된다")
        else:
            print(f"[채점 불가] {tc_dir} 안에 *.in 파일이 없음")
        return 2

    worst = 0
    for i, solution in enumerate(solutions):
        if i:
            print("─" * 40)
        worst = max(worst, judge(solution, name, cases, label, time_limit))
    return worst


if __name__ == "__main__":
    sys.exit(main())
