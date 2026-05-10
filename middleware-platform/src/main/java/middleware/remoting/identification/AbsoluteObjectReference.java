package middleware.remoting.identification;

/**
 * Referência absoluta para alcançar o objeto remoto (Fase D — Absolute Object Reference).
 */
public record AbsoluteObjectReference(
        String transportProtocol,
        String host,
        int port,
        String serviceKind,
        ObjectId objectId
) {
}
