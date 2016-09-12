package bp.search;

import bp.bprogram.BProgram;
import bp.bprogram.BThread;
import bp.search.bthreads.SimulatorBThread;
import bp.search.events.SimStartEvent;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by orelmosheweinstock on 4/6/15.
 */
public abstract class BPSearchApplication extends BProgram {

    protected List<BThread> _simBThreads;

    public BPSearchApplication() {
        super();
        _simBThreads = new ArrayList<>();
    }

    @Override
    protected void setupGlobalScope() {
        super.setupGlobalScope();
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        try {
            Object simStartInJS = Context.javaToJS(SimStartEvent.getInstance(), globalScope);
            globalScope.put("simStart", globalScope, simStartInJS);
        } finally {
            Context.exit();
        }

        InputStream script = BPSearchApplication.class.getResourceAsStream("globalScopeInit.js");
        evaluateInGlobalScope(script, "BPSearchGlobalInit");
    }

    public SimulatorBThread registerSimBThread(String name, Function func) {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        try {
            SimulatorBThread simBT = new SimulatorBThread(name, func);
            add(simBT);
            _simBThreads.add(simBT);
            return simBT;
        } finally {
            Context.exit();
        }
    }

}
