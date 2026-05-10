package middleware.remoting.lifecycle;

/**
 * Políticas de instância do objeto remoto (Fase E — escolha 2 de 3).
 */
public enum RemoteInstancePolicy {
    /** Uma instância por processo JVM (Static Instance). */
    STATIC_SINGLETON,
    /** Nova instância por invocação HTTP (Per-Request Instance). */
    PER_REQUEST
}
