package collabode;

import java.io.IOException;

import org.junit.Test;
import org.openqa.selenium.*;

import com.google.common.base.Function;

@BrowserTest.Fixtures("org-imports")
public class OrgImportsTest extends BrowserTest {
    
    static final By ORG_IMPORTS =  By.cssSelector("#orgimports.docbarbutton");
    static final By RESOLVE_DIALOG = By.id("orgimports-container");
    
    @Test public void testUnambiguous() throws IOException, InterruptedException {
        final CollabodeDriver driver = connect();
        driver.get(fixture() + "/src/Unambiguous.java");
        driver.waitForSync();
        WebElement button = driver.findElement(ORG_IMPORTS);
        button.click();
        
        driver.wait.until(successful(new VoidCall.Boxed() {
            void voidCall() throws IOException {
                assertEqualsModWhitespace(expected("Unambiguous.java"), driver.switchToEditorInner().getText());
            }
        }));
    }
    
    @Test public void testAmbiguous() throws IOException, InterruptedException {
        final CollabodeDriver driver = connect();
        driver.get(fixture() + "/src/Ambiguous.java");
        driver.waitForSync();
        WebElement button = driver.findElement(ORG_IMPORTS);
        button.click();
        
        WebElement box = driver.wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(RESOLVE_DIALOG);
            }
        });
        box.findElement(By.xpath("//li[.='java.util.Timer']")).click();
        box.findElement(By.xpath("//li[.='javax.swing.Timer']")).click();
        box.findElement(By.xpath("//button[.='Next']")).click();
        box.findElement(By.xpath("//li[.='java.util.List']")).click();
        box.findElement(By.xpath("//button[.='Finish']")).click();
        
        driver.wait.until(successful(new VoidCall.Boxed() {
            void voidCall() throws IOException {
                assertEqualsModWhitespace(expected("Ambiguous.java"), driver.switchToEditorInner().getText());
            }
        }));
    }
}
