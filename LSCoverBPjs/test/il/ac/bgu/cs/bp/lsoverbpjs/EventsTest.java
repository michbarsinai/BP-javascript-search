/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lsoverbpjs;

import bp.events.BEvent;
import il.ac.bgu.cs.bp.lscoverbpjs.LscBProgram;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author michael
 */
public class EventsTest {
    
    LscBProgram prog;
    Map<String, BEvent> events = new HashMap<>();
    
    @Before
    public void setUp() {
        prog = new LscBProgram() {
            @Override
            protected void setupProgramScope(Scriptable aScope) {
                aScope.put("events", aScope, Context.javaToJS(events, aScope));
                super.setupProgramScope(aScope);
            }
            
            @Override
            protected String getLscBpjCode() {
                return "events.put('messagePassed1', lsc.Message('f','t','chart-id','content'));\n"
                        + "events.put('messagePassed2', lsc.Message('f','t','chart-id','content'));\n";
            }
        };
    }
    

    @Test
    public void testEquals() throws InterruptedException {
        prog.start(); // evaluate the Javascript code.
        
        assertTrue( events.get("messagePassed1").contains(events.get("messagePassed1")));
        assertTrue( events.get("messagePassed1").contains(events.get("messagePassed2")));
        assertTrue( events.get("messagePassed2").contains(events.get("messagePassed1")));
    }
    
}
