package slack;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Verifies that an incoming request genuinely came from Slack, per
 * https://api.slack.com/authentication/verifying-requests-from-slack
 */
public class SlackSignatureVerifier {

    private static final String HMAC_ALGO = "HmacSHA256";
    private static final long MAX_TIMESTAMP_SKEW_SECONDS = 60 * 5;

    private final String signingSecret;

    public SlackSignatureVerifier(String signingSecret) {
        this.signingSecret = signingSecret;
    }

    public boolean isValid(String timestampHeader, String signatureHeader, byte[] rawBody) {
        if (signingSecret == null || signingSecret.isBlank()) {
            return false;
        }
        if (timestampHeader == null || signatureHeader == null) {
            return false;
        }

        try {
            long timestamp = Long.parseLong(timestampHeader.trim());
            long now = System.currentTimeMillis() / 1000L;
            if (Math.abs(now - timestamp) > MAX_TIMESTAMP_SKEW_SECONDS) {
                return false;
            }

            String baseString = "v0:" + timestampHeader + ":" + new String(rawBody, StandardCharsets.UTF_8);

            Mac mac = Mac.getInstance(HMAC_ALGO);
            mac.init(new SecretKeySpec(signingSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO));
            byte[] hash = mac.doFinal(baseString.getBytes(StandardCharsets.UTF_8));
            String computedSignature = "v0=" + toHex(hash);

            return MessageDigest.isEqual(
                    computedSignature.getBytes(StandardCharsets.UTF_8),
                    signatureHeader.trim().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
