package bp.eventselection;

import bp.events.BEvent;
import java.util.Objects;

/**
 * The result of trying to select an event. This is an algebraic datatype, 
 * with the possible result types as internal classes.
 * 
 * @author michael
 */
public abstract class EventSelectionResult {
    
    public static interface Visitor<R> {
        R visit( Selected se );
        R visit( Deadlock dl );
        R visit( NoneRequested ne );
    }
    
    public static final Deadlock DEADLOCK = new Deadlock();
    public static final NoneRequested NONE_REQUESTED = new NoneRequested();
    public static Selected selected( BEvent evt ) {
        return new Selected(evt);
    }
    
    private EventSelectionResult(){
        // Prevent external subclassing.
    }
    
    /**
     * Base class for results that do not contain an event for some reason.
     */
    public abstract static class EmptyResult extends EventSelectionResult {
        
    }
    
    public abstract <R> R accept( Visitor<R> v );
    
    public static class Selected extends EventSelectionResult {
        private final BEvent selectedEvent;

        public Selected(BEvent selectedEvent) {
            this.selectedEvent = selectedEvent;
        }

        public BEvent getEvent() {
            return selectedEvent;
        }
        
        @Override
        public <R> R accept( Visitor<R> v ) {
            return v.visit( this );
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.selectedEvent);
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
            final Selected other = (Selected) obj;
            return Objects.equals(this.selectedEvent, other.selectedEvent);
        }
        
        @Override
        public String toString(){
            return "[Selected " + selectedEvent + "]";
        }
        
    }
    
    /**
     * No event was selected since all requested events are blocked.
     */
    public static class Deadlock extends EmptyResult {
        @Override
        public <R> R accept( Visitor<R> v ) {
            return v.visit( this );
        }
        
        @Override
        public String toString(){
            return "[Deadlock]";
        }
    }
    
    
    /**
     * No events selected since no event was requested.
     */
    public static class NoneRequested extends EmptyResult {
        @Override
        public <R> R accept( Visitor<R> v ) {
            return v.visit( this );
        }
        
        @Override
        public String toString(){
            return "[NoneRequested]";
        }
    }
}