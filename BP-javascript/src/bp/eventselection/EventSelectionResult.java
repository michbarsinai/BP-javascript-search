package bp.eventselection;

import bp.events.BEvent;
import java.util.Collections;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

/**
 * The result of trying to select an event. There are two options here, hence the 
 * algebraic data type design. Options are either an event is selected, or no
 * event is selected. In the case of selected event, an {@link EventSelected}
 * instance is returned. This instance contains the selected event an a list of indices
 * to remove from the external event queue (typically because they contain instances
 * equals to the selected event).
 * 
 * If no event is selected, the sole {@link EmptyResult} instance is returned.
 * 
 * @author michael
 */
public abstract class EventSelectionResult {
    
    public static interface Visitor<R> {
        R visit( EventSelected se );
        R visit( EmptyResult er );
    }
    
    public static abstract class VoidVisitor implements Visitor<Void> {

        @Override
        public Void visit(EventSelected se) {
            visitImpl(se);
            return null;
        }

        @Override
        public Void visit(EmptyResult dl) {
            visitImpl(dl);
            return null;
        }

        protected abstract void visitImpl( EventSelected se );
        protected abstract void visitImpl( EmptyResult dl );
    }
    
    public static final EmptyResult EMPTY_RESULT = EmptyResult.INSTANCE;
    
    private EventSelectionResult(){
        // Prevent external subclassing.
    }
    
    public abstract <R> R accept( Visitor<R> v );
    
    /**
     * A result where an event is selected. Contains the event itself, and a
     * set of indices to remove from the external event queue. This set allows 
     * the strategy to make the external event queue act like, e.g., a set of 
     * events rather than a list.
     */
    public static class EventSelected extends EventSelectionResult {
        
        private final BEvent selectedEvent;
        private final Set<Integer> indicesToRemove;
        
        public EventSelected(BEvent aSelectedEvent, Set<Integer> someIndices) {
            selectedEvent = aSelectedEvent;
            indicesToRemove = someIndices;
            Set<Integer> negIndices = indicesToRemove.stream().filter( i -> i<0 ).collect( toSet() );
            if ( !negIndices.isEmpty() ) {
                throw new IllegalArgumentException("The following indices are illegal: " 
                        + negIndices.stream().map(Object::toString).collect(joining(",")) );
            }
        }
        
        public EventSelected(BEvent anEvent) {
            this(anEvent, Collections.emptySet());
        }

        @Override
        public <R> R accept( Visitor<R> v ) {
            return v.visit( this );
        }
        
        @Override
        public String toString() {
            return "[SelectedExternal event:" + getEvent() + " indices:" + getIndicesToRemove() + "]";
        }

        public BEvent getEvent() {
            return selectedEvent;
        }

        public Set<Integer> getIndicesToRemove() {
            return indicesToRemove;
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + this.indicesToRemove.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final EventSelected other = (EventSelected) obj;
            return selectedEvent.equals(other.getEvent())
                    && indicesToRemove.equals( other.getIndicesToRemove() );
        }
       
    }
    
    /**
     * Could not select an event.
     */
    public static class EmptyResult extends EventSelectionResult {
        
        static final EmptyResult INSTANCE = new EmptyResult();
        
        private EmptyResult(){
            // prevent instance creation outside of this class.
        }
        
        @Override
        public <R> R accept( Visitor<R> v ) {
            return v.visit( this );
        }
        
        @Override
        public String toString(){
            return "[EmptyResult]";
        }
    }
}
