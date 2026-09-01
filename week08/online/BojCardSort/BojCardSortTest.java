// Sample check - like 'Run code' on Programmers. For full verification run BojCardSortSubmit.
// Open this file in IntelliJ and press Run (green arrow) to judge your solution.
// Auto-generated and refreshed by the generator - do not edit by hand.
// Korean messages are stored as unicode escapes so this compiles under any source encoding.
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BojCardSortTest {
    static final String NAME = "BojCardSort";
    static final boolean SAMPLES = true;
    static final double TIME_LIMIT_SEC = 10.0;
    static final int TRUNCATE = 800;

    public static void main(String[] args) throws Exception {
        Path root = findRoot();
        if (root == null)
            exit(2, "[\ucc44\uc810 \ubd88\uac00] testcases \ud3f4\ub354\ub97c \ucc3e\uc9c0 \ubabb\ud568 \u2014 \uc800\uc7a5\uc18c \ub8e8\ud2b8\ub97c \ud504\ub85c\uc81d\ud2b8\ub85c \uc5f4\uc5c8\ub294\uc9c0 \ud655\uc778");
        Path tcDir = null;
        try (DirectoryStream<Path> weeks = Files.newDirectoryStream(root.resolve("testcases"))) {
            for (Path w : weeks) {
                Path cand = w.resolve(NAME);
                if (Files.isDirectory(cand)) { tcDir = cand; break; }
            }
        }
        if (tcDir == null)
            exit(2, "[\ucc44\uc810 \ubd88\uac00] testcases/\uc8fc\ucc28/" + NAME + " \ud3f4\ub354\uac00 \uc5c6\uc74c");
        Path caseDir = SAMPLES ? tcDir.resolve("samples") : tcDir;
        List<Path> cases = new ArrayList<>();
        if (Files.isDirectory(caseDir))
            try (DirectoryStream<Path> s = Files.newDirectoryStream(caseDir, "*.in")) {
                for (Path p : s) cases.add(p);
            }
        Collections.sort(cases);
        String label = SAMPLES ? "\uc608\uc81c" : "\uac80\uc99d";
        if (cases.isEmpty())
            exit(2, SAMPLES
                ? "[\ucc44\uc810 \ubd88\uac00] \ub4f1\ub85d\ub41c \uc608\uc81c\uac00 \uc5c6\uc74c \u2014 " + caseDir + " \uc5d0 01.in / 01.out \uc30d\uc744 \ucd94\uac00"
                : "[\ucc44\uc810 \ubd88\uac00] " + caseDir + " \uc548\uc5d0 *.in \ud30c\uc77c\uc774 \uc5c6\uc74c");
        System.out.println("[" + label + " \ucc44\uc810] " + NAME + " \u2014 \ucf00\uc774\uc2a4 " + cases.size() + "\uac1c");

        String javaBin = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
        String classpath = System.getProperty("java.class.path");
        int passed = 0, judged = 0;
        for (Path in : cases) {
            String fn = in.getFileName().toString();
            String stem = fn.substring(0, fn.length() - 3);
            Path outFile = in.resolveSibling(stem + ".out");
            if (!Files.exists(outFile)) {
                System.out.println("  [warn] " + fn + ": \uc9dd\uc774 \ub418\ub294 .out \ud30c\uc77c\uc774 \uc5c6\uc5b4 \uac74\ub108\ub700");
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
                System.out.println("  \u274c " + stem + ": \uc2dc\uac04 \ucd08\uacfc (" + TIME_LIMIT_SEC + "\ucd08)");
                continue;
            }
            tOut.join();
            tErr.join();
            if (proc.exitValue() != 0) {
                System.out.println("  \u274c " + stem + ": \ub7f0\ud0c0\uc784 \uc5d0\ub7ec");
                System.out.println(indent(clip(errBuf.toString("UTF-8"))));
                continue;
            }
            String expected = normalize(new String(Files.readAllBytes(outFile), "UTF-8"));
            String actual = normalize(outBuf.toString("UTF-8"));
            if (expected.equals(actual)) {
                passed++;
                System.out.println("  \u2705 " + stem + ": \ud1b5\uacfc");
            } else {
                System.out.println("  \u274c " + stem + ": \uc624\ub2f5");
                System.out.println("     [\uae30\ub300]");
                System.out.println(indent(clip(expected)));
                System.out.println("     [\ucd9c\ub825]");
                System.out.println(indent(clip(actual)));
            }
        }
        System.out.println();
        if (judged == 0)
            exit(2, "[\ucc44\uc810 \ubd88\uac00] \uc720\ud6a8\ud55c \ud14c\uc2a4\ud2b8\ucf00\uc774\uc2a4 \uc30d(.in/.out)\uc774 \uc5c6\uc74c");
        if (passed == judged)
            exit(0, "\ud83c\udf89 " + NAME + " [" + label + "]: " + passed + "/" + judged + " \uc804\uccb4 \ud1b5\uacfc");
        exit(1, "\ud83d\udca5 " + NAME + " [" + label + "]: " + passed + "/" + judged + " \ud1b5\uacfc");
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
        return s.length() <= TRUNCATE ? s : s.substring(0, TRUNCATE) + "\n... (\uc0dd\ub7b5)";
    }

    static String indent(String s) {
        return "     " + s.replace("\n", "\n     ");
    }

    static void exit(int code, String msg) {
        System.out.println(msg);
        System.exit(code);
    }
}
