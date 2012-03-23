package collabode;

import net.appjet.ajstdlib.execution;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class UnitTestApplication implements IApplication {
    
    public Object start(IApplicationContext context) throws Exception {
        
        // Stub out Appjet & EtherPad
        execution.createNamedTaskThreadPool("dbwriter_infreq", 0);
        
        // Minimal Eclipse
        Application.startWorkbench();
        
        return EXIT_OK;
    }
    
    public void stop() {
    }
}
