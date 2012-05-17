import java.util.*;

public class Formatter<L extends Language> {
    
    
    private final L lang;
    
    
    public Formatter(L lang) {
        this.lang = lang;
    }
    
    
    public String format(int count, String thing) {
        return count + " " + lang.pluralize(count, thing);
    }
    
    
    public String format(List<String> things) {
        StringBuilder list = new StringBuilder();
        String[] joiners = lang.joiners(things.size());
        int idx = 0;
        for (Iterator<String> it = things.iterator(); ; ) {
            list.append(it.next());
            if ( ! it.hasNext()) { break; }
            list.append(joiners[idx++]);
        }
        return list.toString();
    }
    
    
}
