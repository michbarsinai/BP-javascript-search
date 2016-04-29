package bp.eventsets;

/**
 * 
 * Utility class for commonly used event sets.
 * 
 */
public class EventSets {
    public final static EmptyEventSet none = new EmptyEventSet();
    
    /**
     * An event set that contains all events.
     * @author Bertrand Russel
     */
    public final static EventSet all = new EventSet() {

        @Override
        public boolean contains(Object o) {
            return (o instanceof EventSet);
        }

        @Override
        public String toString() {
            return ("{AllEvents}");
        }
    };
}
