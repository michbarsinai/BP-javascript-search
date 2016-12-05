package bp.eventselection;

import bp.bprogram.runtimeengine.BSyncStatement;
import bp.events.BEvent;
import java.util.List;
import java.util.Set;

/**
 * Strategy for selecting events from a set of {@link BSyncStatement}s.
 * The selection result might be an event, a deadlock detection, or no event
 * (i.e. nobody requested anything).
 * 
 * @author michael
 */
public interface EventSelectionStrategy {
   
    EventSelectionResult select(Set<BSyncStatement> statements, List<BEvent> externalEvents);
    
}
