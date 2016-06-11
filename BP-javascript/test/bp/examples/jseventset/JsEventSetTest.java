/*
 *  Author: Michael Bar-Sinai
 */
package bp.examples.jseventset;

import bp.bprogram.BProgram;
import bp.bprogram.SingleResourceBProgram;
import bp.bprogram.listeners.StreamLoggerListener;
import java.net.URISyntaxException;
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
        
        bpr.start();
    }
}
