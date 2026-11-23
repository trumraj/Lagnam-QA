package slack;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Minimal HTTP server that exposes the "/run-tests" Slack slash command.
 *
 * Slack calls this endpoint over HTTPS whenever a user types "/run-tests" in
 * a channel. On receipt we:
 *   1. Verify the request signature (see SlackSignatureVerifier).
 *   2. Immediately acknowledge within Slack's 3-second window.
 *   3. Kick off "mvn test" in the background and post the result back to
 *      Slack's response_url once the suite finishes.
 *
 * Run with:  mvn exec:java -Dexec.mainClass=slack.SlackCommandServer
 * See SLACK_INTEGRATION.md for Slack app setup and environment variables.
 */
public class SlackCommandServer {

    private final SlackConfig config;
    private final SlackSignatureVerifier verifier;
    private final MavenTestRunner testRunner;
    private final SlackResponder responder;
    private final AtomicBoolean runInProgress = new AtomicBoolean(false);

    public SlackCommandServer(SlackConfig config) {
        this.config = config;
        this.verifier = new SlackSignatureVerifier(config.getSigningSecret());
        this.testRunner = new MavenTestRunner(config);
        this.responder = new SlackResponder();
    }

    public static void main(String[] args) throws IOException {
        SlackConfig config = SlackConfig.fromEnv();
        if (config.getSigningSecret() == null || config.getSigningSecret().isBlank()) {
            System.err.println("WARNING: SLACK_SIGNING_SECRET is not set - all requests will be rejected.");
        }

        SlackCommandServer server = new SlackCommandServer(config);
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(config.getPort()), 0);
        httpServer.createContext("/slack/run-tests", server::handle);
        httpServer.setExecutor(null);
        httpServer.start();

        System.out.println("Slack command server listening on port " + config.getPort()
                + " for " + config.getSlashCommand());
    }

    private void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"text\":\"Method not allowed\"}");
            return;
        }

        byte[] rawBody = exchange.getRequestBody().readAllBytes();
        String timestamp = firstHeader(exchange, "X-Slack-Request-Timestamp");
        String signature = firstHeader(exchange, "X-Slack-Signature");

        if (!verifier.isValid(timestamp, signature, rawBody)) {
            sendResponse(exchange, 401, "{\"text\":\"Invalid request signature\"}");
            return;
        }

        Map<String, String> form = parseFormBody(new String(rawBody, StandardCharsets.UTF_8));
        String command = form.get("command");
        if (command == null || !command.equals(config.getSlashCommand())) {
            sendResponse(exchange, 200, jsonMessage("ephemeral", "Unrecognized command."));
            return;
        }

        String responseUrl = form.get("response_url");
        String tagsExpression = form.get("text");
        String userName = form.getOrDefault("user_name", "someone");

        if (!runInProgress.compareAndSet(false, true)) {
            sendResponse(exchange, 200, jsonMessage("ephemeral",
                    "A test run is already in progress - please wait for it to finish."));
            return;
        }

        sendResponse(exchange, 200, jsonMessage("in_channel",
                ":hourglass_flowing_sand: " + userName + " started the automation suite... I'll post results here when it's done."));

        new Thread(() -> runTestsAndReport(responseUrl, tagsExpression)).start();
    }

    private void runTestsAndReport(String responseUrl, String tagsExpression) {
        try {
            MavenTestRunner.Result result = testRunner.run(tagsExpression);
            String icon = result.isSuccess() ? ":white_check_mark:" : ":x:";
            String text = icon + " Test run finished (exit code " + result.getExitCode() + ")\n```"
                    + result.getSummary() + "```";
            responder.postMessage(responseUrl, text, true);
        } finally {
            runInProgress.set(false);
        }
    }

    private String firstHeader(HttpExchange exchange, String name) {
        var values = exchange.getRequestHeaders().get(name);
        return (values == null || values.isEmpty()) ? null : values.get(0);
    }

    private Map<String, String> parseFormBody(String body) {
        Map<String, String> result = new HashMap<>();
        for (String pair : body.split("&")) {
            if (pair.isBlank()) {
                continue;
            }
            String[] parts = pair.split("=", 2);
            String key = urlDecode(parts[0]);
            String value = parts.length > 1 ? urlDecode(parts[1]) : "";
            result.put(key, value);
        }
        return result;
    }

    private String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private String jsonMessage(String responseType, String text) {
        String escaped = text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
        return "{\"response_type\":\"" + responseType + "\",\"text\":\"" + escaped + "\"}";
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String jsonBody) throws IOException {
        byte[] bytes = jsonBody.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
