package bp.bprogram;

import bp.events.BEvent;
import bp.eventselection.EventSelectionStrategy;
import java.util.List;

/**
 * Holds information needed at BSync, such as {@link RWBStatement}s and external events.
 * 
 * <p>
 * Future versions might also contain hinting data for custom {@link EventSelectionStrategy}s.
 * </p>
 * 
 * @author michael
 */
public class BSyncState {
    
    private final List<RWBStatement> statements;
    private final List<BEvent> externalEvents;

    public BSyncState(List<RWBStatement> statements, List<BEvent> externalEvents) {
        this.statements = statements;
        this.externalEvents = externalEvents;
    }

    public List<BEvent> getExternalEvents() {
        return externalEvents;
    }

    public List<RWBStatement> getStatements() {
        return statements;
    }
    
}
