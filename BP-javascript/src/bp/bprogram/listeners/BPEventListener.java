package bp.bprogram.listeners;

import bp.bprogram.BProgram;
import bp.bprogram.BThread;
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
    public void bthreadAdded(BProgram bp, BThread theBThread) {
    }

    @Override
    public void bthreadRemoved(BProgram bp, BThread theBThread) {
    }

    @Override
    public void superstepDone(BProgram bp, EventSelectionResult.EmptyResult emptyResult) {
    }
    
}
