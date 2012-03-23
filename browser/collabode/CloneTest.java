package collabode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.apache.http.message.BasicNameValuePair;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebElement;

@BrowserTest.Fixtures("clone")
public class CloneTest extends BrowserTest {
    
    private final ExecutorService exec = Executors.newCachedThreadPool();
    
    @Before public void setUp() throws IOException {
        adminPost(fixture(),
                  new BasicNameValuePair("acl", "1"),
                  new BasicNameValuePair("acl_userid", "anyone"),
                  new BasicNameValuePair("acl_permission", "claim"));
    }
    
    @Test public void testClaims() throws Throwable {
        CollabodeDriver[] drivers = new CollabodeDriver[] {
                connect(), connect(), connect()
        };
        
        final QuietCyclicBarrier barrier = new QuietCyclicBarrier(drivers.length);
        List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
        
        for (final CollabodeDriver driver : drivers) {
            callables.add(new VoidCall() {
                public Void call() throws Exception {
                    driver.get(fixture() + "/src/Hello.java");
                    WebElement content = driver.switchToEditorInner();
                    driver.findEditorLine("println").click();
                    content.sendKeys(" // should be rejected");
                    driver.waitForSync();
                    barrier.await(50, TimeUnit.SECONDS);
                    assertEqualsModWhitespace(initial("Hello.java"), driver.switchToEditorInner().getText());
                    barrier.await(5, TimeUnit.SECONDS);
                    driver.get("clone/" + fixture() + "/src/Hello.java");
                    content = driver.switchToEditorInner();
                    driver.findEditorLine("println").click();
                    content.sendKeys(" // should be allowed");
                    driver.waitForSync();
                    assertEqualsModWhitespace(expected("Hello.java"), driver.switchToEditorInner().getText());
                    return null;
                }
            });
        }
        
        List<Future<Void>> futures = exec.invokeAll(callables);
        
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException ee) {
                throw ee.getCause();
            }
        }
    }
}
