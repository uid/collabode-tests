package collabode.collab;

import static org.junit.Assert.assertEquals;

import org.eclipse.text.edits.ReplaceEdit;
import org.junit.Test;

import collabode.collab.CoordinateMap.UL;

public class CoordinateMapTest {
    
    public CoordinateMap map = new CoordinateMap();
    
    @Test public void testUnionOnlyDelete() {
        map.unionOnly(new ReplaceEdit(20, 17, ""));
        testPairs(
                new UL[] { ul(19, 19), /* missing */           ul(20, 37), ul(21, 38), ul(22, 39) },
                new UL[] { ul(19, 19), ul(20, 20), ul(20, 21), ul(20, 37), ul(21, 38), ul(22, 39) }
        );
    }
    
    @Test public void testUnionOnlyInsert() {
        map.unionOnly(new ReplaceEdit(10, 0, "-seven-"));
        testPairs(
                new UL[] { ul(9, 9), ul(10, 10), ul(11, 10), ul(17, 10), ul(18, 11) },
                new UL[] { ul(9, 9), /* missing */           ul(17, 10), ul(18, 11) }
        );
    }
    
    @Test public void testUnionOnlyChange() {
        map.unionOnly(new ReplaceEdit(5, 7, "five+four"));
        testPairs(
                new UL[] { ul(4, 4), /* missing */ ul(5, 12), ul(6, 12), ul(14, 12), ul(15, 13) },
                new UL[] { ul(4, 4), ul(5, 5), ul(5, 11), /* missing */  ul(14, 12), ul(15, 13) }
        );
    }
    
    private static UL ul(int union, int local) {
        return new UL(union, local);
    }
    
    private void testPairs(UL[] unionToLocal, UL[] localToUnion) {
        for (UL pair : unionToLocal) {
            assertEquals(pair.union + " unionToLocal", pair.local, map.unionToLocal(pair.union));
        }
        for (UL pair : localToUnion) {
            assertEquals(pair.local + " localToUnion", pair.union, map.localToUnion(pair.local));
        }
    }
}
