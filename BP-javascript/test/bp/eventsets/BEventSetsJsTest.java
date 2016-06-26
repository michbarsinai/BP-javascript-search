/*
 *  Author: Michael Bar-Sinai
 */
package bp.eventsets;

import bp.events.*;
import bp.bprogram.BProgram;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author michael
 */
public class BEventSetsJsTest {
    
    BProgram prog;
    Map<String, BEvent> events = new HashMap<>();
    Map<String, EventSet> eventSets = new HashMap<>();
    
    @Before
    public void setUp() {
        prog = new BProgram() {
            @Override
            protected void setupProgramScope(Scriptable aScope) {
                aScope.put("events", aScope, Context.javaToJS(events, aScope));
                aScope.put("eventSets", aScope, Context.javaToJS(eventSets, aScope));
                aScope.put("test", aScope, Context.javaToJS(BEventSetsJsTest.this, aScope));
                evaluateInGlobalContext(aScope,
                        "eventSets.put(\"esName\", bpjs.EventSet( function(e){ return e.name==\"Name\"; }) );\n" +
                        "eventSets.put(\"esDataObjVizIsViz\", bpjs.EventSet( function(e){ return (e.data != undefined) && e.data.viz===\"viz\"; }) );\n" +
                        "eventSets.put(\"esDataIsViz\", bpjs.EventSet( function(e){ return e.data==\"viz\"; }) );\n" +
                        "events.put( \"eName\", bpjs.Event(\"Name\") );\n" +
                        "events.put( \"eNotName\", bpjs.Event(\"NotName\") );\n" +
                        "events.put( \"eVizObj\", bpjs.Event(\"name\", {viz:\"viz\"}) );\n" +
                        "events.put( \"eViz\", bpjs.Event('name', 'viz') );"
                        ,
                        "inline script" );
            }
        };
    }
    

    @Test
    public void testContains_Name() throws InterruptedException {
        prog.start(); // evaluate the Javascript code.
        
        BEvent eName = events.get("eName");
        BEvent eNotName = events.get("eNotName");
        EventSet esName = eventSets.get("esName");
        
        assertTrue( esName.contains(eName) );
        assertFalse( esName.contains(eNotName) );
    }
    
    @Test
    public void testContains_Data() throws Exception {
        prog.start(); // evaluate the Javascript code.
        
        BEvent eVizObj = events.get("eVizObj");
        BEvent eViz = events.get("eViz");
        BEvent eName = events.get("eName");
        EventSet esDataVizIsViz = eventSets.get("esDataObjVizIsViz");
        EventSet esDataIsViz = eventSets.get("esDataIsViz");
        
        System.out.println("eViz = " + eViz);
        
        assertTrue( esDataVizIsViz.contains(eVizObj) );
        assertTrue( esDataIsViz.contains(eViz) );
        
        assertFalse( esDataIsViz.contains(eVizObj) );
        assertFalse( esDataVizIsViz.contains(eViz) );
        assertFalse( esDataVizIsViz.contains(eName) );
        assertFalse( esDataIsViz.contains(eName) );
        
    }
   
    
    public void print(Object o) {
        System.out.println("From JS: " + o);
    }
}
