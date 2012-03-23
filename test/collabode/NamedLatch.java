package collabode;

import java.util.concurrent.CountDownLatch;

/**
 * {@link CountDownLatch} with a name.
 */
public class NamedLatch extends CountDownLatch {
    
    public final String name;
    
    public NamedLatch(int count, String name) {
        super(count);
        this.name = name;
    }
    
    @Override public String toString() {
        return "<" + name + ">[Count = " + getCount() + "]";
    }
}
