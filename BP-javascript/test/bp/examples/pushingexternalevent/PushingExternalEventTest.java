package bp.examples.pushingexternalevent;

import bp.events.BEvent;
import bp.bprogram.BProgram;
import bp.bprogram.listeners.InMemoryEventLoggingListener;
import bp.bprogram.listeners.StreamLoggerListener;
import bp.validation.eventpattern.EventPattern;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import org.mozilla.javascript.Scriptable;

/**
 * @author orelmosheweinstock
 * @author @michbarsinai
 */
public class PushingExternalEventTest {

    BProgram buildProgram() {
        return new BProgram("PushingExternalEvent") {
            @Override
            protected void setupProgramScope( Scriptable aScope ) {
                loadJavascriptResource("PushingExternalEvent.js");
            }
        };
    }
    
    @Test
    public void superStepTest() throws InterruptedException {
        BProgram sut = buildProgram();
        sut.addListener( new StreamLoggerListener() );
        InMemoryEventLoggingListener eventLogger = sut.addListener( new InMemoryEventLoggingListener() );
        
        sut.start();
        
        eventLogger.getEvents().forEach(e->System.out.println(e) );
        EventPattern expected = new EventPattern()
                .append(new BEvent("start"))
                .append(new BEvent("external"))
                .append(new BEvent("done"));
        
        assertTrue( expected.matches(eventLogger.getEvents()) );
    }

}