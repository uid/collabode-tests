package collabode;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class QuietCyclicBarrier extends CyclicBarrier {
    
    public QuietCyclicBarrier(int parties) {
        super(parties);
    }
    
    @Override public int await() {
        try {
            return super.await();
        } catch (InterruptedException ie) {
            throw new Error(ie);
        } catch (BrokenBarrierException bbe) {
            throw new Error(bbe);
        }
    }
}
