package wtf.choco.locksecurity.util;

import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Conditional<T> {

    private final T object;

    public Conditional(boolean condition, Supplier<T> supplier) {
        this.object = (condition) ? supplier.get() : null;
    }

    public boolean testIfPresent(Predicate<T> predicate) {
        return isPresent() && predicate.test(get());
    }

    public boolean isPresent() {
        return object != null;
    }

    public T get() {
        return object;
    }

}
