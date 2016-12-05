package bp.bprogram.runtimeengine.listeners;

import bp.bprogram.runtimeengine.BProgram;
import bp.bprogram.runtimeengine.BThreadSyncSnapshot;
import bp.events.BEvent;
import bp.eventselection.EventSelectionResult;

/**
 * An object interested in the life-cycle of a {@link BProgram}.
 * @author michael
 */
public interface BProgramListener {
    
    void started( BProgram bp );
    void ended( BProgram bp );
    void bthreadAdded( BProgram bp, BThreadSyncSnapshot theBThread );
    void bthreadRemoved( BProgram bp, BThreadSyncSnapshot theBThread );
    void eventSelected( BProgram bp, BEvent theEvent );
    void superstepDone( BProgram bp, EventSelectionResult.EmptyResult emptyResult );
    
}
