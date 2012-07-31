package collabode;

import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.junit.rules.ExternalResource;

/**
 * An Eclipse Java project for testing.
 */
public class TestProject extends ExternalResource {
    
    private static final AtomicInteger COUNTER = new AtomicInteger();
    
    private final String name;
    private IProject project;
    
    public TestProject(String name) {
        this.name = name;
    }
    
    /**
     * Returns the projects.
     */
    public IProject get() {
        return project;
    }
    
    /**
     * Creates project file {@code name} with the given contents.
     */
    public IFile createFile(String name, String content) throws CoreException {
        IFile file = get().getFile(name);
        file.create(new java.io.ByteArrayInputStream(content.getBytes()), false, null);
        return file;
    }
    
    @Override protected void before() throws Throwable {
        project = Workspace.createJavaProject("junit-" + name + "-" + COUNTER.getAndIncrement(), false);
    }
    
    @Override protected void after() {
        try {
            project.delete(true, true, null);
        } catch (CoreException e) {
            fail("Failed to delete project " + name);
        }
    }
}
