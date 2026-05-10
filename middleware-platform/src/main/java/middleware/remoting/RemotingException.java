package middleware.remoting;

/**
 * Erro da camada de remoting (Fase C — Remoting Error), distinto de erro de domínio.
 */
public class RemotingException extends Exception {

    private final int suggestedHttpStatus;

    public RemotingException(String message, int suggestedHttpStatus) {
        super(message);
        this.suggestedHttpStatus = suggestedHttpStatus;
    }

    public RemotingException(String message, Throwable cause, int suggestedHttpStatus) {
        super(message, cause);
        this.suggestedHttpStatus = suggestedHttpStatus;
    }

    public int suggestedHttpStatus() {
        return suggestedHttpStatus;
    }
}
