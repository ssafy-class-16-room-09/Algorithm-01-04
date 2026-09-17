"""검증 채점 — 프로그래머스의 '제출 후 채점'에 해당.

전체 테스트케이스(대형 성능 케이스 포함)로 채점한다.
실행: python submit.py      예제만 돌리려면 python test.py
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
    [sys.executable, str(root / "tools" / "judge.py"), NAME], cwd=root
))
