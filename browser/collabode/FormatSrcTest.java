package collabode;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@BrowserTest.Fixtures("format-src")
public class FormatSrcTest extends BrowserTest {
    
    static final By FORMAT = By.cssSelector("#format.docbarbutton");
    
    @Test public void testFormat() throws IOException {
        final CollabodeDriver driver = connect();
        driver.get(fixture() + "/src/Hello.java");
        driver.waitForSync();
        WebElement button = driver.findElement(FORMAT);
        button.click();
        
        driver.wait.until(successful(new VoidCall.Boxed() {
            void voidCall() throws Exception {
                assertEquals(expected("Hello.java").trim(), driver.switchToEditorInner().getText());
            }
        }));
    }
}
