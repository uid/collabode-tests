package collabode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import collabode.BrowserTest;
import collabode.CollabodeDriver;

@BrowserTest.Fixtures("syntax-highlight")
public class SyntaxHighlightTest extends BrowserTest {
    
    @Test public void testInitialHighlight() {
        List<Span> expectation = Arrays.asList(
                new Span("import", "127, 0, 85", true, false), new Span("java.util.*;"),
                new Span("class", "127, 0, 85", true, false), new Span("Syntactic {"),
                new Span("static", "127, 0, 85", true, false), new Span("Map"),
                  new Span("staticMember", "0, 0, 192", false, true), new Span("="),
                  new Span("null", "127, 0, 85", true, false), new Span(";"),
                new Span("List"), new Span("instanceMember", "0, 0, 192", false, false), new Span(";"),
                new Span("/**", "63, 95, 191", false, false),
                new Span("* Javadoc.", "63, 95, 191", false, false),
                new Span("*", "63, 95, 191", false, false), new Span("@param", "127, 159, 191", true, false),
                  new Span("input the input", "63, 95, 191", false, false),
                new Span("*/", "63, 95, 191", false, false),
                new Span("@"), // XXX at-sign should be highlighted as an annotation
                  new Span("Annotated", "100, 100, 100", false, false),
                  new Span("public", "127, 0, 85", true, false), new Span("static", "127, 0, 85", true, false),
                  new Span("int", "127, 0, 85", true, false), new Span("staticMethod(String input) {"),
                new Span("System."), new Span("out", "0, 0, 192", false, true), new Span(".println("),
                  new Span("\"String literal\"", "42, 0, 255", false, false),
                  new Span("+"), new Span("'.'", "42, 0, 255", false, false), new Span(");"),
                new Span("/* Block comment */", "63, 127, 95", false, false),
                new Span("return", "127, 0, 85", true, false), new Span("Enumerated."),
                  new Span("TWO", "0, 0, 192", false, true), new Span(".ordinal();"),
                  new Span("// Line comment", "63, 127, 95", false, false),
                new Span("}"),
                new Span("public", "127, 0, 85", true, false), new Span("<T"),
                  new Span("extends", "127, 0, 85", true, false),
                  new Span("List> T instanceMethod(Map.Entry<String, T> entry) {"),
                new Span("this", "127, 0, 85", true, false), new Span("."),
                  new Span("instanceMember", "0, 0, 192", false, false), new Span(".size();"),
                new Span("return", "127, 0, 85", true, false), new Span("entry.getValue();"),
                new Span("}"),
                new Span("}"),
                //new Span("enum", "127, 0, 85", true, false), XXX enum should be highlighted as a keyword
                  new Span("enum Enumerated {"),
                  new Span("ONE", "0, 0, 192", false, true), new Span(","),
                  new Span("TWO", "0, 0, 192", false, true), new Span(","),
                  new Span("THREE", "0, 0, 192", false, true), new Span("}"),
                new Span("@"), // XXX at-sign should be highlighted as a keyword
                  new Span("interface", "127, 0, 85", true, false),
                  new Span("Annotated", "100, 100, 100", false, false), new Span("{ }")
        );
        Iterator<Span> expect = expectation.iterator();
        
        CollabodeDriver driver = connect();
        driver.get(fixture() + "/src/Syntactic.java");
        driver.waitForSync();
        WebElement content = driver.switchToEditorInner();
        List<WebElement> spans = content.findElements(By.xpath("div/span"));
        for (WebElement span : spans) {
            if (span.getText().matches("^\\s*$")) { continue; }
            
            Span expected = expect.next();
            assertEqualsModWhitespace(expected.text, span.getText());
            if (expected.color.isEmpty()) {
                assertTrue(span.getText() + " should have no color",
                        span.findElements(By.tagName("span")).isEmpty());
            } else {
                assertEquals(span.getText() + " should have color",
                        expected.color, span.findElement(By.tagName("span")).getCssValue("color"));
            }
            assertEquals(span.getText() + " should be bold",
                    expected.bold, ! span.findElements(By.tagName("b")).isEmpty());
            assertEquals(span.getText() + "should be italic",
                    expected.italic, ! span.findElements(By.tagName("i")).isEmpty());
        }
        
    }
}

class Span {
    public final String text;
    public final String color;
    public final boolean bold;
    public final boolean italic;
    
    Span(String text) {
        this.text = text;
        this.color = "";
        this.bold = false;
        this.italic = false;
    }
    
    Span(String text, String color, boolean bold, boolean italic) {
        this.text = text;
        this.color = "rgb(" + color +")";
        this.bold = bold;
        this.italic = italic;
    }
    
    @Override public String toString() {
        return "Span[" + text + "," + color + (bold ? ",bold" : "") + (italic ? ",italic" : "") + "]";
    }
}
