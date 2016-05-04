package bp.eventselection;

import bp.bprogram.BSyncState;

/**
 * Strategy for selecting events from a {@link BSyncState}.
 * The selection result might be an event, a deadlock detection, or no event
 * (i.e. nobody requested anything).
 * 
 * @author michael
 */
public interface EventSelectionStrategy {
   
    EventSelectionResult select(BSyncState state);
    
}
