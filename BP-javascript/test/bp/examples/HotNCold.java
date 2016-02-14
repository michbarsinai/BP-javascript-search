package bp.examples;

import bp.Arbiter;
import bp.BEvent;
import bp.BJavascriptProgram;
import org.junit.Test;
import org.mozilla.javascript.Context;

import java.util.Arrays;
import org.mozilla.javascript.Scriptable;

/**
 * @author orelmosheweinstock
 * @author @michbarsinai
 */
public class HotNCold extends BJavascriptProgram {
    
    final BEvent hotEvent = new BEvent("hotEvent", true);
    final BEvent coldEvent = new BEvent("coldEvent", true);
    final BEvent allDoneEvent = new BEvent("allDone", true);

    public HotNCold() {
        super("HotNCold");
        _arbiter = new Arbiter();
        setArbiter(_arbiter);
    }

    @Test
    public void hotNColdTest() throws InterruptedException {
        final HotNCold hnc = new HotNCold();
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