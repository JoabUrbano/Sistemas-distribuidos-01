package middleware.remoting;

/**
 * Invoker (Fase C): executa invocação abstrata sobre o objeto remoto.
 */
public interface Invoker {
    Object invoke(InvocationContext ctx, String operationName, Object[] arguments) throws Exception;
}
