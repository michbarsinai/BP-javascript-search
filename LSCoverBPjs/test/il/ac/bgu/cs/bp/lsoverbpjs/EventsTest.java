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
    public void setUp() throws InterruptedException {
        prog = new LscBProgram() {
            @Override
            protected void setupProgramScope(Scriptable aScope) {
                aScope.put("events", aScope, Context.javaToJS(events, aScope));
                super.setupProgramScope(aScope);
            }
            
            @Override
            protected String getLscBpjCode() {
                return "var i=1;\n"
                     + "events.put('leaveLiteral',  lsc.Leave('loc@1','chart'));\n"
                     + "events.put('leaveComputedLit', lsc.Leave('loc@'+1,'chart'));\n"
                     + "events.put('leaveComputedVar', lsc.Leave('loc@'+i,'chart'));\n";
            }
        };
        prog.start();
    }
    

    @Test
    public void testEquals() throws InterruptedException {
        BEvent leaveLiteral = events.get("leaveLiteral");
        BEvent leaveComputedLit = events.get("leaveComputedLit");
        BEvent leaveComputedVar = events.get("leaveComputedVar");
        
        assertTrue( leaveLiteral.contains(leaveLiteral) ); // sanity
        assertTrue( leaveLiteral.contains(leaveComputedLit) );
        assertTrue( leaveLiteral.contains(leaveComputedVar) );
        
        assertTrue( leaveComputedLit.contains(leaveLiteral) );
        assertTrue( leaveComputedLit.contains(leaveComputedVar) );
        
        assertTrue( leaveComputedVar.contains(leaveLiteral) );
        assertTrue( leaveComputedVar.contains(leaveComputedLit) );
        
    }
    
}
