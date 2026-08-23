"""PROBLEMS.md를 파싱해 weekNN/{online,offline}/에 문제별 풀이 파일을 생성한다.

사용법:
    python3 generate_problem_files.py <PROBLEMS.md> [저장소 루트]
        [--lang java|python|both] [--runner py|java|both]

- --lang   : 풀이 스켈레톤 언어. both 면 .java 와 .py 둘 다 (main 브랜치용).
- --runner : 백준 폴더에 넣을 채점 실행 파일.
             py   = test.py / submit.py           (VSCode 등, 파이썬 필요)
             java = <문제명>Test.java / <문제명>Submit.java (IntelliJ 등, 파이썬 불필요)
             both = 둘 다
- 표의 "언어" 칸이 java 인 문제(B형 등)는 --lang 과 무관하게 자바만 생성한다.
- 이미 존재하는 풀이 파일은 절대 덮어쓰지 않는다. 실행 파일은 내용이 바뀌면 갱신한다.
"""
import argparse
import re
from pathlib import Path

# ──────────────────────────── 풀이 스켈레톤 ────────────────────────────

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

# ──────────────────────── 파이썬 채점 실행 파일 ────────────────────────

PY_RUNNER_TEMPLATE = '''"""{doc}
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

PY_TEST_RUNNER = PY_RUNNER_TEMPLATE.format(
    doc="""예제 채점 — 프로그래머스의 '코드 실행'에 해당.

등록된 예제(testcases/주차/문제/samples/)만 빠르게 돌려본다.
실행: python test.py        전체 검증은 python submit.py""",
    extra=', "--set", "samples"',
)

PY_SUBMIT_RUNNER = PY_RUNNER_TEMPLATE.format(
    doc="""검증 채점 — 프로그래머스의 '제출 후 채점'에 해당.

전체 테스트케이스(대형 성능 케이스 포함)로 채점한다.
실행: python submit.py      예제만 돌리려면 python test.py""",
    extra="",
)

# ───────────────────────── 자바 채점 실행 파일 ─────────────────────────
# 파이썬 없이 순수 자바로 채점한다. IntelliJ에서 Run 버튼으로 실행.
# .format 대신 .replace 를 쓰므로 중괄호 이스케이프가 필요 없다.
# 주의: 이 템플릿의 한글·이모지는 java_ascii() 가 유니코드 이스케이프로 바꿔서 쓴다.
#       javac 기본 인코딩이 MS949 인 환경(Windows + JDK 17 이하)에서도 컴파일되게 하기 위함.
#       주석은 이스케이프하면 읽을 수 없으므로 영어(ASCII)로 쓴다.

JAVA_RUNNER_TEMPLATE = r'''// __DOC__
// Open this file in IntelliJ and press Run (green arrow) to judge your solution.
// Auto-generated and refreshed by the generator - do not edit by hand.
// Korean messages are stored as unicode escapes so this compiles under any source encoding.
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class __CLASS__ {
    static final String NAME = "__NAME__";
    static final boolean SAMPLES = __SAMPLES__;
    static final double TIME_LIMIT_SEC = 10.0;
    static final int TRUNCATE = 800;

    public static void main(String[] args) throws Exception {
        Path root = findRoot();
        if (root == null)
            exit(2, "[채점 불가] testcases 폴더를 찾지 못함 — 저장소 루트를 프로젝트로 열었는지 확인");
        Path tcDir = null;
        try (DirectoryStream<Path> weeks = Files.newDirectoryStream(root.resolve("testcases"))) {
            for (Path w : weeks) {
                Path cand = w.resolve(NAME);
                if (Files.isDirectory(cand)) { tcDir = cand; break; }
            }
        }
        if (tcDir == null)
            exit(2, "[채점 불가] testcases/주차/" + NAME + " 폴더가 없음");
        Path caseDir = SAMPLES ? tcDir.resolve("samples") : tcDir;
        List<Path> cases = new ArrayList<>();
        if (Files.isDirectory(caseDir))
            try (DirectoryStream<Path> s = Files.newDirectoryStream(caseDir, "*.in")) {
                for (Path p : s) cases.add(p);
            }
        Collections.sort(cases);
        String label = SAMPLES ? "예제" : "검증";
        if (cases.isEmpty())
            exit(2, SAMPLES
                ? "[채점 불가] 등록된 예제가 없음 — " + caseDir + " 에 01.in / 01.out 쌍을 추가"
                : "[채점 불가] " + caseDir + " 안에 *.in 파일이 없음");
        System.out.println("[" + label + " 채점] " + NAME + " — 케이스 " + cases.size() + "개");

        String javaBin = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
        String classpath = System.getProperty("java.class.path");
        int passed = 0, judged = 0;
        for (Path in : cases) {
            String fn = in.getFileName().toString();
            String stem = fn.substring(0, fn.length() - 3);
            Path outFile = in.resolveSibling(stem + ".out");
            if (!Files.exists(outFile)) {
                System.out.println("  [warn] " + fn + ": 짝이 되는 .out 파일이 없어 건너뜀");
                continue;
            }
            judged++;
            ProcessBuilder pb = new ProcessBuilder(
                javaBin, "-Dfile.encoding=UTF-8", "-cp", classpath, NAME);
            pb.redirectInput(in.toFile());
            Process proc = pb.start();
            ByteArrayOutputStream outBuf = new ByteArrayOutputStream();
            ByteArrayOutputStream errBuf = new ByteArrayOutputStream();
            Thread tOut = pipe(proc.getInputStream(), outBuf);
            Thread tErr = pipe(proc.getErrorStream(), errBuf);
            if (!proc.waitFor((long) (TIME_LIMIT_SEC * 1000), TimeUnit.MILLISECONDS)) {
                proc.destroyForcibly();
                System.out.println("  ❌ " + stem + ": 시간 초과 (" + TIME_LIMIT_SEC + "초)");
                continue;
            }
            tOut.join();
            tErr.join();
            if (proc.exitValue() != 0) {
                System.out.println("  ❌ " + stem + ": 런타임 에러");
                System.out.println(indent(clip(errBuf.toString("UTF-8"))));
                continue;
            }
            String expected = normalize(new String(Files.readAllBytes(outFile), "UTF-8"));
            String actual = normalize(outBuf.toString("UTF-8"));
            if (expected.equals(actual)) {
                passed++;
                System.out.println("  ✅ " + stem + ": 통과");
            } else {
                System.out.println("  ❌ " + stem + ": 오답");
                System.out.println("     [기대]");
                System.out.println(indent(clip(expected)));
                System.out.println("     [출력]");
                System.out.println(indent(clip(actual)));
            }
        }
        System.out.println();
        if (judged == 0)
            exit(2, "[채점 불가] 유효한 테스트케이스 쌍(.in/.out)이 없음");
        if (passed == judged)
            exit(0, "🎉 " + NAME + " [" + label + "]: " + passed + "/" + judged + " 전체 통과");
        exit(1, "💥 " + NAME + " [" + label + "]: " + passed + "/" + judged + " 통과");
    }

    static Path findRoot() {
        for (Path p = Paths.get("").toAbsolutePath(); p != null; p = p.getParent())
            if (Files.isDirectory(p.resolve("testcases"))) return p;
        return null;
    }

    static Thread pipe(final InputStream src, final ByteArrayOutputStream dst) {
        Thread t = new Thread(() -> {
            try {
                byte[] buf = new byte[8192];
                int n;
                while ((n = src.read(buf)) != -1) dst.write(buf, 0, n);
            } catch (IOException ignored) {
            }
        });
        t.start();
        return t;
    }

    static String normalize(String s) {
        String[] lines = s.trim().split("\r?\n", -1);
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) b.append('\n');
            b.append(lines[i].replaceAll("\\s+$", ""));
        }
        return b.toString();
    }

    static String clip(String s) {
        s = s.trim();
        return s.length() <= TRUNCATE ? s : s.substring(0, TRUNCATE) + "\n... (생략)";
    }

    static String indent(String s) {
        return "     " + s.replace("\n", "\n     ");
    }

    static void exit(int code, String msg) {
        System.out.println(msg);
        System.exit(code);
    }
}
'''


def java_ascii(s: str) -> str:
    """비ASCII 문자를 자바 유니코드 이스케이프(백슬래시-u + 16진수 4자리)로 바꾼다.

    javac 는 소스 인코딩을 적용하기 전에 유니코드 이스케이프를 먼저 해석하므로,
    이렇게 쓴 파일은 기본 인코딩이 MS949 든 UTF-8 이든 똑같이 컴파일되고
    실행 시 메시지는 원래 한글·이모지로 출력된다. BMP 밖 문자(이모지 일부)는
    서로게이트 쌍 두 개로 쓴다.
    """
    out = []
    for ch in s:
        cp = ord(ch)
        if cp < 0x80:
            out.append(ch)
        elif cp <= 0xFFFF:
            out.append(f"\\u{cp:04x}")
        else:
            cp -= 0x10000
            out.append(f"\\u{0xD800 + (cp >> 10):04x}\\u{0xDC00 + (cp & 0x3FF):04x}")
    return "".join(out)


def java_runner(name: str, samples: bool):
    """(파일명, 내용) — 인텔리제이용 자바 실행 파일. 내용은 순수 ASCII."""
    cls = name + ("Test" if samples else "Submit")
    doc = (
        f"Sample check - like 'Run code' on Programmers. For full verification run {name}Submit."
        if samples
        else f"Full verification - like 'Submit' on Programmers. To run samples only, run {name}Test."
    )
    content = (
        JAVA_RUNNER_TEMPLATE
        .replace("__CLASS__", cls)
        .replace("__NAME__", name)
        .replace("__SAMPLES__", "true" if samples else "false")
        .replace("__DOC__", doc)
    )
    return f"{cls}.java", java_ascii(content)


PY_RUNNERS = [("test.py", PY_TEST_RUNNER), ("submit.py", PY_SUBMIT_RUNNER)]


def parse_problems(problems_path: Path):
    """PROBLEMS.md → [(week, title, name, url, section, lang_only)] 목록.

    표 칼럼: 문제 | 파일명 | 링크 | 구분 | 언어 | 비고
    - 구분: online(기본) / offline
    - 언어: any(기본) / java  ← java 면 파이썬 브랜치에도 자바 파일만 생성 (B형 등)
    """
    problems = []
    week = None
    header_idx = {}  # 헤더 행에서 읽은 칼럼 위치 (구분/언어 칼럼이 없는 옛 표도 지원)
    for line in problems_path.read_text(encoding="utf-8").splitlines():
        stripped = line.strip()

        # "## week-01" 또는 "<summary>week-01 ..." 형태의 주차 구분
        # 제목에 "완료"가 있으면 그 주차는 파일 생성 대상에서 제외한다
        if stripped.startswith(("#", "<summary")):
            m = re.search(r"week[- ]?(\d+)", stripped, re.IGNORECASE)
            week = int(m.group(1)) if m and "완료" not in stripped else None
            header_idx = {}
            continue

        if week is None or not stripped.startswith("|"):
            continue

        cells = [c.strip() for c in stripped.strip("|").split("|")]
        if len(cells) < 3:
            continue

        # 헤더 행: 칼럼명 → 위치 기록
        if cells[0] == "문제":
            header_idx = {c: i for i, c in enumerate(cells)}
            continue
        # 구분선 행
        if not (set(cells[0]) - set("-: ")):
            continue

        title, name = cells[0], cells[1]
        if not re.fullmatch(r"[A-Z][A-Za-z0-9]*", name):
            print(f"[skip] week{week:02d} '{title}': 파일명 '{name}'이 PascalCase가 아님")
            continue

        def col(key, default=""):
            i = header_idx.get(key)
            return cells[i] if i is not None and i < len(cells) else default

        url_match = re.search(r"https?://\S+?(?=\)|$)", col("링크", cells[2]))
        url = url_match.group(0) if url_match else None
        section = "offline" if col("구분").lower().startswith("off") else "online"
        lang_only = "java" if col("언어").lower().startswith("java") else None
        problems.append((week, title, name, url, section, lang_only))
    return problems


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("problems", help="PROBLEMS.md 경로")
    parser.add_argument("root", nargs="?", default=".", help="저장소 루트")
    parser.add_argument("--lang", choices=["java", "python", "both"], default="java")
    parser.add_argument("--runner", choices=["py", "java", "both"], default="py")
    args = parser.parse_args()
    root = Path(args.root)

    created, removed = [], []

    for week, title, name, url, section, lang_only in parse_problems(Path(args.problems)):
        is_boj = name.startswith("Boj")
        week_dir = root / f"week{week:02d}"
        base = week_dir / section
        folder = base / name if is_boj else base

        # 이 문제에 만들 언어 목록 (표의 "언어=java" 는 브랜치 설정보다 우선)
        if lang_only == "java":
            langs = ["java"]
        elif args.lang == "both":
            langs = ["java", "python"]
        else:
            langs = [args.lang]

        # ── 풀이 스켈레톤 ──
        # 주차 폴더 어딘가에 같은 언어의 풀이 파일이 이미 있으면 건너뜀
        # (위치를 옮겼거나 예전 구조로 생성된 경우에도 중복 생성 방지)
        # 단일 언어 모드에서는 다른 언어 파일이 있어도 건너뜀 (기존 동작 유지)
        exists_java = week_dir.exists() and any(week_dir.rglob(f"{name}.java"))
        exists_py = week_dir.exists() and any(week_dir.rglob(f"{name}.py"))
        for lang in langs:
            if lang == "java":
                if exists_java or (args.lang != "both" and exists_py):
                    continue
                template = BOJ_TEMPLATE if is_boj else FILE_TEMPLATE
                header = f"// {title}\n" + (f"// {url}\n" if url else "")
                target = folder / f"{name}.java"
            else:
                if exists_py or (args.lang != "both" and exists_java):
                    continue
                template = PY_BOJ_TEMPLATE if is_boj else PY_FILE_TEMPLATE
                header = f"# {title}\n" + (f"# {url}\n" if url else "")
                target = folder / f"{name}.py"
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(template.format(header=header, name=name), encoding="utf-8")
            created.append(target)

        # ── 백준 폴더의 채점 실행 파일 ──
        # 없으면 만들고, 내용이 낡았으면 갱신하고, 선택되지 않은 스타일의 파일은 정리한다
        if not (is_boj and folder.exists()):
            continue
        want = []
        if args.runner in ("py", "both"):
            want += PY_RUNNERS
        if args.runner in ("java", "both"):
            want += [java_runner(name, True), java_runner(name, False)]
        want_names = {fname for fname, _ in want}
        for fname, content in want:
            runner = folder / fname
            if not runner.exists() or runner.read_text(encoding="utf-8") != content:
                runner.write_text(content, encoding="utf-8")
                created.append(runner)
        for fname in ("test.py", "submit.py", f"{name}Test.java", f"{name}Submit.java"):
            if fname not in want_names and (folder / fname).exists():
                (folder / fname).unlink()
                removed.append(folder / fname)

    if created:
        print("생성·갱신된 파일:")
        for path in created:
            print(f"  {path}")
    if removed:
        print("정리(삭제)된 파일:")
        for path in removed:
            print(f"  {path}")
    if not created and not removed:
        print("생성할 새 파일 없음")


if __name__ == "__main__":
    main()
