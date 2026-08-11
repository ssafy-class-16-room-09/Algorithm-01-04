"""PROBLEMS.md를 파싱해 weekNN/online/에 문제별 풀이 파일을 생성한다.

사용법: python3 generate_problem_files.py <PROBLEMS.md 경로> [저장소 루트] [--lang java|python]
이미 존재하는 파일은 건드리지 않는다 (다른 언어로 이미 생성된 문제도 건너뜀).
"""
import argparse
import re
from pathlib import Path

FILE_TEMPLATE = """{header}public class {name} {{

}}
"""

# 파일명이 Boj로 시작하면 표준 입출력 형식이므로 main 스켈레톤을 넣는다
BOJ_TEMPLATE = """{header}import java.io.*;
import java.util.*;

public class {name} {{
    public static void main(String[] args) throws IOException {{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();

        System.out.print(sb);
    }}
}}
"""

PY_FILE_TEMPLATE = """{header}
def solution():
    pass
"""

PY_BOJ_TEMPLATE = """{header}import sys

input = sys.stdin.readline

"""

# 백준 문제 폴더에 함께 생성되는 채점 실행 파일 (내용이 바뀌면 자동 갱신됨)
RUNNER_TEMPLATE = '''"""{doc}
이 파일은 자동 생성·갱신되므로 직접 수정하지 않는다.
"""
import subprocess
import sys
from pathlib import Path

NAME = Path(__file__).resolve().parent.name  # 폴더명 = 문제 파일명

root = Path(__file__).resolve().parent
while root != root.parent and not (root / "tools" / "judge.py").exists():
    root = root.parent
if not (root / "tools" / "judge.py").exists():
    sys.exit("tools/judge.py 를 찾지 못함 — Generate 액션으로 브랜치를 동기화했는지 확인")

sys.exit(subprocess.call(
    [sys.executable, str(root / "tools" / "judge.py"), NAME{extra}], cwd=root
))
'''

TEST_RUNNER = RUNNER_TEMPLATE.format(
    doc="""예제 채점 — 프로그래머스의 '코드 실행'에 해당.

등록된 예제(testcases/주차/문제/samples/)만 빠르게 돌려본다.
실행: python test.py        전체 검증은 python submit.py""",
    extra=', "--set", "samples"',
)

SUBMIT_RUNNER = RUNNER_TEMPLATE.format(
    doc="""검증 채점 — 프로그래머스의 '제출 후 채점'에 해당.

전체 테스트케이스(대형 성능 케이스 포함)로 채점한다.
실행: python submit.py      예제만 돌리려면 python test.py""",
    extra="",
)


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("problems", help="PROBLEMS.md 경로")
    parser.add_argument("root", nargs="?", default=".", help="저장소 루트")
    parser.add_argument("--lang", choices=["java", "python"], default="java")
    args = parser.parse_args()
    problems_path = Path(args.problems)
    root = Path(args.root)

    week = None
    created = []
    for line in problems_path.read_text(encoding="utf-8").splitlines():
        stripped = line.strip()

        # "## week-01" 또는 "<summary>week-01 ..." 형태의 주차 구분
        # 제목에 "완료"가 있으면 그 주차는 파일 생성 대상에서 제외한다
        if stripped.startswith(("#", "<summary")):
            m = re.search(r"week[- ]?(\d+)", stripped, re.IGNORECASE)
            week = int(m.group(1)) if m and "완료" not in stripped else None
            continue

        if week is None or not stripped.startswith("|"):
            continue

        cells = [c.strip() for c in stripped.strip("|").split("|")]
        if len(cells) < 3:
            continue

        # 표 칼럼: 문제 | 파일명 | 링크 (링크는 순수 URL, 마크다운 링크, 또는 "-")
        title, name = cells[0], cells[1]
        if not re.fullmatch(r"[A-Z][A-Za-z0-9]*", name):
            # 헤더 행("파일명")과 구분선 행("---")은 조용히 건너뛰고, 그 외엔 경고
            if title and title != "문제" and set(title) - set("-: "):
                print(f"[skip] week{week:02d} '{title}': 파일명 '{name}'이 PascalCase가 아님")
            continue
        url_match = re.search(r"https?://\S+?(?=\)|$)", cells[2])
        url = url_match.group(0) if url_match else None

        # 백준 문제는 전용 폴더 안에 풀이 파일 + 채점 실행 파일(test.py)을 만든다
        is_boj = name.startswith("Boj")
        week_dir = root / f"week{week:02d}"
        base = week_dir / "online"
        folder = base / name if is_boj else base
        # 위치·언어와 무관하게 주차 폴더 어딘가에 이미 풀이 파일이 있으면 건너뜀
        # (파일을 옮겼거나 예전 구조로 생성된 경우에도 중복 생성 방지)
        solution_exists = week_dir.exists() and (
            any(week_dir.rglob(f"{name}.java")) or any(week_dir.rglob(f"{name}.py"))
        )
        if not solution_exists:
            if args.lang == "python":
                template = PY_BOJ_TEMPLATE if is_boj else PY_FILE_TEMPLATE
                header = f"# {title}\n" + (f"# {url}\n" if url else "")
                target = folder / f"{name}.py"
            else:
                template = BOJ_TEMPLATE if is_boj else FILE_TEMPLATE
                header = f"// {title}\n" + (f"// {url}\n" if url else "")
                target = folder / f"{name}.java"
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(
                template.format(header=header, name=name), encoding="utf-8"
            )
            created.append(target)

        # 백준 폴더의 실행 파일(test.py/submit.py)은 없으면 만들고, 내용이 낡았으면 갱신
        if is_boj and folder.exists():
            for fname, content in (("test.py", TEST_RUNNER), ("submit.py", SUBMIT_RUNNER)):
                runner = folder / fname
                if not runner.exists() or runner.read_text(encoding="utf-8") != content:
                    runner.write_text(content, encoding="utf-8")
                    created.append(runner)

    if created:
        print("생성된 파일:")
        for path in created:
            print(f"  {path}")
    else:
        print("생성할 새 파일 없음")


if __name__ == "__main__":
    main()
