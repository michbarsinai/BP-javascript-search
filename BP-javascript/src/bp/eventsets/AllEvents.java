package bp.eventsets;

import java.io.Serializable;

/**
 * @author Bertrand Russell
 * 
 *         A set that contains everything.
 */
public class AllEvents implements EventSet, Serializable {
    
    
	
    @Override
	public boolean contains(Object o) {
		return (o instanceof EventSet);
	}

	@Override
	public String toString() {
		return ("{AllEvents}");
	}

}
