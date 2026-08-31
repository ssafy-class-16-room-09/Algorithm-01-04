# 📋 주차별 문제

> 이 파일에 문제를 등록한 뒤 [Actions → Generate problem files](https://github.com/ssafy-class-16-room-09/Algorithm-01-04/actions/workflows/generate-problem-files.yml)에서 **Run workflow**를 누르면, 각 브랜치의 `weekNN/online/` 또는 `weekNN/offline/` 폴더에 문제별 풀이 파일이 자동 생성됩니다.

## ✍️ 등록 방법

1. 아래 형식대로 주차 토글(`<details>` + `<summary>week-NN</summary>`)과 표에 문제를 추가한다. (`<summary>` 다음에 빈 줄이 있어야 표가 렌더링된다)
2. 표 칼럼은 **문제 | 파일명 | 링크 | 구분 | 언어 | 비고** 순서다.
   - **파일명**: 영문 PascalCase (확장자 제외, 예: `PgsDiskController`). 이 이름 그대로 파일과 public 클래스가 만들어진다.
   - **링크**: `[문제명](URL)` 형식의 마크다운 링크. 표가 길어지지 않게 URL을 그대로 적지 않는다. 없으면 `-`.
   - **구분**: `online`(주중 과제) 또는 `offline`(목요일 오프라인 세션 문제). 폴더가 이에 따라 갈린다.
   - **언어**: `any`(기본, 브랜치 설정을 따름) 또는 `java`. **B형 연습처럼 자바로만 풀어야 하는 문제는 `java`** 로 적으면 파이썬 브랜치에도 자바 파일이 생성된다.
   - **비고**: 자유 기입 (난이도, 주의사항 등). 없으면 `-`.
3. main에 커밋 후 Actions 탭에서 워크플로를 실행한다 (반영할 브랜치 선택 가능, 기본값 all).

- 이미 존재하는 풀이 파일은 절대 덮어쓰지 않으므로 여러 번 실행해도 안전하다.
- 주차 제목에 `(완료)` 를 붙이면 그 주차는 파일 생성을 건너뛴다 (지난 주차 기록용).

## ⚙️ 브랜치별 설정

[워크플로 파일](.github/workflows/generate-problem-files.yml) 상단의 `env` 에서 관리한다:

| 설정 | 의미 | 현재 |
|---|---|---|
| `PYTHON_BRANCHES` | 나열된 브랜치는 파이썬(.py) 풀이 + 파이썬 실행 파일(test.py/submit.py), 나머지는 자바(.java) 풀이 + 자바 실행 파일(`<문제명>Test/Submit.java`) | hjh |
| (main 브랜치) | 항상 자바+파이썬 풀이 파일과 양쪽 실행 파일을 모두 생성 — 누구나 어떤 환경에서든 실행 가능 | 고정 |

## 🧪 백준(BOJ) 문제 & 테스트케이스 채점

백준 서비스 종료로 문제를 직접 등록하는 경우:

1. 이슈나 노션에 문제를 올리고, **링크** 칸에 그 링크를 적는다.
2. **파일명**은 `Boj`로 시작하게 짓는다 (예: `Boj1225`) — 전용 폴더 `weekNN/<구분>/<파일명>/` 안에 표준 입출력용 `main` 스켈레톤과 채점 실행 파일이 생성된다. 실행 파일은 풀이 언어를 따라간다 (자바로 풀면 자바 실행 파일, 파이썬으로 풀면 파이썬 실행 파일):
   ```
   [파이썬 브랜치]                        [자바 브랜치 — 파이썬 설치 불필요]
   week02/online/BojDragonAndDungeon/    week02/online/BojDragonAndDungeon/
   ├── BojDragonAndDungeon.java          ├── BojDragonAndDungeon.java
   ├── test.py      ← 예제 채점          ├── BojDragonAndDungeonTest.java    ← 예제 채점
   └── submit.py    ← 검증 채점          └── BojDragonAndDungeonSubmit.java  ← 검증 채점
   ```
3. 테스트케이스를 main의 `testcases/weekNN/<파일명>/` 에 올린다. **예제와 검증을 구분**해서:
   ```
   testcases/week02/BojDragonAndDungeon/
   ├── samples/          ← 예제 (문제에 공개된 입출력, 예제 채점 대상)
   │   ├── 01.in
   │   └── 01.out
   ├── 01.in             ← 검증 케이스 (엣지·대형 성능 포함, 검증 채점 대상)
   ├── 01.out
   └── ...
   ```
   번호는 자유이고 `.in`과 같은 이름의 `.out`이 짝이다.
4. 워크플로를 실행하면 문제 파일과 함께 `tools/` · `testcases/` 가 각 브랜치에 동기화된다.

**채점 방법 (프로그래머스와 같은 2단계)**

| | 예제 실행 (프로그래머스 '코드 실행') | 검증 실행 (프로그래머스 '제출 후 채점') |
|---|---|---|
| 용도 | 예제만 빠르게 돌려 로직 확인. 풀이 도중 수시로 | 전체 테스트케이스(대형 성능 포함). 예제 통과 후 최종 확인 |
| 파이썬 실행 파일 | 문제 폴더에서 `python test.py` | 문제 폴더에서 `python submit.py` |
| 자바 실행 파일 (IntelliJ) | `<문제명>Test.java` 열고 Run ▶ | `<문제명>Submit.java` 열고 Run ▶ |
| 저장소 루트에서 직접 | `python tools/judge.py Boj1225 --set samples` | `python tools/judge.py Boj1225` |

- 어느 방식이든 케이스별 ✅/❌ 와 오답 시 기대/실제 출력이 표시된다.
- main 브랜치처럼 같은 문제의 `.java`와 `.py`가 둘 다 있으면 각각 채점되고, `--lang java` / `--lang python` 으로 하나만 고를 수 있다.
- **예제 추가**: `testcases/weekNN/<파일명>/samples/` 에 `.in`/`.out` 쌍만 넣으면 바로 채점에 포함된다. main에 올리면 전원 공유, 본인 브랜치에만 두면 개인용.
- 자동 채점: 개인 브랜치에 push하면 **Judge 액션**이 변경된 파일 중 테스트케이스가 있는 문제를 검증 세트로 자동 채점하고, 커밋에 ✅/❌ 체크가 달린다 (상세 결과는 Actions 탭 Summary).

---

<details open>
<summary><b>week-05 (6문제)</b></summary>

| 문제 | 파일명 | 링크 | 구분 | 언어 | 비고 |
|---|---|---|---|---|---|
| 프로그래머스 12900 · 2 x n 타일링 | PgsTwoByNTiling | [2 x n 타일링](https://school.programmers.co.kr/learn/courses/30/lessons/12900) | online | any | DP 개념 · Lv.3 |
| 프로그래머스 12945 · 피보나치 수 | PgsFibonacci | [피보나치 수](https://school.programmers.co.kr/learn/courses/30/lessons/12945) | online | any | DP 개념 · Lv.2 · 메모이제이션 |
| 프로그래머스 42898 · 등굣길 | PgsSchoolRoute | [등굣길](https://school.programmers.co.kr/learn/courses/30/lessons/42898) | online | any | DP 응용 · Lv.3 |
| 프로그래머스 43105 · 정수 삼각형 | PgsIntegerTriangle | [정수 삼각형](https://school.programmers.co.kr/learn/courses/30/lessons/43105) | online | any | DP 응용 · Lv.3 |
| 백준 1520 · 내리막 길 | BojDownhillPath | - | offline | any | DP · 골드3 |
| 백준 2482 · 색상환 | BojColorWheel | - | offline | any | DP · 골드5 · BOJ 서비스 종료로 자체 테스트케이스 채점 예정 |

</details>

<details>
<summary><b>week-04 (6문제)</b></summary>

| 문제 | 파일명 | 링크 | 구분 | 언어 | 비고 |
|---|---|---|---|---|---|
| 프로그래머스 43165 · 타겟 넘버 | PgsTargetNumber | [타겟 넘버](https://school.programmers.co.kr/learn/courses/30/lessons/43165) | online | any | DFS 개념 · Lv.2 |
| 프로그래머스 43162 · 네트워크 | PgsNetwork | [네트워크](https://school.programmers.co.kr/learn/courses/30/lessons/43162) | online | any | DFS 응용 · Lv.3 |
| 프로그래머스 1844 · 게임 맵 최단거리 | PgsGameMapShortestDistance | [게임 맵 최단거리](https://school.programmers.co.kr/learn/courses/30/lessons/1844) | online | any | BFS 개념 · Lv.2 · 효율성 테스트 있음 |
| 프로그래머스 43163 · 단어 변환 | PgsWordTransform | [단어 변환](https://school.programmers.co.kr/learn/courses/30/lessons/43163) | online | any | BFS 응용 · Lv.3 |
| 백준 2667 · 단지번호붙이기 | BojHousingComplex | [단지번호붙이기](https://app.notion.com/p/363216e328d182569a490134208eaa68?source=copy_link) | offline | any | DFS · 실버1 |
| 백준 2206 · 벽 부수고 이동하기 | BojBreakWall | [벽 부수고 이동하기](https://app.notion.com/p/027216e328d182e9b4e901fc4baea044?source=copy_link) | offline | any | BFS · 골드3 · 메모리 192MB 주의 |

</details>

<details>
<summary><b>week-03 (5문제)</b></summary>

| 문제 | 파일명 | 링크 | 구분 | 언어 | 비고 |
|---|---|---|---|---|---|
| 프로그래머스 42578 · 의상 | PgsDisguise | [의상](https://school.programmers.co.kr/learn/courses/30/lessons/42578) | online | any | 해시 개념 · Lv.2 |
| 프로그래머스 42577 · 전화번호 목록 | PgsPhoneBook | [전화번호 목록](https://school.programmers.co.kr/learn/courses/30/lessons/42577) | online | any | 해시 응용 · Lv.2 |
| 프로그래머스 42626 · 더 맵게 | PgsMoreSpicy | [더 맵게](https://school.programmers.co.kr/learn/courses/30/lessons/42626) | online | any | 힙 개념 · Lv.2 · 힙 직접 구현 |
| 프로그래머스 42628 · 이중우선순위큐 | PgsDoublePriorityQueue | [이중우선순위큐](https://school.programmers.co.kr/learn/courses/30/lessons/42628) | online | any | 힙 응용 · Lv.3 |
| 백준 1655 · 가운데를 말해요 | BojMedianTeller | [가운데를 말해요](https://app.notion.com/p/3c0216e328d180a7b13ffd817c75959f?v=a5d216e328d18233a3258800cd5b8394&source=copy_link) | offline | any | 힙 · 골드2 · 출력 버퍼링 주의 |

</details>

<details>
<summary><b>week-02 (5문제)</b></summary>

| 문제 | 파일명 | 링크 | 구분 | 언어 | 비고 |
|---|---|---|---|---|---|
| 프로그래머스 42746 · 가장 큰 수 | PgsLargestNumber | [가장 큰 수](https://school.programmers.co.kr/learn/courses/30/lessons/42746) | online | any | - |
| 프로그래머스 43238 · 입국심사 | PgsImmigration | [입국심사](https://school.programmers.co.kr/learn/courses/30/lessons/43238) | online | any | - |
| 백준 · 드래곤 앤 던전 | BojDragonAndDungeon | [드래곤 앤 던전](https://www.notion.so/3b8216e328d180ffb42cc824ab0d5cd4?source=copy_link) | online | any | 선택 문제 |
| 백준 · 좌표 압축 | BojCoordCompression | [좌표 압축](https://www.notion.so/3b8216e328d1808fa149d820442afb49?source=copy_link) | online | any | 선택 문제 |
| 백준 2143 · 두 배열의 합 | BojTwoArraysSum | - | offline | any | 답이 int 범위를 넘을 수 있음 |

</details>

<details>
<summary><b>week-01 (3문제, 완료)</b></summary>

| 문제 | 파일명 | 링크 | 구분 | 언어 | 비고 |
|---|---|---|---|---|---|
| 프로그래머스 42584 · 주식가격 | PgsStockPrice | [주식가격](https://school.programmers.co.kr/learn/courses/30/lessons/42584) | online | any | - |
| SWEA 1225 · 암호생성기 | Swea1225 | [암호생성기](https://swexpertacademy.com/main/code/problem/problemDetail.do?contestProbId=AV14uWl6AF0CFAYD) | online | any | - |
| 자체과제 · 링크드리스트 만들기 | MyLinkedList | [링크드리스트](https://app.notion.com/p/insert-delete-find-653216e328d183eb896481ac88d606ae?v=a5d216e328d18233a3258800cd5b8394&source=copy_link) | online | any | 테스트케이스 포함 구현 |

</details>
