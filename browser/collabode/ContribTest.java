package collabode;

import static org.openqa.selenium.Keys.*;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@BrowserTest.Fixtures("monkeys")
public class ContribTest extends BrowserTest {
    
    static final By INS = By.tagName("u");
    static final By DEL = By.tagName("s");
    
    @Test public void testSingleFile() {
        CollabodeDriver driver = connect();
        driver.get(fixture() + "/src/Evil.java");
        driver.waitForSync();
        String userId = (String)driver.executeScript("return clientVars.userId"); // XXX
        
        WebElement content = driver.switchToEditorInner();
        
        long start = System.currentTimeMillis();
        
        final String see = "void see() { }";
        driver.findEditorLine("see()", 2).click();
        content.sendKeys("" + SHIFT + UP + BACK_SPACE + UP + BACK_SPACE + UP + BACK_SPACE + NULL);
        driver.waitForSync();
        
        long mid = System.currentTimeMillis();
        
        final String hear = "// Listen";
        driver.findEditorLine("hear()", 1).click();
        content.sendKeys(hear);
        driver.waitForSync();
        
        long end = System.currentTimeMillis();
        
        assertInsertedAndDeleted(driver, userId + ":",                      hear, see);
        
        assertInsertedAndDeleted(driver, userId + ":" + start + ".." + mid, "",   see);
        assertInsertedAndDeleted(driver, userId + ":" + start + ".." + end, hear, see);
        assertInsertedAndDeleted(driver, userId + ":" + start,              hear, see);
        
        assertInsertedAndDeleted(driver, userId + ":" + mid + ".." + mid,   "",   "");
        assertInsertedAndDeleted(driver, userId + ":" + mid + ".." + end,   hear, "");
        assertInsertedAndDeleted(driver, userId + ":" + mid,                hear, "");
    }
    
    private void assertInsertedAndDeleted(CollabodeDriver driver, String contrib, String expectInsert, String expectDelete) {
        driver.get("contrib:" + contrib + "/" + fixture());
        StringBuilder insert = new StringBuilder();
        for (WebElement elt : driver.findElements(INS)) {
            insert.append(elt.getText()).append(" ");
        }
        assertEqualsModWhitespace(expectInsert, insert.toString());
        StringBuilder delete = new StringBuilder();
        for (WebElement elt : driver.findElements(DEL)) {
            delete.append(elt.getText()).append(" ");
        }
        assertEqualsModWhitespace(expectDelete, delete.toString());
    }
}
