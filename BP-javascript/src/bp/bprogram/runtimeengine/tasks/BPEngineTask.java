package bp.bprogram.runtimeengine.tasks;

import bp.bprogram.runtimeengine.BProgram;
import java.util.concurrent.Callable;
import bp.bprogram.runtimeengine.BThreadSyncSnapshot;
import java.util.Optional;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * Base class for a parallel task executed during the execution of a {@link BProgram}.
 * Provides facilities for opening and closing the javascript context, so that
 * sub-classes can just implement {@link #run(org.mozilla.javascript.Context)}
 * and forget about managing that.
 * 
 * @author moshewe
 * @author Michael
 */
public abstract class BPEngineTask implements Callable<Optional<BThreadSyncSnapshot>>{
    
    private Context jsContext;
    
    @Override
    public Optional<BThreadSyncSnapshot> call() throws Exception {
        try {
            openGlobalContext();
            return run(jsContext);
        } finally {
            closeGlobalContext();
        }
    }
    
    protected abstract Optional<BThreadSyncSnapshot> run(Context jsContext);

    private void openGlobalContext() {
        jsContext = ContextFactory.getGlobal().enterContext();
        jsContext.setOptimizationLevel(-1); // must use interpreter mode
    }
    
    private void closeGlobalContext() {
        Context.exit();
    }
}
