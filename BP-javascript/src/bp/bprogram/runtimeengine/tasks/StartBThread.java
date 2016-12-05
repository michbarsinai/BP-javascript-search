package bp.bprogram.runtimeengine.tasks;

import bp.bprogram.runtimeengine.BSyncStatement;
import bp.bprogram.runtimeengine.BThreadSyncSnapshot;
import java.util.Optional;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContinuationPending;

/**
 * A task to start a BThread.
 */
public class StartBThread extends BPEngineTask {
    private final BThreadSyncSnapshot bthreadBss;

    public StartBThread(BThreadSyncSnapshot aBThread) {
        bthreadBss = aBThread;
    }

    @Override
    protected Optional<BThreadSyncSnapshot> run(Context jsContext) {
         try {
            jsContext.callFunctionWithContinuations(bthreadBss.getEntryPoint(), bthreadBss.getScope(), new Object[0]);
            return Optional.empty();

        } catch (ContinuationPending cbs) {
            return Optional.of( bthreadBss.copyWith(cbs, (BSyncStatement) cbs.getApplicationState()));
        }
    }
   
    @Override
    public String toString() {
        return "[StartBThread " + bthreadBss.getName() + "]";
    }
}
