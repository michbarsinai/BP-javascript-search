package bp.examples.addingbthreads;

import bp.events.BEvent;
import bp.bprogram.BJavascriptProgram;
import bp.bprogram.listeners.InMemoryEventLoggingListener;
import bp.bprogram.listeners.StreamLoggerListener;
import bp.eventsets.EventSet;
import bp.validation.eventpattern.EventPattern;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Tests for a bhtread that adds bthreads as it works.
 * 
 * @author @michbarsinai
 */
public class AddingBthreadsTest {
    

    BJavascriptProgram buildProgram() {
        return new BJavascriptProgram("AddingBthreadsTest") {
            @Override
            protected void setupProgramScope() {
                loadJavascriptFile("AddingBthreads.js");
            }
        };
    }
    
    @Test
    public void superStepTest() throws InterruptedException {
        
        final BEvent parentDone = new BEvent("parentDone");
        final BEvent kidADone = new BEvent("kidADone");
        final BEvent kidBDone = new BEvent("kidBDone");
        
        BJavascriptProgram sut = buildProgram();
        sut.addListener( new StreamLoggerListener() );
        InMemoryEventLoggingListener eventLogger = sut.addListener( new InMemoryEventLoggingListener() );
        
        sut.start();
        EventSet kiddies = bp.eventsets.ComposableEventSet.anyOf(kidADone, kidBDone);
        EventPattern expected = new EventPattern()
                .append(kiddies)
                .append(kiddies)
                .append(parentDone)
                .append(kiddies)
                .append(kiddies)
                .append(parentDone);
        assertTrue( expected.matches(eventLogger.getEvents()) );
    }

}