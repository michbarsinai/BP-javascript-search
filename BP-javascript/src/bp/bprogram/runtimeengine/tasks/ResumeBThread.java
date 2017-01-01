package bp.bprogram.runtimeengine.tasks;

import bp.bprogram.runtimeengine.BSyncStatement;
import bp.events.BEvent;
import bp.bprogram.runtimeengine.BThreadSyncSnapshot;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContinuationPending;

/**
 * A task to resume a BThread from a BSync operation.
 */
public class ResumeBThread extends BPEngineTask {
    private final BThreadSyncSnapshot bss;
    private final BEvent event;

    public ResumeBThread(BThreadSyncSnapshot aBThread, BEvent selectedEvent) {
        bss = aBThread;
        event = selectedEvent;
    }

    @Override
    protected BThreadSyncSnapshot run(Context jsContext) {
        try {
            Object toResume = bss.getContinuation();
            Object eventInJS = Context.javaToJS(event, bss.getScope());
            jsContext.resumeContinuation(toResume, bss.getScope(), eventInJS); // may throw CapturedBSync
            return null;
            
        } catch (ContinuationPending cbs) {  
            return bss.copyWith(cbs.getContinuation(), (BSyncStatement) cbs.getApplicationState());
        } 
    }

    @Override
    public String toString() {
        return String.format("[ResumeBThread: %s event:%s]", bss, event);
    }
}
