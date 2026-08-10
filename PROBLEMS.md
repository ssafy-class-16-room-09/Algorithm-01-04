# 📋 주차별 문제

> 이 파일에 문제를 등록한 뒤 [Actions → Generate problem files](https://github.com/ssafy-class-16-room-09/Algorithm-01-04/actions/workflows/generate-problem-files.yml)에서 **Run workflow**를 누르면, 각 개인 브랜치의 `weekNN/online/` 폴더에 문제별 자바 파일이 자동 생성됩니다.

## ✍️ 등록 방법

1. 아래 형식대로 주차 토글(`<details>` + `<summary>week-NN</summary>`)과 표에 문제를 추가한다. (`<summary>` 다음에 빈 줄이 있어야 표가 렌더링된다)
2. **파일명** 칸은 영문 PascalCase로 쓴다 (확장자 제외, 예: `PgsDiskController`). 이 이름 그대로 `.java` 파일과 public 클래스가 만들어진다.
3. **링크** 칸에는 문제 URL을 적는다. 자체과제처럼 링크가 없으면 `-` 를 적는다.
4. main에 커밋 후 Actions 탭에서 워크플로를 실행한다 (반영할 브랜치 선택 가능, 기본값 all).

- 이미 존재하는 파일은 절대 덮어쓰지 않으므로 여러 번 실행해도 안전하다.
- 주차 제목에 `(완료)` 를 붙이면 그 주차는 파일 생성을 건너뛴다 (지난 주차 기록용).

## 🧪 백준(BOJ) 문제 & 테스트케이스 채점

백준 서비스 종료로 문제를 직접 등록하는 경우:

1. 이슈에 문제 사진을 올리고, **링크** 칸에 그 이슈 링크를 적는다.
2. **파일명**은 `Boj`로 시작하게 짓는다 (예: `Boj1225`) — 전용 폴더 `weekNN/online/<파일명>/` 안에 표준 입출력용 `main` 스켈레톤과 채점 실행 파일이 생성된다.
   ```
   week02/online/BojDragonAndDungeon/
   ├── BojDragonAndDungeon.java   ← 여기에 코드 작성
   └── test.py                    ← 이 파일을 실행하면 채점
   ```
3. 테스트케이스를 main의 `testcases/weekNN/<파일명>/` 에 `01.in` / `01.out` 쌍으로 올린다 (번호는 자유, `.in`과 같은 이름의 `.out`이 짝).
4. 워크플로를 실행하면 문제 파일과 함께 `tools/` · `testcases/` 가 각 브랜치에 동기화된다.

**채점 방법**

- 로컬(실행 버튼): 문제 폴더의 `test.py`를 실행하면 케이스별 ✅/❌ 와 기대/실제 출력이 표시된다 (IDE에서 test.py 실행 버튼을 눌러도 됨).
  ```
  python test.py
  ```
  저장소 루트에서 `python tools/judge.py Boj1225` 처럼 직접 실행해도 동일하다.
- 자동 채점: 개인 브랜치에 push하면 **Judge 액션**이 변경된 파일 중 테스트케이스가 있는 문제를 자동 채점하고, 커밋에 ✅/❌ 체크가 달린다 (상세 결과는 Actions 탭 Summary).

---

<details open>
<summary><b>week-02 (3문제)</b></summary>

| 문제 | 파일명 | 링크 |
|---|---|---|
| 프로그래머스 42746 · 가장 큰 수 | PgsLargestNumber | https://school.programmers.co.kr/learn/courses/30/lessons/42746 |
| 프로그래머스 43238 · 입국심사 | PgsImmigration | https://school.programmers.co.kr/learn/courses/30/lessons/43238 |
| 백준 · 드래곤 앤 던전 | BojDragonAndDungeon | https://www.notion.so/3b8216e328d180ffb42cc824ab0d5cd4?source=copy_link |

</details>

<details>
<summary><b>week-01 (3문제, 완료)</b></summary>

| 문제 | 파일명 | 링크 |
|---|---|---|
| 프로그래머스 42584 · 주식가격 | PgsStockPrice | https://school.programmers.co.kr/learn/courses/30/lessons/42584 |
| SWEA 1225 · 암호생성기 | Swea1225 | https://swexpertacademy.com/main/code/problem/problemDetail.do?contestProbId=AV14uWl6AF0CFAYD |
| 자체과제 · 링크드리스트 만들기 | MyLinkedList | - |

</details>
