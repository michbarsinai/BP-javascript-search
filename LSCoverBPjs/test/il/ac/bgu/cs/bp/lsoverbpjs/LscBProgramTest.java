/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lsoverbpjs;

import bp.bprogram.BProgram;
import bp.bprogram.BThread;
import bp.bprogram.listeners.BProgramListener;
import bp.bprogram.listeners.StreamLoggerListener;
import bp.events.BEvent;
import bp.eventselection.EventSelectionResult;
import org.junit.Test;
import il.ac.bgu.cs.bp.lscoverbpjs.LscBProgram;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author michael
 */
public class LscBProgramTest {
    
    /**
     * Just run a piece of code in a LscBProgram container.
     * @throws InterruptedException 
     */
    @Test
    public void sanityTest() throws InterruptedException {
        LscBProgram sut = new LscBProgram() {
            @Override
            protected String getLscBpjCode() {
                return "var msg = lsc.Message('f@1','t@3','banana?','c1');\n"
                     + "lsc.Enabled(msg);";
            }
        };
        
        sut.start();
    }
    
    @Test
    public void testBlocking() throws Exception {
        final AtomicBoolean result = new AtomicBoolean();
        
        LscBProgram sut = new LscBProgram() {
            @Override
            protected String getLscBpjCode() {
                return  "bpjs.registerBThread( 'requestor', function(){\n" +
                        "  bsync( {request:bpjs.Event('anEvent')} );\n" +
                        "});\n" +
                        "bpjs.registerBThread( 'blocker', function(){\n" +
                        "  bsync( {block:bpjs.Event('anEvent')} );\n" +
                        "});";
            }
        };
        System.out.println("Test Blocking");
        sut.addListener( new BProgramListener() {
            @Override
            public void started(BProgram bp) {
            }

            @Override
            public void ended(BProgram bp) {
            }

            @Override
            public void bthreadAdded(BProgram bp, BThread theBThread) {
            }

            @Override
            public void bthreadRemoved(BProgram bp, BThread theBThread) {
            }

            @Override
            public void eventSelected(BProgram bp, BEvent theEvent) {
                fail("No event should have been selected");
            }

            @Override
            public void superstepDone(BProgram bp, EventSelectionResult.EmptyResult emptyResult) {
                result.set( emptyResult instanceof EventSelectionResult.Deadlock );
            }
        });
        sut.addListener( new StreamLoggerListener());
        sut.start();
        assertTrue("Program did not deadlock, while it should have.", result.get() );
        System.out.println("/ Test Blocking");
    }
}
