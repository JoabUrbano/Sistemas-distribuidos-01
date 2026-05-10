package middleware.remoting.invocation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import middleware.remoting.annotations.RemoteOperation;

/**
 * Mapa nome da operação → Method (registro em tempo de execução — Fase B).
 */
public final class AnnotationInvocationRegistry {

    private final Map<String, Method> operations = new HashMap<>();

    public AnnotationInvocationRegistry(Class<?> remoteType) {
        for (Method m : remoteType.getMethods()) {
            RemoteOperation ann = m.getAnnotation(RemoteOperation.class);
            if (ann == null) {
                continue;
            }
            String key = ann.value().isEmpty() ? m.getName().toLowerCase(Locale.ROOT) : ann.value();
            operations.put(key, m);
        }
    }

    public Method methodFor(String operationName) {
        return operations.get(operationName);
    }

    public boolean hasOperation(String operationName) {
        return operations.containsKey(operationName);
    }
}
