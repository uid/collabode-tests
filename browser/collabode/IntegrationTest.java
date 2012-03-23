package collabode;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@BrowserTest.Fixtures("integration")
public class IntegrationTest extends BrowserTest {
    
    private final ExecutorService exec = Executors.newCachedThreadPool();
    private QuietCyclicBarrier barrier;
    
    @Test public void testUserMutualDepSingleFile() throws Throwable {
        barrier = new QuietCyclicBarrier(2);
        
        List<Future<Void>> futures = exec.invokeAll(Arrays.asList(new VoidCall[] {
                new UserMutualDep("Hello", 1, "foo", "bar", 2),
                new UserMutualDep("Hello", 3, "bar", "foo", 2)
        }));
        
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException ee) {
                throw ee.getCause();
            }
        }
    }
    
    @Test public void testUserMutualDepMultiFile() throws Throwable {
        barrier = new QuietCyclicBarrier(2);
        
        List<Future<Void>> futures = exec.invokeAll(Arrays.asList(new VoidCall[] {
                new UserMutualDep("Hello", 2, "foo", "Greeting.bar", 1),
                new UserMutualDep("Greeting", 2, "bar", "Hello.foo", 1)
        }));
        
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException ee) {
                throw ee.getCause();
            }
        }
    }
    
    class UserMutualDep implements VoidCall {
        final String clazz, declare, call;
        final int offset, edits;
        UserMutualDep(String clazz, int offset, String declare, String call, int edits) {
            this.clazz = clazz;
            this.offset = offset;
            this.declare = declare;
            this.call = call;
            this.edits = edits;
        }
        public Void call() throws Exception {
            CollabodeDriver driver = connect();
            driver.get(fixture() + "/src/" + clazz + ".java");
            driver.findEditorLine(clazz, offset).click();
            WebElement content = driver.switchToEditorInner();
            content.sendKeys("public static void " + declare + "() { " + call + "(); ");
            driver.waitForSync();
            barrier.await(10, TimeUnit.SECONDS);
            driver.switchToEditorOuter();
            assertEquals(edits, driver.findElements(By.className("collab-annotation")).size());
            assertEquals(2, driver.findElements(By.className("problem-error-annotation")).size());
            barrier.await(10, TimeUnit.SECONDS);
            driver.switchToEditorInner();
            content.sendKeys("}");
            driver.waitForSync();
            barrier.await(10, TimeUnit.SECONDS);
            driver.switchToEditorOuter();
            assertEquals(0, driver.findElements(By.className("collab-annotation")).size());
            assertEquals(0, driver.findElements(By.className("problem-error-annotation")).size());
            return null;
        }
    }
}
