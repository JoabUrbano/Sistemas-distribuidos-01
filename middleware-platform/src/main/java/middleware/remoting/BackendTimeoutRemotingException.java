package middleware.remoting;

public final class BackendTimeoutRemotingException extends RemotingException {
    public BackendTimeoutRemotingException(String message) {
        super(message, 504);
    }
}
