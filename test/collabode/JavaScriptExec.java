package collabode;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.appjet.bodylock.BodyLock;
import net.appjet.bodylock.Executable;
import net.appjet.common.util.HttpServletRequestFactory;
import net.appjet.oui.*;

import org.apache.commons.lang.StringUtils;
import org.mortbay.util.IO;

import scala.*;

/**
 * JavaScript executable task.
 */
public abstract class JavaScriptExec implements VoidCall {
    
    private static final Function0<Object> DONE = F.n0pass();
    
    /**
     * Latch initialized with a count of 1 and decremented if and when
     * execution completes.
     */
    public final NamedLatch done;
    
    private final Executable executable;
    private final Map<String, String> headers = new HashMap<String, String>();
    private final Function2<Integer, String, Object> error = new F.n2<Integer, String, Object>() {
        public Object apply(Integer sc, String msg) {
            error(sc, msg);
            return null;
        }
    };
    private final Function0<Object> complete = new F.n0<Object>() {
        public Object apply() {
            done.countDown();
            complete();
            return null;
        }
    };
    
    /**
     * Constructs an executable for the given script.
     */
    JavaScriptExec(URL script, String... name) throws IOException {
        String path = script.getPath();
        String module = path.substring(path.lastIndexOf('/') + 1);
        String js = IO.toString(script.openStream());
        executable = BodyLock.compileString(js, "module " + module, 1);
        String spec = name.length > 0 ? " " + StringUtils.join(name, ", ") : "";
        done = new NamedLatch(1, "Expect completed " + module + spec);
    }
    
    /**
     * Override to initialize the execution context.
     * Default implementation does nothing.
     */
    void initialize(ExecutionContext ec) {
    }
    
    /**
     * Override to handle errors.
     * Default implementation throws an error.
     */
    void error(Integer sc, String msg) {
        throw new AssertionError(msg);
    }
    
    /**
     * Override to handle completion.
     * Default implementation does nothing.
     */
    void complete() {
    }
    
    public Void call() throws Exception {
        RequestWrapper req = new RequestWrapper(HttpServletRequestFactory.createRequest("/", headers, "GET", null));
        ResponseWrapper res = new ResponseWrapper(HttpServletRequestFactory.createResponse());
        ExecutionContext ec = new ExecutionContext(req, res, ScopeReuseManager.getRunner());
        
        initialize(ec);
        
        execution.execute(ec, error, DONE, complete, new Some<Executable>(executable));
        
        return null;
    }

}
