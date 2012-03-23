package collabode;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import net.appjet.oui.ExecutionContext;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.junit.Rule;
import org.junit.Test;
import org.osgi.service.prefs.Preferences;

public class ConcurrentCloneTest extends UnitTest {
    
    private final ExecutorService exec = Executors.newCachedThreadPool();
    @Rule public TestProject project = new TestProject("clone-me");
    
    @Test public void testClaims() throws Throwable {
        project.createFile("src/Hello.java", "public class Hello {\n    \n}\n");
        
        class Cloner extends JavaScriptExec {
            
            final String username;
            
            Cloner(String username) throws IOException {
                super(ConcurrentCloneTest.class.getResource("concurrent_clone_test.js"), username);
                this.username = username;
                errors.addLatch(done);
            }
            
            @Override void initialize(ExecutionContext ec) {
                ec.attributes().put("test-username", username);
                ec.attributes().put("test-project", project.get());
            }
        }
        
        Cloner[] cloners = new Cloner[] {
                new Cloner("aa"), new Cloner("bb"), new Cloner("cc"), new Cloner("dd"), new Cloner("ee"), new Cloner("ff"),
                new Cloner("uu"), new Cloner("vv"), new Cloner("ww"), new Cloner("xx"), new Cloner("yy"), new Cloner("zz")
        };
        
        List<Future<Void>> futures = exec.invokeAll(Arrays.asList(cloners));
        
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException ee) {
                throw ee.getCause();
            }
        }
        errors.await();
        
        assertTrue(project.get().exists());
        IWorkspaceRoot root = Workspace.getWorkspace().getRoot();
        for (Cloner cloner : cloners) {
            IProject clone = root.getProject(project.get().getName() + "-" + cloner.username);
            assertTrue(cloner.username + " clone", clone.exists());
            Preferences acl = Workspace.getProjectPrefs(clone, "acl");
            String key = "r." + cloner.username + "/";
            assertArrayEquals(cloner.username + "acls", new String[] { key }, acl.keys());
            assertEquals(cloner.username + " owner", "owner", acl.get(key, null));
        }
    }
}
