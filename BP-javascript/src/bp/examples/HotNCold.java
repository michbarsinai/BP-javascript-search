package bp.examples;

import bp.Arbiter;
import bp.BEvent;
import bp.BPJavascriptApplication;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * Created by orelmosheweinstock on 3/24/15.
 */
public class HotNCold extends BPJavascriptApplication {

    private class HotEvent extends BEvent {

        public HotEvent() {
            _name = "hotEvent";
            _outputEvent = true;
        }
    }

    private class ColdEvent extends BEvent {

        public ColdEvent() {
            _name = "coldEvent";
            _outputEvent = true;
        }
    }

    private class AllDoneEvent extends BEvent {

        public AllDoneEvent() {
            _name = "ALLDONE";
            _outputEvent = true;
        }
    }

    public HotNCold() {
        super();
        _bp.setName("HotNCold");
        _arbiter = new Arbiter();
        _bp.setArbiter(_arbiter);

        evaluateInGlobalScope("out/production/BP-javascript/bp/examples/HotNCold.js");

        setupBThreadScopes();
    }

    @Test
    public void hotNColdTest() {
        final HotNCold hnc = new HotNCold();
        hnc.start();
        System.out.println("starting output event read loop");
//      imagine this is run in a separate thread on another machine
        BEvent outputEvent = hnc._bp.getOutputEvent();
        while (!outputEvent.getName().equals("ALLDONE")) {
            System.out.println("program emitted " + outputEvent);
            outputEvent = hnc._bp.getOutputEvent();
        }
        System.out.println("got ALLDONE event");
    }

    @Override
    protected void setupGlobalScope() {
        super.setupGlobalScope();
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        try {
            cx.setOptimizationLevel(-1); // must use interpreter mode
            _globalScope.put("hotEvent", _globalScope,
                    Context.javaToJS(new HotEvent(), _globalScope));
            _globalScope.put("coldEvent", _globalScope,
                    Context.javaToJS(new ColdEvent(), _globalScope));
            _globalScope.put("allDone", _globalScope,
                    Context.javaToJS(new AllDoneEvent(), _globalScope));
        } finally {
            Context.exit();
        }
    }
}