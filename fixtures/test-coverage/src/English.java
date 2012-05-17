import java.util.Arrays;

public class English implements Language {
    
    
    public String titleize(String lower) {
        return lower.substring(0, 1).toUpperCase() + lower.substring(1);
    }
    
    
    public String pluralize(int count, String singular) {
        return count == 1 ? singular : singular + "s";
    }
    
    
    public String[] joiners(int length) {
        String[] joiners = new String[length-1];
        Arrays.fill(joiners, ", ");
        if (length == 2) {
            joiners[0] = " and ";
        } else if (length > 2) {
            joiners[length-2] = ", and ";
        }
        return joiners;
    }
    
    
}
