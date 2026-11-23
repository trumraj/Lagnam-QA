package slack;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shells out to the project's own Maven build to run the Playwright/Cucumber
 * automation suite, the same way a developer would from a terminal.
 */
public class MavenTestRunner {

    // Only allow safe Cucumber tag-expression characters through to the command line.
    private static final Pattern SAFE_TAGS_PATTERN = Pattern.compile("^[A-Za-z0-9@_\\-\\s&|()!]+$");
    private static final Pattern SUMMARY_LINE_PATTERN =
            Pattern.compile("\\d+\\s+(Scenarios|Steps)\\s+\\([^)]*\\)");
    private static final int MAX_OUTPUT_CHARS = 3500;

    private final SlackConfig config;

    public MavenTestRunner(SlackConfig config) {
        this.config = config;
    }

    public Result run(String rawTagsExpression) {
        List<String> command = new ArrayList<>();
        command.add(isWindows() ? "mvn.cmd" : "mvn");
        command.add(config.getMavenGoal());

        String tagsExpression = sanitizeTags(rawTagsExpression);
        if (tagsExpression != null) {
            command.add("-Dcucumber.filter.tags=" + tagsExpression);
        }

        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File(config.getProjectDir()));
            builder.redirectErrorStream(true);

            Process process = builder.start();
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append('\n');
                }
            }

            int exitCode = process.waitFor();
            return new Result(exitCode == 0, exitCode, summarize(output.toString()));
        } catch (Exception e) {
            return new Result(false, -1, "Failed to launch Maven: " + e.getMessage());
        }
    }

    private String sanitizeTags(String rawTagsExpression) {
        if (rawTagsExpression == null || rawTagsExpression.isBlank()) {
            return null;
        }
        String trimmed = rawTagsExpression.trim();
        if (!SAFE_TAGS_PATTERN.matcher(trimmed).matches()) {
            return null;
        }
        return trimmed;
    }

    private String summarize(String fullOutput) {
        StringBuilder summaryLines = new StringBuilder();
        Matcher matcher = SUMMARY_LINE_PATTERN.matcher(fullOutput);
        while (matcher.find()) {
            summaryLines.append(matcher.group()).append('\n');
        }

        if (summaryLines.length() > 0) {
            return summaryLines.toString().trim();
        }

        // Fall back to the tail of the log if no recognizable Cucumber summary was printed.
        if (fullOutput.length() <= MAX_OUTPUT_CHARS) {
            return fullOutput.trim();
        }
        return fullOutput.substring(fullOutput.length() - MAX_OUTPUT_CHARS).trim();
    }

    private boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    public static class Result {
        private final boolean success;
        private final int exitCode;
        private final String summary;

        public Result(boolean success, int exitCode, String summary) {
            this.success = success;
            this.exitCode = exitCode;
            this.summary = summary;
        }

        public boolean isSuccess() {
            return success;
        }

        public int getExitCode() {
            return exitCode;
        }

        public String getSummary() {
            return summary;
        }
    }
}
