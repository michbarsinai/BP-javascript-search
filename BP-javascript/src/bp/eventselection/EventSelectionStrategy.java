package bp.eventselection;

import bp.bprogram.runtimeengine.BSyncStatement;
import bp.events.BEvent;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Strategy for selecting events from a set of {@link BSyncStatement}s.
 * The selection result might be an event, a deadlock detection, or no event
 * (i.e. nobody requested anything).
 * 
 * This class has two methods, one for creating the set of selectable events, and 
 * the other for selecting the actual event. The former is useful in both execution 
 * and model checking. The latter, in execution only.
 * 
 * @author michael
 */
public interface EventSelectionStrategy {
    
    /**
     * Creates the set of selectable events, given the {@link BSyncStatement}s from 
     * all participating BThreads.
     * @param statements statements of all participating BThreads.
     * @param externalEvents events queued by external processes.
     * @return A set of events that may be selected for execution.
     */
    Set<BEvent> selectableEvents(Set<BSyncStatement> statements, List<BEvent> externalEvents);
    
    /**
     * Selects an event for execution from the parameter {@code selectableEvents}, or
     * returns an {@link EventSelectionResult.EmptyResult} in case no suitable event
     * is found.
     * 
     * @param statements statements of all participating BThreads.
     * @param externalEvents events queued by external processes.
     * @param selectableEvents A set of events to select from
     * @return An event selection result, or no result.
     */
    Optional<EventSelectionResult> select(Set<BSyncStatement> statements, 
                                          List<BEvent> externalEvents,
                                          Set<BEvent> selectableEvents );
    
}
