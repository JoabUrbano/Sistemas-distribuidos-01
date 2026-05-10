package middleware.remoting.extension;

import middleware.remoting.InvocationContext;

/**
 * Interceptor de exemplo: tempo de processamento (Fase F).
 */
public final class LoggingInvocationInterceptor implements InvocationInterceptor {

    @Override
    public Object intercept(InvocationContext ctx, InvocationChain chain) throws Exception {
        long t0 = System.nanoTime();
        try {
            return chain.proceed();
        } finally {
            long ms = (System.nanoTime() - t0) / 1_000_000L;
        }
    }
}
