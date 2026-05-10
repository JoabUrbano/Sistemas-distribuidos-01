package middleware.remoting.lifecycle;

import java.util.function.Supplier;

/**
 * Lazy Acquisition genérico (Fase E — recurso obtido na primeira necessidade).
 */
public final class LazyHolder<T> {

    private volatile T value;
    private final Supplier<T> factory;

    public LazyHolder(Supplier<T> factory) {
        this.factory = factory;
    }

    public T get() {
        T v = value;
        if (v == null) {
            synchronized (this) {
                v = value;
                if (v == null) {
                    v = factory.get();
                    value = v;
                }
            }
        }
        return v;
    }
}
