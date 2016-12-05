package bp.bprogram.runtimeengine;

import bp.events.BEvent;
import java.util.List;

/**
 * The state of a {@link BProgram} at {@code bsync}.
 * Holds information needed at BSync, such as {@link BSyncStatement}s and external events.
 * 
 * <p>
 * For search: this class would serve as (part of?) the nodes in the search tree.
 * </p>
 * 
 * @author michael
 */
public class BProgramSyncSnapshot {
    
    private final List<BSyncStatement> statements;
    private final List<BEvent> externalEvents;

    public BProgramSyncSnapshot(List<BSyncStatement> statements, List<BEvent> externalEvents) {
        this.statements = statements;
        this.externalEvents = externalEvents;
    }

    public List<BEvent> getExternalEvents() {
        return externalEvents;
    }

    public List<BSyncStatement> getStatements() {
        return statements;
    }
    
}
