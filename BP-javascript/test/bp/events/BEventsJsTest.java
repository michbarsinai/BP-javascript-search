/*
 *  Author: Michael Bar-Sinai
 */
package bp.events;

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
public class BEventsJsTest {
    
    BProgram prog;
    Map<String, BEvent> events = new HashMap<>();
    
    @Before
    public void setUp() {
        prog = new BProgram() {
            @Override
            protected void setupProgramScope(Scriptable aScope) {
                aScope.put("events", aScope, Context.javaToJS(events, aScope));
                evaluateInGlobalContext(aScope,
                        "events.put('nameOnly1',       bpjs.Event('nameOnly'));\n"
                        + "events.put('nameOnly2',     bpjs.Event('nameOnly'));\n"
                        + "events.put('nameOnly-diff', bpjs.Event('nameOnly-diff'));\n"
                        + "events.put('withData1',           bpjs.Event('withData',{a:'a',b:'b',c:700}));\n"
                        + "events.put('withData2',           bpjs.Event('withData',{a:'a',b:'b',c:700}));\n"
                        + "events.put('withData2-reordered', bpjs.Event('withData',{b:'b',a:'a',c:700}));\n"
                        + "events.put('withData-diff1', bpjs.Event('withDataX',{a:'a',b:'b',c:700}));\n"
                        + "events.put('withData-diff2', bpjs.Event('withData',{b:'b',c:700}));\n"
                        + "events.put('withData-diff3', bpjs.Event('withData',{a:'a',b:'b',c:700,d:'x'}));\n"
                        + "events.put('withData-diff4', bpjs.Event('withData',{a:'x',b:'b',c:700}));\n"
                        + "events.put('withData-rec',   bpjs.Event('withData',{a:'x',b:'b',child:{a:2,b:'b'}}));\n"
                        + "events.put('withData-rec2',  bpjs.Event('withData',{a:'x',b:'b',child:{a:2,b:'b'}}));\n"
                        + "events.put('withPrimitiveData1',      bpjs.Event('withPrimitiveData',12));\n"
                        + "events.put('withPrimitiveData2',      bpjs.Event('withPrimitiveData',12));\n"
                        + "events.put('withPrimitiveData-diff1', bpjs.Event('withPrimitiveData',13));\n"
                        + "events.put('withPrimitiveData-diff2', bpjs.Event('withPrimitiveData','string'));\n"
                        + "events.put('withPrimitiveData-diff3', bpjs.Event('withPrimitiveData',{}));\n"
                        + "events.put('withPrimitiveData-diff4', bpjs.Event('withPrimitiveData',function(p){return p;}));\n"
                        ,
                        "inline script" );
            }
        };
    }
    

    @Test
    public void testContains_Name() throws InterruptedException {
        prog.start(); // evaluate the Javascript code.
        
        BEvent nameOnly1 = events.get("nameOnly1");
        BEvent nameOnly2 = events.get("nameOnly2");
        BEvent nameOnlyDiff = events.get("nameOnly-diff");
        
        assertTrue( nameOnly1.contains(nameOnly1) );
        assertTrue( nameOnly1.contains(nameOnly2) );
        assertTrue( nameOnly2.contains(nameOnly1) );
        assertFalse( nameOnly2.contains(nameOnlyDiff) );
    }
    
    @Test
    public void testContains_Object() throws Exception {
        prog.start(); // evaluate the Javascript code.
        
        BEvent withData1 = events.get("withData1");
        BEvent withData2 = events.get("withData2");
        BEvent withData2Ordered = events.get("withData2-reordered");
        BEvent withDataDiff1 = events.get("withData-diff1");
        BEvent withDataDiff2 = events.get("withData-diff2");
        BEvent withDataDiff3 = events.get("withData-diff3");
        BEvent withDataDiff4 = events.get("withData-diff4");
        BEvent withDataRec = events.get("withData-rec");
        BEvent withDataRec2 = events.get("withData-rec2");
        
        assertTrue( withData1.contains(withData1) );
        assertTrue( withData1.contains(withData2) );
        assertTrue( withData2.contains(withData1) );
        
        assertTrue( withData2Ordered.contains(withData1) );
        assertTrue( withData1.contains(withData2Ordered) );
        
        assertFalse( withData1.contains(withDataDiff1) );
        assertFalse( withData1.contains(withDataDiff2) );
        assertFalse( withData1.contains(withDataDiff3) );
        assertFalse( withData1.contains(withDataDiff4) );
        assertFalse( withData1.contains(withDataRec) );
        
        assertTrue( withDataRec2.contains(withDataRec) );
    }

    @Test
    public void testContains_Primitives() throws Exception {
        prog.start(); // evaluate the Javascript code.
    
        BEvent withPrimitiveData1 = events.get("withPrimitiveData1");
        BEvent withPrimitiveData2 = events.get("withPrimitiveData2");
        BEvent withPrimitiveDataDiff1 = events.get("withPrimitiveData-diff1");
        BEvent withPrimitiveDataDiff2 = events.get("withPrimitiveData-diff2");
        BEvent withPrimitiveDataDiff3 = events.get("withPrimitiveData-diff3");
        BEvent withPrimitiveDataDiff4 = events.get("withPrimitiveData-diff4");
        
        assertTrue( withPrimitiveData1.contains(withPrimitiveData1) );
        assertTrue( withPrimitiveData1.contains(withPrimitiveData2) );
        assertTrue( withPrimitiveData2.contains(withPrimitiveData1) );
        
        assertFalse( withPrimitiveData1.contains(withPrimitiveDataDiff1) );
        assertFalse( withPrimitiveData1.contains(withPrimitiveDataDiff2) );
        assertFalse( withPrimitiveData1.contains(withPrimitiveDataDiff3) );
        assertFalse( withPrimitiveData1.contains(withPrimitiveDataDiff4) );
        
        assertFalse( withPrimitiveDataDiff1.contains(withPrimitiveData1) );
        assertFalse( withPrimitiveDataDiff2.contains(withPrimitiveData1) );
        assertFalse( withPrimitiveDataDiff3.contains(withPrimitiveData1) );
        assertFalse( withPrimitiveDataDiff4.contains(withPrimitiveData1) );
    }
    
}
