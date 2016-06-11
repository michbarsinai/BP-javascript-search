package bp.examples.namedargs;

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
public class NamedArgsHotNColdTest {

    BProgram buildProgram() {
        return new BProgram("NamedArgsHotNCold") {
            @Override
            protected void setupProgramScope( Scriptable aScope ) {
                loadJavascriptResource("NamedArgsHotNCold.js");
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
        final BEvent hotEvent = new BEvent("hotEvent");
        final BEvent coldEvent = new BEvent("coldEvent");
        final BEvent allDoneEvent = new BEvent("allDone");
        EventPattern expected = new EventPattern().append(coldEvent).append(hotEvent)
                .append(coldEvent).append(hotEvent)
                .append(coldEvent).append(hotEvent)
                .append(allDoneEvent);
        
        assertTrue( expected.matches(eventLogger.getEvents()) );
    }

}