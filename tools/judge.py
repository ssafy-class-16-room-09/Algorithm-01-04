"""테스트케이스 기반 채점기 (로컬 / CI 공용).

사용법:
    python tools/judge.py <파일명(클래스명)> [--time-limit 초]

- 풀이 파일: week*/ 아래에서 <파일명>.java 를 찾는다.
- 테스트케이스: testcases/weekNN/<파일명>/*.in 과 같은 이름의 *.out 쌍.
- 종료 코드: 전체 통과 0, 실패 1, 채점 불가(파일·케이스 없음 등) 2.
"""
import argparse
import subprocess
import sys
import tempfile
from pathlib import Path

TRUNCATE = 800  # 실패 시 보여줄 출력 최대 길이


def normalize(text: str) -> str:
    """줄 끝 공백과 마지막 개행 차이는 무시하고 비교한다."""
    return "\n".join(line.rstrip() for line in text.strip().splitlines())


def clip(text: str) -> str:
    text = text.strip()
    return text if len(text) <= TRUNCATE else text[:TRUNCATE] + "\n... (생략)"


def main() -> int:
    try:
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    except AttributeError:
        pass

    parser = argparse.ArgumentParser()
    parser.add_argument("name", help="파일명(=public 클래스명), 확장자 제외")
    parser.add_argument("--time-limit", type=float, default=10.0, help="케이스당 제한 시간(초)")
    args = parser.parse_args()
    name = args.name

    root = Path(__file__).resolve().parent.parent

    solutions = [f for d in root.glob("week*") for f in d.rglob(f"{name}.java")]
    if not solutions:
        print(f"[채점 불가] week*/ 아래에서 {name}.java 를 찾지 못함")
        return 2
    solution = solutions[0]

    tc_root = root / "testcases"
    tc_dir = next((d for d in tc_root.glob(f"*/{name}") if d.is_dir()), None)
    if tc_dir is None:
        print(f"[채점 불가] testcases/weekNN/{name}/ 폴더가 없음")
        print("  → main에 테스트케이스를 올린 뒤 'Generate problem files' 액션으로 동기화했는지 확인")
        return 2

    cases = sorted(tc_dir.glob("*.in"))
    if not cases:
        print(f"[채점 불가] {tc_dir} 안에 *.in 파일이 없음")
        return 2

    with tempfile.TemporaryDirectory() as build_dir:
        compile_result = subprocess.run(
            ["javac", "-encoding", "UTF-8", "-d", build_dir, str(solution)],
            capture_output=True, text=True,
        )
        if compile_result.returncode != 0:
            print("[컴파일 에러]")
            print(compile_result.stderr.strip())
            return 1

        passed = 0
        judged = 0
        for case in cases:
            expected_file = case.with_suffix(".out")
            if not expected_file.exists():
                print(f"  [warn] {case.name}: 짝이 되는 .out 파일이 없어 건너뜀")
                continue
            judged += 1
            try:
                run = subprocess.run(
                    ["java", "-cp", build_dir, name],
                    stdin=case.open("r", encoding="utf-8"),
                    capture_output=True, text=True, encoding="utf-8",
                    timeout=args.time_limit,
                )
            except subprocess.TimeoutExpired:
                print(f"  ❌ {case.stem}: 시간 초과 ({args.time_limit}초)")
                continue

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
        print(f"🎉 {name}: {passed}/{judged} 전체 통과")
        return 0
    print(f"💥 {name}: {passed}/{judged} 통과")
    return 1


if __name__ == "__main__":
    sys.exit(main())
