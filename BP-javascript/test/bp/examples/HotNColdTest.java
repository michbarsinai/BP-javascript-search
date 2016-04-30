package bp.examples;

import bp.events.BEvent;
import bp.bprogram.BJavascriptProgram;
import bp.bprogram.listeners.InMemoryEventLoggingListener;
import bp.bprogram.listeners.StreamLoggerListener;
import bp.validation.eventpattern.EventPattern;
import org.junit.Test;
import org.mozilla.javascript.Context;

import java.util.Arrays;
import static org.junit.Assert.assertTrue;
import org.mozilla.javascript.Scriptable;

/**
 * @author orelmosheweinstock
 * @author @michbarsinai
 */
public class HotNColdTest {
    
    final BEvent hotEvent = new BEvent("hotEvent");
    final BEvent coldEvent = new BEvent("coldEvent");
    final BEvent allDoneEvent = new BEvent("allDone");

    BJavascriptProgram buildProgram() {
        return new BJavascriptProgram("HotAndCold") {
            @Override
            protected void setupProgramScope() {
                loadJavascriptFile("HotNCold.js");
        
                final Scriptable globalScope = getGlobalScope();
                Arrays.asList(hotEvent, coldEvent, allDoneEvent).forEach(
                        e -> globalScope.put( e.getName(), globalScope, Context.javaToJS(e, globalScope)));            
            }
            
        };
    }
    
    @Test
    public void superStepTest() throws InterruptedException {
        BJavascriptProgram sut = buildProgram();
        sut.addListener( new StreamLoggerListener() );
        InMemoryEventLoggingListener eventLogger = sut.addListener( new InMemoryEventLoggingListener() );
        
        sut.start();
        
        EventPattern expected = new EventPattern().append(coldEvent).append(hotEvent)
                .append(coldEvent).append(hotEvent)
                .append(coldEvent).append(hotEvent)
                .append(allDoneEvent);
        assertTrue( expected.matches(eventLogger.getEvents()) );
    }

}