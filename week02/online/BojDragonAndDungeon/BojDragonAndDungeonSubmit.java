// 검증 채점 — 프로그래머스의 '제출 후 채점'에 해당. 예제만 돌리려면 BojDragonAndDungeonTest 실행.
// IntelliJ에서 이 파일을 열고 Run(초록 화살표)을 누르면 채점된다.
// 이 파일은 자동 생성·갱신되므로 직접 수정하지 않는다.
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BojDragonAndDungeonSubmit {
    static final String NAME = "BojDragonAndDungeon";
    static final boolean SAMPLES = false;
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
