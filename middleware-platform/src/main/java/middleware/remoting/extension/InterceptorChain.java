package middleware.remoting.extension;

import java.util.ArrayList;
import java.util.List;

import middleware.remoting.InvocationContext;

/**
 * Cadeia de interceptores ao redor da invocação remota (Fase F).
 */
public final class InterceptorChain {

    private final List<InvocationInterceptor> interceptors;

    public InterceptorChain(List<InvocationInterceptor> interceptors) {
        this.interceptors = List.copyOf(interceptors);
    }

    public static InterceptorChain defaults() {
        List<InvocationInterceptor> list = new ArrayList<>();
        list.add(new LoggingInvocationInterceptor());
        return new InterceptorChain(list);
    }

    public Object execute(InvocationContext ctx, InvocationInterceptor.InvocationChain terminal) throws Exception {
        return executeFrom(ctx, terminal, 0);
    }

    private Object executeFrom(InvocationContext ctx, InvocationInterceptor.InvocationChain terminal, int index)
            throws Exception {
        if (index >= interceptors.size()) {
            return terminal.proceed();
        }
        InvocationInterceptor ic = interceptors.get(index);
        return ic.intercept(ctx, () -> executeFrom(ctx, terminal, index + 1));
    }
}
