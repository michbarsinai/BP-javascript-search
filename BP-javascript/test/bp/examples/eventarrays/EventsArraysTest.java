/*
 *  Author: Michael Bar-Sinai
 */
package bp.examples.eventarrays;

import bp.bprogram.runtimeengine.BProgram;
import bp.bprogram.runtimeengine.SingleResourceBProgram;
import bp.bprogram.runtimeengine.listeners.InMemoryEventLoggingListener;
import bp.bprogram.runtimeengine.listeners.StreamLoggerListener;
import bp.events.BEvent;
import java.util.Arrays;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class EventsArraysTest {

    @Test
    public void testEventsWithData() throws Exception {
        BProgram bpr = new SingleResourceBProgram( getClass().getResource("EventArrays.js").toURI() );
        bpr.addListener( new StreamLoggerListener() );
        InMemoryEventLoggingListener events = bpr.addListener( new InMemoryEventLoggingListener() );
        
        bpr.start();
        
        assertEquals( Arrays.asList("e11", "e21"),
                      events.getEvents().stream().map( BEvent::getName).collect(toList()));
        
    }
    
}
