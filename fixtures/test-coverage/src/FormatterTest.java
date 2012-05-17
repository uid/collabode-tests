import static org.junit.Assert.assertEquals;

import java.util.*;

import org.junit.Test;

public class FormatterTest {
    
    
    public <T> List<T> newList() {
        return new ArrayList<T>();
    }
    
    
    @Test public void testFormatCount() {
        Formatter<?> english = new Formatter<English>(new English());
        assertEquals("0 kiwis", english.format(0, "kiwi"));
        assertEquals("1 kiwi", english.format(1, "kiwi"));
        assertEquals("2 kiwis", english.format(2, "kiwi"));
    }
    
    
    @Test public void testFormatList() {
        Formatter<?> english = new Formatter<English>(new English());
        List<String> things = newList();
        things.add("apple");
        assertEquals("apple", english.format(things));
        things.add("banana");
        assertEquals("apple and banana", english.format(things));
        things.add("cantaloupe");
        assertEquals("apple, banana, and cantaloupe", english.format(things));
    }
    
    
}
