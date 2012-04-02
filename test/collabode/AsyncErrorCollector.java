package collabode;

import static org.junit.Assert.assertTrue;

import java.util.Queue;
import java.util.concurrent.*;

import org.junit.rules.ErrorCollector;

/**
 * Collects latches that must count down for the test to pass.
 */
public class AsyncErrorCollector extends ErrorCollector {
    
    private final Queue<CountDownLatch> latches = new ConcurrentLinkedQueue<CountDownLatch>();
    
    /**
     * Add a latch that must count down for the test to pass.
     */
    public void addLatch(CountDownLatch... latches) {
        for (CountDownLatch latch : latches) {
            this.latches.add(latch);
        }
    }
    
    /**
     * Wait some minimal time for all latches to count down to zero.
     */
    public void await() throws InterruptedException {
        for (CountDownLatch latch : latches) {
            boolean result = latch.await(1, TimeUnit.SECONDS); // get result first
            assertTrue(latch.toString(), result);
        }
    }
    
    @Override protected void verify() throws Throwable {
        await();
        super.verify();
    }
}
