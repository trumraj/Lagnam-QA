package slack;

/**
 * Configuration for the Slack "/run-tests" webhook server.
 * All values are sourced from environment variables so the signing secret
 * never has to live in source control.
 */
public class SlackConfig {

    private final String signingSecret;
    private final int port;
    private final String slashCommand;
    private final String projectDir;
    private final String mavenGoal;

    private SlackConfig(String signingSecret, int port, String slashCommand, String projectDir, String mavenGoal) {
        this.signingSecret = signingSecret;
        this.port = port;
        this.slashCommand = slashCommand;
        this.projectDir = projectDir;
        this.mavenGoal = mavenGoal;
    }

    public static SlackConfig fromEnv() {
        String signingSecret = System.getenv("SLACK_SIGNING_SECRET");
        String portEnv = System.getenv("SLACK_SERVER_PORT");
        String slashCommand = System.getenv().getOrDefault("SLACK_SLASH_COMMAND", "/run-tests");
        String projectDir = System.getenv().getOrDefault("SLACK_PROJECT_DIR", System.getProperty("user.dir"));
        String mavenGoal = System.getenv().getOrDefault("SLACK_MAVEN_GOAL", "test");

        int port = 4567;
        if (portEnv != null && !portEnv.isBlank()) {
            port = Integer.parseInt(portEnv.trim());
        }

        return new SlackConfig(signingSecret, port, slashCommand, projectDir, mavenGoal);
    }

    public String getSigningSecret() {
        return signingSecret;
    }

    public int getPort() {
        return port;
    }

    public String getSlashCommand() {
        return slashCommand;
    }

    public String getProjectDir() {
        return projectDir;
    }

    public String getMavenGoal() {
        return mavenGoal;
    }
}
