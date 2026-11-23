package slack;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Posts delayed/follow-up messages back to Slack via the response_url
 * that Slack includes in every slash-command payload.
 */
public class SlackResponder {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public void postMessage(String responseUrl, String text, boolean inChannel) {
        if (responseUrl == null || responseUrl.isBlank()) {
            return;
        }
        String responseType = inChannel ? "in_channel" : "ephemeral";
        String json = "{\"response_type\":\"" + responseType + "\",\"text\":\"" + escapeJson(text) + "\"}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(responseUrl))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            System.err.println("Failed to post result back to Slack: " + e.getMessage());
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}
