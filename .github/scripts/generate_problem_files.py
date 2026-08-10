"""PROBLEMS.md를 파싱해 weekNN/online/에 문제별 자바 파일을 생성한다.

사용법: python3 generate_problem_files.py <PROBLEMS.md 경로> [저장소 루트]
이미 존재하는 파일은 건드리지 않는다.
"""
import re
import sys
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

# 백준 문제 폴더에 함께 생성되는 채점 실행 파일 (이 파일을 실행하면 채점된다)
TEST_RUNNER = '''"""이 파일을 실행하면 옆의 풀이 파일이 테스트케이스로 채점된다.

실행: python test.py  (IDE의 실행 버튼도 동일)
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
    [sys.executable, str(root / "tools" / "judge.py"), NAME], cwd=root
))
'''


def main() -> None:
    problems_path = Path(sys.argv[1])
    root = Path(sys.argv[2]) if len(sys.argv) > 2 else Path(".")

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
        base = root / f"week{week:02d}" / "online"
        target = base / name / f"{name}.java" if is_boj else base / f"{name}.java"
        if target.exists():
            continue
        target.parent.mkdir(parents=True, exist_ok=True)
        template = BOJ_TEMPLATE if is_boj else FILE_TEMPLATE
        header = f"// {title}\n" + (f"// {url}\n" if url else "")
        target.write_text(
            template.format(header=header, name=name), encoding="utf-8"
        )
        created.append(target)
        if is_boj:
            test_file = target.parent / "test.py"
            if not test_file.exists():
                test_file.write_text(TEST_RUNNER, encoding="utf-8")
                created.append(test_file)

    if created:
        print("생성된 파일:")
        for path in created:
            print(f"  {path}")
    else:
        print("생성할 새 파일 없음")


if __name__ == "__main__":
    main()
