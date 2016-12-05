package bp.bprogram.runtimeengine.listeners;

import bp.bprogram.runtimeengine.BProgram;
import bp.bprogram.runtimeengine.BThreadSyncSnapshot;
import bp.eventselection.EventSelectionResult;

/**
 * A simple listener that is only invoked when an event is selected.
 * @author Aviran
 */
public abstract class BPEventListener implements BProgramListener {

    @Override
    public void started(BProgram bp) {
    }

    @Override
    public void ended(BProgram bp) {
    }

    @Override
    public void bthreadAdded(BProgram bp, BThreadSyncSnapshot theBThread) {
    }

    @Override
    public void bthreadRemoved(BProgram bp, BThreadSyncSnapshot theBThread) {
    }

    @Override
    public void superstepDone(BProgram bp, EventSelectionResult.EmptyResult emptyResult) {
    }
    
}
