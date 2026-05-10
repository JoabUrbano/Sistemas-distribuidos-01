package middleware.remoting;

public final class MarshallingRemotingException extends RemotingException {
    public MarshallingRemotingException(String message) {
        super(message, 400);
    }

    public MarshallingRemotingException(String message, Throwable cause) {
        super(message, cause, 400);
    }
}
