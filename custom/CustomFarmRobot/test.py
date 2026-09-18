"""예제 채점 — 프로그래머스의 '코드 실행'에 해당.

등록된 예제(testcases/주차/문제/samples/)만 빠르게 돌려본다.
실행: python test.py        전체 검증은 python submit.py
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
    [sys.executable, str(root / "tools" / "judge.py"), NAME, "--set", "samples"], cwd=root
))
