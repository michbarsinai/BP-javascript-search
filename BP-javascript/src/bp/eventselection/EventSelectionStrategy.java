package bp.eventselection;

import bp.bprogram.RWBStatement;
import java.util.Collection;

/**
 * Strategy for selecting events from a collection of {@link RWBStatement}s.
 * The selection result might be an event, a deadlock detection, or no event
 * (i.e. nobody requested anything).
 * 
 * @author michael
 */
public interface EventSelectionStrategy {
   
    EventSelectionResult select(Collection<RWBStatement> statements);
    
}
