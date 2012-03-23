package collabode.collab;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.ReplaceEdit;
import org.junit.Rule;
import org.junit.Test;

import scala.Function1;
import collabode.*;

public class JavaCommitterTest extends UnitTest {
    
    private static final String junit0 = "junit0";
    
    @Rule public TestProject project = new TestProject("java-committer");
    
    @Test public void testInsertComment() throws Exception {
        String initial = "public class Hello {\n    \n}\n";
        int offset = "public class Hello {\n    ".length();
        String insertion = "// this is a comment";
        String expected = "public class Hello {\n    // this is a comment\n}\n";
        testSingleUserEdits(initial, expected, new ReplaceEdit(offset, 0, insertion));
    }
    
    @Test public void testDeleteComment() throws Exception {
        String initial = "public class Hello {\n    // this is a comment\n}\n";
        int offset = "public class Hello {\n    ".length();
        int length = "// this is a comment".length();
        String expected = "public class Hello {\n    \n}\n";
        testSingleUserEdits(initial, expected, new ReplaceEdit(offset, length, ""));
    }
    
    @Test public void testReplaceComment() throws Exception {
        String initial = "public class Hello {\n    // comment\n}\n";
        int offset = "public class Hello {\n    ".length();
        int length = "// comment".length();
        String insertion = "// shiny new comment";
        String expected = "public class Hello {\n    // shiny new comment\n}\n";
        testSingleUserEdits(initial, expected, new ReplaceEdit(offset, length, insertion));
    }
    
    @Test public void testRewriteComments() throws Exception {
        String initial = "public class Hello {\n    \n    \n}\n";
        int eol2 = "public class Hello {\n    ".length();
        int eol3 = eol2 + "\n    ".length();
        String insertionA = "// Apprehensive";
        String insertionB = "// B";
        String insertionC = "// C";
        String insertionD = "// Dubious";
        String expected = "public class Hello {\n    // C\n    // Dubious\n}\n";
        testSingleUserEdits(initial, expected,
                new ReplaceEdit(eol2, 0, insertionA),
                new ReplaceEdit(eol3 + insertionA.length(), 0, insertionB),
                new ReplaceEdit(eol2, insertionA.length(), insertionC),
                new ReplaceEdit(eol3 + insertionC.length(), insertionB.length(), insertionD));
    }
    
    private void testSingleUserEdits(String initial, String expected, ReplaceEdit... edits) throws CoreException, IOException, BadLocationException, InterruptedException {
        IFile hello = project.createFile("src/Hello.java", initial);
        
        Collab collab = Collab.of(junit0);
        collab.createDocument(junit0, hello, 0, setPadTextFn(1));
        
        PadDocument userDoc = PadDocumentOwner.of(junit0).get(hello.getFullPath().toString());
        CollabDocument collabDoc = collab.get(hello);
        
        final NamedLatch updated = new NamedLatch(1, "Expect doc update");
        final NamedLatch committed = new NamedLatch(1, "Expect doc commit");
        errors.addLatch(updated, committed);
        
        collab.addListener(new CollabListener() {
            public void updated(PadDocument doc) { updated.countDown(); }
            public void committed(CollabDocument doc) { committed.countDown(); }
        });
        
        assertEquals(initial, collabDoc.disk.get());
        
        collabDoc.syncUnionCoordinateEdits(userDoc, 2, edits);
        
        errors.await();
        assertEquals(expected, collabDoc.disk.get());
    }
    
    private Function1<String, Double> setPadTextFn(final int version) {
        final NamedLatch set = new NamedLatch(1, "Expect call to setPadText");
        errors.addLatch(set);
        return new F.n1<String, Double>() {
            public Double apply(String txt) {
                set.countDown();
                return (double)version;
            }
        };
    }
}
