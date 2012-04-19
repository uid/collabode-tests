package collabode;

import static org.junit.Assert.assertEquals;
import static org.openqa.selenium.Keys.ENTER;

import java.util.*;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@BrowserTest.Fixtures("monkeys")
public class MonkeysTest extends BrowserTest {
    
    @Test public void testComments() throws Exception {
        CollabodeDriver[] drivers = new CollabodeDriver[] {
                connect(), connect(), connect()
        };
        
        List<String> methods = new ArrayList<String>(Arrays.asList("see", "hear", "speak"));
        final Iterator<String> method = methods.iterator();
        
        testWithMonkeys(drivers, "Evil.java", "EvilComments.java", new NextRunnable() {
            public Runnable next(final CollabodeDriver driver, final QuietCyclicBarrier barrier) {
                return new Runnable() {
                    final String target = method.next();
                    public void run() {
                        driver.findEditorLine(target, 1).click();
                        barrier.await();
                        WebElement content = driver.switchToEditorInner();
                        content.sendKeys("// " + target + " no evil");
                        driver.waitForSync().switchToEditorInner();
                        content.sendKeys(ENTER, "// " + target + " no evil again");
                    }
                };
            }
        });
        
        for (CollabodeDriver driver : drivers) {
            assertEquals(0, driver.switchToEditorOuter().findElements(By.className("annotation")).size());
        }
    }
    
    @Test public void testCode() throws Exception {
        CollabodeDriver[] drivers = new CollabodeDriver[] {
                connect(), connect(), connect()
        };
        
        List<String> methods = new ArrayList<String>(Arrays.asList("see", "hear", "hear", "speak", "speak", "see"));
        final Iterator<String> method = methods.iterator();
        
        testWithMonkeys(drivers, "Evil.java", "EvilCode.java", new NextRunnable() {
            public Runnable next(final CollabodeDriver driver, final QuietCyclicBarrier barrier) {
                return new Runnable() {
                    final String first = method.next();
                    final String second = method.next();
                    public void run() {
                        WebElement content = driver.switchToEditorInner();
                        driver.findEditorLine(first, 1).click();
                        barrier.await();
                        content.sendKeys("x = 5;");
                        driver.waitForSync();
                        barrier.await();
                        driver.findEditorLine(second, 0).click();
                        barrier.await();
                        content.sendKeys(ENTER, "int x;");
                    }
                };
            }
        });
        
        for (CollabodeDriver driver : drivers) {
            driver.switchToEditorOuter();
            assertEquals(0, driver.findElements(By.className("collab-annotation")).size());
            assertEquals(0, driver.findElements(By.className("problem-error-annotation")).size());
            assertEquals(3, driver.findElements(By.className("problem-warning-annotation")).size());
        }
    }
    
    @Test public void testFields() throws Exception {
        CollabodeDriver[] drivers = new CollabodeDriver[] {
                connect(), connect(), connect()
        };
        
        List<NextRunnable> runnables = Arrays.asList(new NextRunnable[] {
                new NextRunnable() {
                    public Runnable next(final CollabodeDriver driver, final QuietCyclicBarrier barrier) {
                        return new Runnable() {
                            public void run() {
                                WebElement content = driver.switchToEditorInner();
                                driver.findEditorLine("class Evil").click();
                                barrier.await();
                                content.sendKeys(ENTER, ENTER, "int x;");
                                content.sendKeys(ENTER, "static int a;");
                                content.sendKeys(ENTER, "private int y;");
                                content.sendKeys(ENTER, "private static int b;");
                            }
                        };
                    }
                },
                new NextRunnable() {
                    public Runnable next(final CollabodeDriver driver, final QuietCyclicBarrier barrier) {
                        return new Runnable() {
                            public void run() {
                                WebElement content = driver.switchToEditorInner();
                                driver.findEditorLine("}").click();
                                barrier.await();
                                content.sendKeys(ENTER, ENTER, "String z;");
                                content.sendKeys(ENTER, "static Boolean[] c;");
                                content.sendKeys(ENTER, "private String foo;");
                                content.sendKeys(ENTER, "private static Integer bar;");
                            }
                        };
                    }
                },
                new NextRunnable() {
                    public Runnable next(final CollabodeDriver driver, final QuietCyclicBarrier barrier) {
                        return new Runnable() {
                            public void run() {
                                WebElement content = driver.switchToEditorInner();
                                driver.findEditorLine("see()", 1).click();
                                barrier.await();
                                content.sendKeys("// working here", ENTER);
                                content.sendKeys("x = 1;", ENTER);
                                content.sendKeys("z = \"2\";", ENTER);
                                content.sendKeys("a = 3;", ENTER);
                                content.sendKeys("c = new Boolean[] { true, false, false };");
                            }
                        };
                    }
                }
        });
        final Iterator<NextRunnable> run = runnables.iterator();
        
        testWithMonkeys(drivers, "Evil.java", "EvilFields.java", new NextRunnable() {
            public Runnable next(CollabodeDriver driver, QuietCyclicBarrier barrier) {
                return run.next().next(driver, barrier);
            }
        });
        
        for (CollabodeDriver driver : drivers) {
            driver.switchToEditorOuter();
            assertEquals(0, driver.findElements(By.className("collab-annotation")).size());
            assertEquals(0, driver.findElements(By.className("problem-error-annotation")).size());
            assertEquals(4, driver.findElements(By.className("problem-warning-annotation")).size());
        }
    }
    
    private void testWithMonkeys(CollabodeDriver[] drivers, final String target, String expected, NextRunnable runnables) throws Exception {
        
        final QuietCyclicBarrier barrier = new QuietCyclicBarrier(drivers.length);
        
        List<Thread> threads = new ArrayList<Thread>();
        for (final CollabodeDriver driver : drivers) {
            final Runnable run = runnables.next(driver, barrier);
            threads.add(new Thread(new Runnable() {
                public void run() {
                    driver.get(fixture() + "/src/" + target);
                    run.run();
                    driver.waitForSyncing();
                    driver.waitForSync();
                }
            }));
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            thread.join();
        }
        
        for (CollabodeDriver driver : drivers) {
            assertEqualsModWhitespace(expected(expected), driver.switchToEditorInner().getText());
        }
        // XXX assert something about what's on disk
    }
}

interface NextRunnable {
    Runnable next(CollabodeDriver driver, QuietCyclicBarrier barrier);
}
