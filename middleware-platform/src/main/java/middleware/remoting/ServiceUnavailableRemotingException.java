package middleware.remoting;

public final class ServiceUnavailableRemotingException extends RemotingException {
    public ServiceUnavailableRemotingException(String message) {
        super(message, 503);
    }
}
