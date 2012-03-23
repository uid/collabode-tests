package collabode;

import java.util.concurrent.Callable;

/**
 * A task that returns no result.
 * Can be used in cases where the compiler rejects parameterized type
 * {@code Callable<Void>}.
 */
public interface VoidCall extends Callable<Void> {
}
