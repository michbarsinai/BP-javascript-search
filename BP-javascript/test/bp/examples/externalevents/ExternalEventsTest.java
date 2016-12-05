package bp.examples.externalevents;

import bp.bprogram.runtimeengine.BProgram;
import bp.bprogram.runtimeengine.listeners.InMemoryEventLoggingListener;
import bp.bprogram.runtimeengine.listeners.StreamLoggerListener;
import bp.events.BEvent;
import bp.validation.eventpattern.EventPattern;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author michael
 */
public class ExternalEventsTest {

    BProgram buildProgram() {
        return new BProgram("ExternalEvents") {
            @Override
            protected void setupProgramScope( Scriptable aScope ) {
                loadJavascriptResource("ExternalEvents.js");
            }
        };
    }
    
    @Test
    public void superStepTest() throws InterruptedException {
        final BEvent in1a = new BEvent("in1a");
        final BEvent in1b = new BEvent("in1b");
        final BEvent ext1 = new BEvent("ext1");
        final BProgram sut = buildProgram();
        sut.addListener( new StreamLoggerListener() );
        InMemoryEventLoggingListener eventLogger = sut.addListener( new InMemoryEventLoggingListener() );
        
        new Thread( ()->sut.enqueueExternalEvent(ext1) ).start();
        
        sut.start();
        
        eventLogger.getEvents().forEach(e->System.out.println(e) );
        
        EventPattern expected = new EventPattern()
                .append(in1a).append(ext1).append(in1b);
        
        assertTrue( expected.matches(eventLogger.getEvents()) );
    }    
}
