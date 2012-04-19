package collabode;

import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.Keys.*;

import java.util.*;

import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Function;

@BrowserTest.Fixtures("testing")
public class TestingTest extends BrowserTest {
    
    static final By TEST_CONTAINER = By.id("testorcontainer");
    
    @Test public void testImplementing() {
        CollabodeDriver driver = connect();
        driver.get(fixture() + "/src/Widget.java");
        driver.waitForSync();
        
        final WebElement box = driver.findElement(TEST_CONTAINER);
        
        new WebDriverWait(driver, 10).until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return box.findElement(By.cssSelector(".test.error"));
            }
        });
        
        assertEquals(Arrays.asList(new ATest(AResult.ERROR, "WidgetTest", "testFoo")),
                     ATest.tests(box.findElements(By.cssSelector(".test"))));
        
        driver.findEditorLine("Widget", 1).click();
        driver.switchToEditorInner().sendKeys(ENTER,
                                              "public static int foo() {", ENTER,
                                              "return 0;", ENTER,
                                              "}", ENTER);
        driver.switchToPage();
        
        new WebDriverWait(driver, 10).until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return box.findElement(By.cssSelector(".test.failure"));
            }
        });
        
        assertEquals(Arrays.asList(new ATest(AResult.FAILURE, "WidgetTest", "testFoo")),
                     ATest.tests(box.findElements(By.cssSelector(".test"))));
        
        driver.findEditorLine("return 0;").click();
        driver.switchToEditorInner().sendKeys(BACK_SPACE, BACK_SPACE, "1;");
        driver.switchToPage();
        
        new WebDriverWait(driver, 10).until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return box.findElement(By.cssSelector(".test.ok"));
            }
        });
        
        assertEquals(Arrays.asList(new ATest(AResult.OK, "WidgetTest", "testFoo")),
                     ATest.tests(box.findElements(By.cssSelector(".test"))));
    }
}

enum AResult {
    OK, FAILURE, ERROR;
    
    static AResult forClass(String clazz) {
        List<String> classes = Arrays.asList(clazz.split(" +"));
        if (classes.contains("ok")) {
            return OK;
        } else if (classes.contains("failure")) {
            return FAILURE;
        } else if (classes.contains("error")) {
            return ERROR;
        }
        throw new IllegalArgumentException();
    }
}

class ATest {
    final AResult result;
    final String clazz;
    final String method;
    
    ATest(WebElement test) {
        result = AResult.forClass(test.getAttribute("class"));
        clazz = test.findElement(By.cssSelector(".testclass")).getText();
        method = test.findElement(By.cssSelector(".testmethod")).getText();
    }
    
    static List<ATest> tests(List<WebElement> tests) {
        List<ATest> results = new ArrayList<ATest>();
        for (WebElement test : tests) {
            results.add(new ATest(test));
        }
        return results;
    }
    
    ATest(AResult result, String clazz, String method) {
        this.result = result;
        this.clazz = clazz;
        this.method = method;
    }
    
    @Override public boolean equals(Object obj) {
        if ( ! (obj instanceof ATest)) { return false; }
        ATest other = (ATest)obj;
        return result == other.result && clazz.equals(other.clazz) && method.equals(other.method);
    }
    
    @Override public int hashCode() {
        return result.hashCode() + clazz.hashCode() + method.hashCode();
    }
    
    @Override public String toString() {
        return result.toString() + ":" + clazz + "." + method;
    }
}
