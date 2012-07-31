package collabode;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Browser automation test using Selenium.
 */
public class BrowserTest {
    
    private static final String selenium;
    private static final CircularIterator<String> ports;
    private static final String collabode;
    private static final String localhost;
    static {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File("config/tests-browser.properties")));
            
            selenium = "http://" + properties.get("seleniumIP") + ":%s/wd/hub";
            ports = new CircularIterator<String>(properties.getProperty("seleniumPorts", "4444").split(","));
            
            Properties config = new Properties();
            config.load(new FileInputStream(new File("config/collabode-browser.properties")));
            String port = config.getProperty("listen").split(":")[1];
            
            collabode = "http://" + properties.getProperty("listenIP") + ":" + port + "/";
            localhost = "http://localhost:" + port + "/";
        } catch (IOException ioe) { throw new Error(ioe); }
    }
    
    private static final DefaultHttpClient admin = new DefaultHttpClient();
    private static final StatusHandler handler = new StatusHandler();
    static {
        try {
            HttpGet login = new HttpGet(localhost + "login/");
            admin.execute(login, handler);
        } catch (IOException ioe) { throw new Error(ioe); }
    }
    
    private static final List<CollabodeDriver> drivers = new ArrayList<CollabodeDriver>();
    private static ListIterator<CollabodeDriver> available;
    
    private String fixture;
    
    @Before public final void setUpCollabode() throws IOException {
        createFixtures();
        available = drivers.listIterator();
    }
    
    @After public final void tearDownCollabode() throws IOException {
        for (CollabodeDriver driver : drivers) { driver.get(""); }
        available = null;
        deleteFixtures();
    }
    
    @AfterClass public static final void tearDownClassCollabode() {
        for (CollabodeDriver driver : drivers) {
            driver.quit();
        }
        drivers.clear();
    }
    
    /**
     * Obtains a new browser connection.
     */
    protected CollabodeDriver connect() {
        if ( ! available.hasNext()) {
            try {
                URL remote = new URL(String.format(selenium, ports.next().trim()));
                WebDriver web = new RemoteWebDriver(remote, DesiredCapabilities.firefox());
                CollabodeDriver driver = new CollabodeDriver(collabode, web);
                driver.login("junit" + drivers.size());
                available.add(driver);
                available.previous();
            } catch (MalformedURLException murle) {
                throw new Error(murle);
            }
        }
        return available.next();
    }
    
    /**
     * Returns the fixture project name for the current test.
     * Requires a {@link Fixtures} annotation.
     */
    protected String fixture() {
        return fixture;
    }
    
    /**
     * Returns the contents of the given file in the {@code src} subdirectory
     * of the fixture directory specified with {@link Fixtures}.
     */
    protected String initial(String filename) throws IOException {
        String fixtureDir = getClass().getAnnotation(Fixtures.class).value();
        File initialDir = new File("fixtures/" + fixtureDir + "/src");
        return new Scanner(new File(initialDir, filename)).useDelimiter("\\A").next();
    }
    
    /**
     * Returns the contents of the given file in the {@code expected} subdirectory
     * of the fixture directory specified with {@link Fixtures}.
     */
    protected String expected(String filename) throws IOException {
        String fixtureDir = getClass().getAnnotation(Fixtures.class).value();
        File expectedDir = new File("fixtures/" + fixtureDir + "/expected");
        return new Scanner(new File(expectedDir, filename)).useDelimiter("\\A").next();
    }
    
    private void createFixtures() throws IOException {
        Fixtures fixtures = getClass().getAnnotation(Fixtures.class);
        if (fixtures == null) { return; }
        
        String fixtureDir = fixtures.value();
        fixture = "junit-" + fixtureDir + "-" + (int)(System.currentTimeMillis() / 1000);
        
        boolean testDriven = getClass().isAnnotationPresent(TestDriven.class);
        adminPost("",
                  new BasicNameValuePair("projectname", fixture),
                  new BasicNameValuePair("projecttype", testDriven ? "javatdproject" : "javaproject"));
        adminPost(fixture,
                  new BasicNameValuePair("acl", "1"),
                  new BasicNameValuePair("acl_userid", "anyone"),
                  new BasicNameValuePair("acl_permission", "write"));
        
        File fixtureSrc = new File("fixtures/" + fixtureDir + "/src");
        for (String filename : fixtureSrc.list()) {
            adminPost(fixture + "/src",
                      new BasicNameValuePair("filename", filename),
                      new BasicNameValuePair("content", initial(filename)));
        }
    }
    
    private void deleteFixtures() throws IOException {
        if (fixture == null) { return; }
        
        adminPost("delete/" + fixture);
        fixture = null;
    }
    
    /**
     * Performs a HTTP POST on the admin interface.
     */
    protected void adminPost(String path, NameValuePair... parameters) throws IOException {
        admin.execute(new HttpEntityPost(localhost + path, parameters), handler);
    }
    
    /**
     * Asserts that two Strings are equal modulo whitespace.
     * Changes to leading, trailing, and internal whitespace are ignored.
     */
    protected static void assertEqualsModWhitespace(String expected, String actual) {
        assertEquals(regularWhitespace(expected), regularWhitespace(actual));
    }
    
    protected static String regularWhitespace(String input) {
        return (" " + input + " ").replaceAll("\\s+", " ");
    }
    
    /**
     * Specifies the location of test fixtures.
     */
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Fixtures {
        String value();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface TestDriven {
    }
}

class HttpEntityPost extends HttpPost {
    public HttpEntityPost(String url, NameValuePair... parameters) throws UnsupportedEncodingException {
        super(url);
        setEntity(new UrlEncodedFormEntity(Arrays.asList(parameters)));
    }
}

class StatusHandler implements ResponseHandler<Integer> {
    public Integer handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        EntityUtils.consume(response.getEntity());
        return response.getStatusLine().getStatusCode();
    }
}
