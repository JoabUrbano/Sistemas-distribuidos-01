package middleware.remoting.invocation;

import java.util.function.Supplier;

import middleware.remoting.Invoker;

/**
 * Fábricas de Invoker para políticas de lifecycle (Fase E).
 */
public final class InvokerFactories {

    private InvokerFactories() {}

    public static Invoker staticInvoker(Object singleton, Class<?> remoteType) {
        AnnotationInvocationRegistry reg = new AnnotationInvocationRegistry(remoteType);
        return new ReflectionInvoker(() -> singleton, reg);
    }

    public static Invoker perRequestInvoker(Supplier<Object> factory, Class<?> remoteType) {
        AnnotationInvocationRegistry reg = new AnnotationInvocationRegistry(remoteType);
        return new ReflectionInvoker(factory, reg);
    }
}
