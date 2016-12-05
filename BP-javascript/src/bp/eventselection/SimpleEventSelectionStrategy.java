package bp.eventselection;

import bp.bprogram.runtimeengine.BProgramSyncSnapshot;
import bp.events.BEvent;
import bp.bprogram.runtimeengine.BSyncStatement;
import bp.eventsets.ComposableEventSet;
import bp.eventsets.EventSet;
import bp.eventsets.Events;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An event selection strategy that randomly selects an event that's requested and not blocked.
 * If no internal event can be found, it attempts to select the first unblocked event from the external queue.
 * 
 * @author michael
 */
public class SimpleEventSelectionStrategy implements EventSelectionStrategy {
    
    private final Random rnd;
    private final long seed;
    
    public SimpleEventSelectionStrategy( long seed ) {
        rnd = new Random(seed);
        this.seed = seed;
    }
    
    public SimpleEventSelectionStrategy() {
        this( new Random().nextLong() );
    }
    
    @Override
    public EventSelectionResult select(Set<BSyncStatement> statements, List<BEvent> externalEvents ) {
        
        EventSet blocked = ComposableEventSet.anyOf(statements.stream()
                .filter( stmt -> stmt!=null )
                .map(BSyncStatement::getBlock )
                .filter(r -> r != Events.emptySet )
                .collect( Collectors.toSet() ) );
        
        // Corner case, not sure this is even possible.
        if ( statements.isEmpty() ) return selectExternal(externalEvents, blocked, EventSelectionResult.NONE_REQUESTED);
        
        Set<BEvent> requested = statements.stream()
                .filter( stmt -> stmt!=null )
                .flatMap( stmt -> stmt.getRequest().stream() )
                .collect( Collectors.toSet() );
        
        // No internal events requested, defer to externals.
        if ( requested.isEmpty() ) return selectExternal(externalEvents, blocked, EventSelectionResult.NONE_REQUESTED);
        
        // Let's see what internal events are requested and not blocked (if any).
        List<BEvent> requestedAndNotBlocked = requested.stream()
                .filter( req -> !blocked.contains(req) )
                .collect( Collectors.toList() );
        
        return requestedAndNotBlocked.isEmpty() ?
                selectExternal(externalEvents, blocked, EventSelectionResult.DEADLOCK)
                : EventSelectionResult.selected( requestedAndNotBlocked.get( rnd.nextInt(requestedAndNotBlocked.size())) );
    }
    
    private EventSelectionResult selectExternal( List<BEvent> externals, EventSet blocked, EventSelectionResult returnOnEmpty ) {
        
        if ( externals.isEmpty() ) {
            return returnOnEmpty;
        }
        
        for ( ListIterator<BEvent> it = externals.listIterator(); it.hasNext() ; ) {
            BEvent candidate = it.next();
            if ( ! blocked.contains(candidate) ) {
                return EventSelectionResult.selectedExternal(candidate, it.previousIndex());
            }
        }
        
        return EventSelectionResult.DEADLOCK;
    }
    
    public long getSeed() {
        return seed;
    }
}
