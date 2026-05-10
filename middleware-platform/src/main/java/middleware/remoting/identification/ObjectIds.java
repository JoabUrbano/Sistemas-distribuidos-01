package middleware.remoting.identification;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

/**
 * Geração determinística de {@link ObjectId} quando o cliente não envia um explicitamente.
 */
public final class ObjectIds {

    private ObjectIds() {}

    public static ObjectId deterministic(String host, int port, String serviceKind) {
        String raw = serviceKind + "|" + host.toLowerCase(Locale.ROOT) + "|" + port;
        return ObjectId.of(UUID.nameUUIDFromBytes(raw.getBytes(StandardCharsets.UTF_8)).toString());
    }
}
