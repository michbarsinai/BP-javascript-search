package bp.examples.externaleventsdaemon;

import bp.bprogram.BProgram;
import bp.bprogram.listeners.InMemoryEventLoggingListener;
import bp.bprogram.listeners.StreamLoggerListener;
import bp.events.BEvent;
import bp.validation.eventpattern.EventPattern;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author michael
 */
public class ExternalEventsDaemonTest {
    BProgram buildProgram() {
        return new BProgram("ExternalEventsDaemon") {
            @Override
            protected void setupProgramScope() {
                loadJavascriptFile("ExternalEventsDaemon.js");
            }
            
        };
    }
    
    @Test
    public void superStepTest() throws InterruptedException {
        final BProgram sut = buildProgram();
        sut.addListener( new StreamLoggerListener() );
        InMemoryEventLoggingListener eventLogger = sut.addListener( new InMemoryEventLoggingListener() );
        
        new Thread( ()-> {
            try {
                for ( int i=0; i<4; i++ ) {
                    Thread.sleep(500);
                    sut.enqueueExternalEvent(new BEvent("ext1"));
                    
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ExternalEventsDaemonTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } ).start();
        
        sut.start();
        
        eventLogger.getEvents().forEach(e->System.out.println(e) );
        
        final BEvent in1a = new BEvent("in1a");
        final BEvent in1b = new BEvent("in1b");
        final BEvent ext1 = new BEvent("ext1");

        EventPattern expected = new EventPattern()
                .append(ext1).append(in1a).append(in1b)
                .append(ext1).append(in1a).append(in1b)
                .append(ext1).append(in1a).append(in1b);
        
        assertTrue( expected.matches(eventLogger.getEvents()) );
    }    
}
