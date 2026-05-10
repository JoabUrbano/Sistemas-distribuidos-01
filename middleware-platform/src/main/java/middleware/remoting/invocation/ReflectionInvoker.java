package middleware.remoting.invocation;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import middleware.remoting.InvocationContext;
import middleware.remoting.Invoker;

/**
 * Remote Object + Invoker default via reflexão (Fase C).
 */
public final class ReflectionInvoker implements Invoker {

    private final Supplier<Object> targetSupplier;
    private final AnnotationInvocationRegistry registry;

    public ReflectionInvoker(Supplier<Object> targetSupplier, AnnotationInvocationRegistry registry) {
        this.targetSupplier = targetSupplier;
        this.registry = registry;
    }

    @Override
    public Object invoke(InvocationContext ctx, String operationName, Object[] arguments) throws Exception {
        Method m = registry.methodFor(operationName);
        if (m == null) {
            throw new IllegalArgumentException("Operação não registrada: " + operationName);
        }
        Object target = targetSupplier.get();
        return m.invoke(target, arguments);
    }
}
