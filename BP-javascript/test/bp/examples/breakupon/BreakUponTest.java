package bp.examples.breakupon;

import bp.events.BEvent;
import bp.bprogram.runtimeengine.BProgram;
import bp.bprogram.runtimeengine.listeners.InMemoryEventLoggingListener;
import bp.bprogram.runtimeengine.listeners.StreamLoggerListener;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.mozilla.javascript.Scriptable;

/**
 * @author orelmosheweinstock
 * @author @michbarsinai
 */
public class BreakUponTest {

    BProgram buildProgram() {
        return new BProgram("BreakUpon") {
            @Override
            protected void setupProgramScope( Scriptable aScope ) {
                loadJavascriptResource("BreakUpon.js");
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
        final BEvent breakingEvent = new BEvent("breaking");
        
        assertEquals( Arrays.asList(breakingEvent, breakingEvent), eventLogger.getEvents() );
    }

}