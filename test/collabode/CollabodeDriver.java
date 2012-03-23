package collabode;

import static org.junit.Assert.assertEquals;

import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

/**
 * Represents a web browser using Collabode.
 */
public class CollabodeDriver implements WebDriver, JavascriptExecutor {
    
    public final String baseUrl;
    /**
     * Underlying driver.
     */
    public final WebDriver driver;
    
    public CollabodeDriver(String baseUrl, WebDriver driver) {
        this.baseUrl = baseUrl;
        this.driver = driver;
    }
    
    /**
     * Log in to Collabode.
     */
    void login(String username) {
        get("login/");
        WebElement field = findElement(By.name("rusername"));
        field.sendKeys(username);
        field.submit();
        assertEquals(username, findElement(By.id("docbaruser")).getText());
    }
    
    /**
     * Select the outer page.
     * 
     * @return this
     */
    public WebDriver switchToPage() {
        return switchTo().defaultContent();
    }
    
    /**
     * Select the outer frame of the pad editor.
     */
    public WebElement switchToEditorOuter() {
        switchToPage();
        switchTo().frame(0);
        return findElement(By.id("outerdocbody"));
    }
    
    /**
     * Select the inner frame of the pad editor.
     */
    public WebElement switchToEditorInner() {
        switchToEditorOuter();
        switchTo().frame(0);
        return findElement(By.id("innerdocbody"));
    }
    
    /**
     * Wait for the pad editor to report a connected & synced state.
     * 
     * @return this
     */
    public CollabodeDriver waitForSync() {
        switchToPage();
        final WebElement connecting = driver.findElement(By.id("connstatusconnecting"));
        new WebDriverWait(driver, 2).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver input) {
                return ! connecting.isDisplayed();
            }
        });
        final WebElement syncing = driver.findElement(By.id("syncstatussyncing"));
        new WebDriverWait(driver, 2).until(new Predicate<WebDriver>() {
            public boolean apply(WebDriver input) {
                return ! syncing.isDisplayed();
            }
        });
        return this;
    }
    
    /**
     * Returns the first pad editor line containing {@code target}.
     */
    public WebElement findEditorLine(String target) {
        return findEditorLine(target, 0);
    }
    
    /**
     * Returns the pad editor line {@code offset} lines below the first line
     * containing {@code target}.
     */
    public WebElement findEditorLine(String target, int offset) {
        WebElement content = waitForSync().switchToEditorInner();
        List<WebElement> lines = content.findElements(By.className("ace-line"));
        WebElement line;
        for (Iterator<WebElement> it = lines.iterator(); it.hasNext(); ) {
            if ((line = it.next()).getText().contains(target)) {
                for (int ii = 0; ii < offset; ii++) {
                    line = it.next();
                }
                return line;
            }
        }
        throw new IllegalArgumentException("Failed to find line \"" + target +"\" + " + offset);
    }
    
    // Implement WebDriver interface
    
    /**
     * Load a new Collabode page.
     * 
     * @param url The <i>relative</i> URL to load.
     */
    public void get(String url) {
        driver.get(baseUrl + url);
    }
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    public String getTitle() {
        return driver.getTitle();
    }
    public List<WebElement> findElements(By by) {
        return driver.findElements(by);
    }
    public WebElement findElement(By by) {
        return driver.findElement(by);
    }
    public String getPageSource() {
        return driver.getPageSource();
    }
    public void close() {
        driver.close();
    }
    public void quit() {
        driver.quit();
    }
    public Set<String> getWindowHandles() {
        return driver.getWindowHandles();
    }
    public String getWindowHandle() {
        return driver.getWindowHandle();
    }
    public TargetLocator switchTo() {
        return driver.switchTo();
    }
    public Navigation navigate() {
        return driver.navigate();
    }
    public Options manage() {
        return driver.manage();
    }
    
    // Implement JavascriptExecutor interface
    
    public Object executeScript(String script, Object... args) {
        return ((JavascriptExecutor)driver).executeScript(script, args);
    }
    public Object executeAsyncScript(String script, Object... args) {
        return ((JavascriptExecutor)driver).executeAsyncScript(script, args);
    }
}
