package middleware.remoting.identification;

/**
 * Identificador lógico estável do objeto remoto (Fase D — Object Id).
 */
public record ObjectId(String value) {

    public static ObjectId of(String value) {
        return new ObjectId(value);
    }
}
