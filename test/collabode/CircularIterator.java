package collabode;

import java.util.*;

public class CircularIterator<E> implements Iterator<E> {
    
    private final List<E> list;
    private Iterator<E> it;
    
    public CircularIterator(List<E> list) {
        this.list = list;
        it = list.iterator();
    }
    
    public CircularIterator(E... array) {
        this(Arrays.asList(array));
    }
    
    public boolean hasNext() {
        return ! list.isEmpty();
    }
    
    public E next() {
        if ( ! it.hasNext()) {
            it = list.iterator();
        }
        return it.next();
    }
    
    public void remove() {
        it.remove();
    }
}
