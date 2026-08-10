"""이 파일을 실행하면 옆의 풀이 파일이 테스트케이스로 채점된다.

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
