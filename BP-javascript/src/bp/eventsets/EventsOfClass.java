package bp.eventsets;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A set containing all event instances from a class list.
 * 
 * @see Events#ofClass(java.lang.Class) 
 */

public class EventsOfClass  implements EventSet {

    private final Set<Class<?>> classes = new HashSet<>();

    public EventsOfClass(Class<?>... clsList) {
        classes.addAll(Arrays.asList(clsList));
    }

    public boolean contains(Object o) {
        return classes.stream().anyMatch( cls -> cls.isInstance(o) );
    }


    public String toString() {
        return "{" + classes + "}";
    }

}
