// Sample check - like 'Run code' on Programmers. For full verification run BojTemporaryClassLeaderSubmit.
// Open this file in IntelliJ and press Run (green arrow) to judge your solution.
// Auto-generated and refreshed by the generator - do not edit by hand.
// Korean messages are stored as unicode escapes so this compiles under any source encoding.
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BojTemporaryClassLeaderTest {
    static final String NAME = "BojTemporaryClassLeader";
    static final boolean SAMPLES = true;
    static final double DEFAULT_TIME_LIMIT_SEC = 10.0;
    static final long TERMINATION_GRACE_MILLIS = 2000;
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
        double timeLimitSec = resolveTimeLimit(tcDir, args);
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
            if (!proc.waitFor((long) (timeLimitSec * 1000), TimeUnit.MILLISECONDS)) {
                killProcessTree(proc);
                finishPipes(proc, tOut, tErr);
                System.out.println("  \u274c " + stem + ": \uc2dc\uac04 \ucd08\uacfc (" + timeLimitSec + "\ucd08)");
                continue;
            }
            finishPipes(proc, tOut, tErr);
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

    static double resolveTimeLimit(Path tcDir, String[] args) {
        String override = null;
        if (args.length == 2 && "--time-limit".equals(args[0])) {
            override = args[1];
        } else if (args.length == 1 && args[0].startsWith("--time-limit=")) {
            override = args[0].substring("--time-limit=".length());
        } else if (args.length != 0) {
            exit(2, "[\ucc44\uc810 \ubd88\uac00] \uc0ac\uc6a9\ubc95: --time-limit <\ucd08>");
        }
        if (override != null)
            return parseConfiguredTimeLimit(override, "\uba85\ub839\ud589 --time-limit");

        Path metadata = tcDir.resolve("time_limit.txt");
        if (Files.exists(metadata)) {
            try {
                String value = new String(Files.readAllBytes(metadata), StandardCharsets.UTF_8);
                return parseConfiguredTimeLimit(value, metadata.toString());
            } catch (IOException e) {
                exit(2, "[\ucc44\uc810 \ubd88\uac00] \uc2dc\uac04 \uc81c\ud55c \uc124\uc815 \uc624\ub958: " + metadata
                    + ": \uc77d\uc744 \uc218 \uc5c6\uc74c (" + e.getMessage() + ")");
            }
        }

        String env = System.getenv("JUDGE_TIME_LIMIT_SECONDS");
        if (env != null && !env.trim().isEmpty())
            return parseConfiguredTimeLimit(env, "\ud658\uacbd \ubcc0\uc218 JUDGE_TIME_LIMIT_SECONDS");
        return DEFAULT_TIME_LIMIT_SEC;
    }

    static double parseConfiguredTimeLimit(String raw, String source) {
        String value = raw.trim();
        try {
            double seconds = Double.parseDouble(value);
            if (Double.isFinite(seconds) && seconds > 0) return seconds;
        } catch (NumberFormatException ignored) {
        }
        exit(2, "[\ucc44\uc810 \ubd88\uac00] \uc2dc\uac04 \uc81c\ud55c \uc124\uc815 \uc624\ub958: " + source + ": '" + value
            + "' \u2014 \ucd08 \ub2e8\uc704\uc758 0\ubcf4\ub2e4 \ud070 \uc720\ud55c\ud55c \uc218\uc5ec\uc57c \ud568");
        return DEFAULT_TIME_LIMIT_SEC;
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
        // A malformed solution must not keep the judge JVM alive through an inherited pipe.
        t.setDaemon(true);
        t.start();
        return t;
    }

    static void finishPipes(Process proc, Thread tOut, Thread tErr) throws InterruptedException {
        long deadline = System.nanoTime()
            + TimeUnit.MILLISECONDS.toNanos(TERMINATION_GRACE_MILLIS);
        joinUntil(tOut, deadline);
        joinUntil(tErr, deadline);
        if (tOut.isAlive()) closeAsync(proc.getInputStream());
        if (tErr.isAlive()) closeAsync(proc.getErrorStream());
    }

    static void joinUntil(Thread thread, long deadlineNanos) throws InterruptedException {
        long remaining = deadlineNanos - System.nanoTime();
        if (remaining <= 0) return;
        long millis = TimeUnit.NANOSECONDS.toMillis(remaining);
        int nanos = (int) (remaining - TimeUnit.MILLISECONDS.toNanos(millis));
        thread.join(millis, nanos);
    }

    static void closeAsync(final InputStream stream) {
        Thread closer = new Thread(() -> {
            try {
                stream.close();
            } catch (IOException ignored) {
            }
        });
        // ProcessPipeInputStream.close() may itself wait for an inherited handle.
        closer.setDaemon(true);
        closer.start();
    }

    static void killProcessTree(Process proc) {
        List<ProcessHandle> descendants = new ArrayList<>();
        proc.descendants().forEach(descendants::add);
        Collections.reverse(descendants);
        for (ProcessHandle child : descendants)
            if (child.isAlive()) child.destroyForcibly();
        proc.destroyForcibly();
        try {
            proc.waitFor(TERMINATION_GRACE_MILLIS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        for (ProcessHandle child : descendants)
            if (child.isAlive()) child.destroyForcibly();
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
