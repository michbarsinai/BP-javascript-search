/*
 *  Author: Michael Bar-Sinai
 */
package bp.examples.jseventset;

import bp.bprogram.BProgram;
import bp.bprogram.SingleResourceBProgram;
import bp.bprogram.listeners.InMemoryEventLoggingListener;
import bp.bprogram.listeners.StreamLoggerListener;
import bp.events.BEvent;
import java.net.URISyntaxException;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class JsEventSetTest {
    
    @Test
    public void testRun() throws InterruptedException, URISyntaxException {
        BProgram bpr = new SingleResourceBProgram( getClass().getResource("JsEventSet.js").toURI());
        bpr.addListener( new StreamLoggerListener() );
        InMemoryEventLoggingListener eventLogger = bpr.addListener( new InMemoryEventLoggingListener() );
        bpr.start();
        
        assertEquals(Arrays.asList(new BEvent("1stEvent"), new BEvent("2ndEvent")), eventLogger.getEvents() );
        
    }
}
