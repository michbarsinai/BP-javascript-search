package bp.search.bthreads;

import bp.bprogram.BThread;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.InputStream;

/**
 * Created by orelmosheweinstock on 4/25/15.
 */
public class SimulatorBThread extends BThread {

    public boolean _simMode = false;

    public SimulatorBThread(String name, Function func) {
        super(name, func);
    }

    @Override
    public void setupScope(Scriptable programScope) {
        super.setupScope(programScope);
        InputStream ios = SimulatorBThread.class.getResourceAsStream("simscope.js");
        generateSubScope(programScope, ios, "SimScope");
    }
}
