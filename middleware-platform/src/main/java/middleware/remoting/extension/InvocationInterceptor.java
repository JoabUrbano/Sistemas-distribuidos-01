package middleware.remoting.extension;

import middleware.remoting.InvocationContext;

/**
 * Invocation Interceptor (Fase F).
 */
public interface InvocationInterceptor {

    Object intercept(InvocationContext ctx, InvocationChain chain) throws Exception;

    @FunctionalInterface
    interface InvocationChain {
        Object proceed() throws Exception;
    }
}
