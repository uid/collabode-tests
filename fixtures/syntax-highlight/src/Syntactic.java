import java.util.*;

class Syntactic {
    
    static Map staticMember = null;
    
    List instanceMember;
    
    /**
     * Javadoc.
     * @param input the input
     */
    @Annotated public static int staticMethod(String input) {
        System.out.println("String literal" + '.');
        /* Block comment */
        return Enumerated.TWO.ordinal(); // Line comment
    }
    
    public <T extends List> T instanceMethod(Map.Entry<String, T> entry) {
        this.instanceMember.size();
        return entry.getValue();
    }
}

enum Enumerated { ONE, TWO, THREE }

@interface Annotated { }
