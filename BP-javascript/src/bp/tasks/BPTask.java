package bp.tasks;

import bp.BPApplication;
import java.util.concurrent.Callable;

import static bp.BProgramControls.debugMode;

/**
 * Base class for a parallel task executed during the execution of a {@link BPApplication}.
 * @author moshewe
 */
public abstract class BPTask implements Callable<Void>{

    @Override
    public Void call() throws Exception {
        run();
        return null;
    }
    
    protected abstract void run();

    public void bplog(String string) {
        if (debugMode)
            System.out.println("[" + this + "]: " + string);
    }

}
