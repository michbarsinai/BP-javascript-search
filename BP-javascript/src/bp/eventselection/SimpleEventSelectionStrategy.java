package bp.eventselection;

import bp.BEvent;
import bp.RWBStatement;
import bp.eventsets.ComposableEventSet;
import bp.eventsets.EventSet;
import bp.eventsets.EventSets;
import bp.eventsets.Requestable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An event selection strategy that arbitrarily selects an event that's requested and not blocked.
 * 
 * @author michael
 */
public class SimpleEventSelectionStrategy implements EventSelectionStrategy {

    @Override
    public EventSelectionResult select(Collection<RWBStatement> statements) {
        if ( statements.isEmpty() ) return EventSelectionResult.NONE_REQUESTED;
        
        statements.forEach( s->System.out.println(s) );
        
        Set<Requestable> requested = statements.stream()
                .filter( stmt -> stmt!=null )
                .map( RWBStatement::getRequest )
                .filter( r -> r != EventSets.none )
                .collect( Collectors.toSet() );
        
        if ( requested.isEmpty() ) return EventSelectionResult.NONE_REQUESTED;
        
        EventSet blocked = ComposableEventSet.anyOf(
                statements.stream()
                .filter( stmt -> stmt!=null )
                .map( RWBStatement::getBlock )
                .filter( r -> r != EventSets.none )
                .collect( Collectors.toSet() ) );
        
        Set<Requestable> requestedAndNotBlocked = requested.stream()
                .filter( req -> !blocked.contains(req) ).collect( Collectors.toSet() );
        
        return requestedAndNotBlocked.isEmpty() ?
                EventSelectionResult.DEADLOCK
                : EventSelectionResult.selected( (BEvent)requestedAndNotBlocked.iterator().next() );
            
    }
}
