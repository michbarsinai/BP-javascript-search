package bp.bprogram.listeners;

import bp.bprogram.BProgram;
import bp.bprogram.BThread;
import bp.events.BEvent;
import bp.eventselection.EventSelectionResult;

/**
 * An object interested in the life-cycle of a {@link BProgram}.
 * @author michael
 */
public interface BProgramListener {
    
    void started( BProgram bp );
    void ended( BProgram bp );
    void bthreadAdded( BProgram bp, BThread theBThread );
    void bthreadRemoved( BProgram bp, BThread theBThread );
    void eventSelected( BProgram bp, BEvent theEvent );
    void superstepDone( BProgram bp, EventSelectionResult.EmptyResult emptyResult );
    
}
