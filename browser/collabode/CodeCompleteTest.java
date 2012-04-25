package collabode;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.openqa.selenium.Keys.*;

import java.util.*;

import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;

import collabode.BrowserTest;
import collabode.CollabodeDriver;

@BrowserTest.Fixtures("code-complete")
public class CodeCompleteTest extends BrowserTest {
    
    @Test public void testArray() {
        testCompletionsExist("public static void main",
                new String[] { "Array", chord(CONTROL, SPACE) },
                new String[] { "ArrayList - java.util", "Arrays - java.util" });
    }
    
    @Test public void testSystemDot() {
        testCompletionsExist("public static void main",
                new String[] { "System", "." },
                new String[] { "class : Class", "out : PrintStream - System", "currentTimeMillis() : long - System" });
    }
    
    @Test public void testSystemDotOutDot() {
        testCompletionsExist("public static void main",
                new String[] { "System", ".", "out", "." },
                new String[] { "println() : void - PrintStream", "toString() : String - Object" });
    }
    
    @Test public void testStaticScope() {
        testCompletionsExist("myStaticMethod",
                new CharSequence[] { ENTER, chord(CONTROL, SPACE) }, // XXX ENTER
                new String[] { "x : int" });
    }
    
    @Test public void testInstanceScope() {
        testCompletionsExist("myInstanceMethod",
                new CharSequence[] { ENTER, chord(CONTROL, SPACE) }, // XXX ENTER
                new String[] { "y : int", "myStaticMethod(int x) : void - Hello" });
    }
    
    private void testCompletionsExist(String below, CharSequence[] inputs, String[] completions) {
        CollabodeDriver driver = connect();
        driver.get(fixture() + "/src/Hello.java");
        
        driver.findEditorLine(below, 1).click();
        WebElement content = driver.switchToEditorInner();
        for (CharSequence input : inputs) {
            content.sendKeys(input);
            driver.waitForSync().switchToEditorInner();
        }
        driver.switchToPage();
        
        List<String> expected = new ArrayList<String>(Arrays.asList(completions));
        assertFalse("Nothing to test for " + Arrays.toString(inputs), expected.isEmpty());
        
        WebElement list = driver.wait.until(new ExpectedCondition<WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(By.className("codecomplete"));
            }
        });
        for (WebElement item : list.findElements(By.tagName("li"))) {
            expected.remove(item.getText());
            if (expected.isEmpty()) { break; }
        }
        
        assertTrue("Missing completions for " + Arrays.toString(inputs) + ": " + expected, expected.isEmpty());
    }
}
