package bp.examples.externaleventsdaemon;

import bp.bprogram.BJavascriptProgram;
import bp.bprogram.listeners.InMemoryEventLoggingListener;
import bp.bprogram.listeners.StreamLoggerListener;
import bp.events.BEvent;
import bp.validation.eventpattern.EventPattern;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author michael
 */
public class ExternalEventsDaemonTest {
    final BEvent in1a = new BEvent("in1a");
    final BEvent in1b = new BEvent("in1b");
    final BEvent ext1 = new BEvent("ext1");

    BJavascriptProgram buildProgram() {
        return new BJavascriptProgram("ExternalEventsDaemon") {
            @Override
            protected void setupProgramScope() {
                loadJavascriptFile("ExternalEventsDaemon.js");
        
                final Scriptable globalScope = getGlobalScope();
                Arrays.asList(in1a, in1b, ext1).forEach(
                        e -> globalScope.put( e.getName(), globalScope, Context.javaToJS(e, globalScope)));            
            }
            
        };
    }
    
    @Test
    public void superStepTest() throws InterruptedException {
        final BJavascriptProgram sut = buildProgram();
        sut.addListener( new StreamLoggerListener() );
        InMemoryEventLoggingListener eventLogger = sut.addListener( new InMemoryEventLoggingListener() );
        
        new Thread( ()-> {
            try {
                for ( int i=0; i<4; i++ ) {
                    Thread.sleep(500);
                    sut.enqueueExternalEvent(ext1);
                    
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(ExternalEventsDaemonTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        } ).start();
        
        sut.start();
        
        eventLogger.getEvents().forEach(e->System.out.println(e) );
        
        EventPattern expected = new EventPattern()
                .append(ext1).append(in1a).append(in1b)
                .append(ext1).append(in1a).append(in1b)
                .append(ext1).append(in1a).append(in1b);
        
        assertTrue( expected.matches(eventLogger.getEvents()) );
    }    
}
