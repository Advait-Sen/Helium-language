package adsen.helium.utils;

/**
 * Custom consumer for 3 parameters, since native Java only has up until {@link java.util.function.BiConsumer}
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {
    void accept(A a, B b, C c);
}
