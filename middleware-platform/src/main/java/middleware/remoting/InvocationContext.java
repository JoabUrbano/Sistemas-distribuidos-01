package middleware.remoting;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contexto que atravessa SRH → interceptores → Invoker (Fase F).
 */
public final class InvocationContext {

    private final String correlationId;
    private final Instant startedAt;
    private final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

    private InvocationContext(String correlationId, Instant startedAt) {
        this.correlationId = correlationId;
        this.startedAt = startedAt;
    }

    public static InvocationContext newCorrelation() {
        return new InvocationContext(UUID.randomUUID().toString(), Instant.now());
    }

    public String correlationId() {
        return correlationId;
    }

    public Instant startedAt() {
        return startedAt;
    }

    public void put(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object v = attributes.get(key);
        if (v == null || !type.isInstance(v)) {
            return null;
        }
        return (T) v;
    }
}
