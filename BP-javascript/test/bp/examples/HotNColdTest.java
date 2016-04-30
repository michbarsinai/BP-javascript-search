package bp.examples;

import bp.Arbiter;
import bp.BEvent;
import bp.BJavascriptProgram;
import bp.validation.eventpattern.EventPattern;
import org.junit.Test;
import org.mozilla.javascript.Context;

import java.util.Arrays;
import static org.junit.Assert.assertTrue;
import org.mozilla.javascript.Scriptable;

/**
 * @author orelmosheweinstock
 * @author @michbarsinai
 */
public class HotNColdTest extends BJavascriptProgram {
    
    final BEvent hotEvent = new BEvent("hotEvent");
    final BEvent coldEvent = new BEvent("coldEvent");
    final BEvent allDoneEvent = new BEvent("allDone");

    public HotNColdTest() {
        super("HotNCold");
        _arbiter = new Arbiter();
        setArbiter(_arbiter);
    }

    @Test
    public void superStepTest() throws InterruptedException {
        setupGlobalScope();
        setupBThreadScopes();
        superStep();
        
        EventPattern expected = new EventPattern().append(coldEvent).append(hotEvent)
                .append(coldEvent).append(hotEvent)
                .append(coldEvent).append(hotEvent)
                .append(allDoneEvent);
        assertTrue( expected.matches(eventLog) );
    }
    
    public void hotNColdTest() throws InterruptedException {
        final HotNColdTest hnc = new HotNColdTest();
        hnc.start();
        System.out.println("starting output event read loop");
 
        BEvent outputEvent = hnc.readOutputEvent();
        while (! outputEvent.equals(allDoneEvent) ) {
            System.out.println("program emitted " + outputEvent);
            outputEvent = hnc.readOutputEvent();
        }
        System.out.println("got ALLDONE event");
    }

    @Override
    protected void setupProgramScope() {
        loadJavascriptFile("HotNCold.js");
        
        final Scriptable globalScope = getGlobalScope();
        Arrays.asList(hotEvent, coldEvent, allDoneEvent).forEach(
                e -> globalScope.put( e.getName(), globalScope, Context.javaToJS(e, globalScope)));
    }
}