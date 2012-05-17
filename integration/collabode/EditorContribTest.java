package collabode;

import java.io.IOException;
import java.util.*;

import net.appjet.oui.ExecutionContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.mozilla.javascript.ScriptableObject;

@SuppressWarnings("serial")
public class EditorContribTest {
    
    @Rule public TestName name = new TestName();
    
    @Test public void testPadChangeEmpty() throws IOException, Exception {
        new ContribTestExec("testPadChange", new HashMap<String, Object>() {
            {
                put("initial",   "");
                put("revisions", new String[] { "Z:1>0$", "Z:1>0$" });
                put("intervals", new String[] { "0/1", "-5/1", "-5/2", "1/2", "0/2" });
                put("0/1",  "Z:1>0$");
                put("-5/1", "Z:1>0$");
                put("-5/2", "Z:1>0$");
                put("1/2",  "Z:1>0$");
                put("0/2",  "Z:1>0$");
            }
        }).call();
    }
    
    @Test public void testPadChangeNoAttribs() throws IOException, Exception {
        new ContribTestExec("testPadChange", new HashMap<String, Object>() {
            {
                put("initial",   "");
                put("revisions", new String[] { "Z:1>5+5$hello", "Z:6>8=5|1+1+7$\ngoodbye" });
                put("intervals", new String[] { "0/1", "1/2", "0/2" });
                put("0/1",  "Z:1>5+5$hello");
                put("-1/2", "Z:1>d|1+6+7$hello\ngoodbye");
                put("1/2",  "Z:6>8=5|1+1+7$\ngoodbye");
                put("-1/1", "Z:1>5+5$hello");
                put("0/2",  "Z:1>d|1+6+7$hello\ngoodbye");
                put("0/3",  "Z:1>d|1+6+7$hello\ngoodbye");
                put("-1/2", "Z:1>d|1+6+7$hello\ngoodbye");
            }
        }).call();
    }
    
    @Test public void testPadChangeATextEmpty() throws IOException, Exception {
        new ContribTestExec("testPadChangeAText", new HashMap<String, String>() {
            {
                put("startText",    "\n");
                put("startAttribs", "|1+1");
                put("endText",      "\n");
                put("endAttribs",   "|1+1");
                put("changeset",    "Z:1>0$");
                put("finalText",    "\n");
                put("finalAttribs", "|1+1");
            }
        }).call();
    }
    
    @Test public void testPadChangeATextNoAttribs() throws IOException, Exception {
        new ContribTestExec("testPadChangeAText", new HashMap<String, String>() {
            {
                put("startText",    "abc\ndef\n");
                put("startAttribs", "|2+8");
                put("endText",      "ab\ndefg\n");
                put("endAttribs",   "|2+8");
                put("changeset",    "Z:8>0=2-1|1=1=3+1$g");
                put("finalText",    "abc\ndefg\n");
                put("finalAttribs", "+2*DEL+1|1+1+3*INS+1|1+1");
            }
        }).call();
    }
    
    @Test public void testPadChangeATextAttribs() throws IOException, Exception {
        new ContribTestExec("testPadChangeAText", new HashMap<String, String>() {
            {
                put("startText",    "abc\ndef\n");
                put("startAttribs", "*a|1+4*b|1+4");
                put("endText",      "ab\ndefg\n");
                put("endAttribs",   "*a|1+3*b|1+5");
                put("changeset",    "Z:8>0*a=2-1*a|1=1*b=3*b+1$g");
                put("finalText",    "abc\ndefg\n");
                put("finalAttribs", "*a+2*DEL+1*a|1+1*b+3*b*INS+1*b|1+1"); // XXX deleted text should have start attribs
            }
        }).call();
    }
    
    class ContribTestExec extends JavaScriptExec {
        
        private final String function;
        private final Map<String, ?> data;
        
        ContribTestExec(String function, Map<String, ?> data) throws IOException {
            super(EditorContribTest.class.getResource("editor_contrib_test.js"), name.getMethodName());
            this.function = function;
            this.data = data;
        }
        
        @Override void initialize(ExecutionContext ec) {
            ScriptableObject.putProperty(ec.runner().globalScope(), "testPadId", name.getMethodName());
            ScriptableObject.putProperty(ec.runner().globalScope(), "testFunctionName", function);
            ScriptableObject.putProperty(ec.runner().globalScope(), "testData", data);
        }
    }
}
