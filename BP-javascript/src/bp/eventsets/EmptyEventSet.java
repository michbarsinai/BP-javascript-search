package bp.eventsets;

import bp.BEvent;
import bp.exceptions.BPJRequestableSetException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * An event set that does not contain any event.
 */
public class EmptyEventSet implements EventSet, Requestable, Serializable {
	
    @Override
	public boolean contains(Object o) {
		return false;
	}

    @Override
	public Iterator<Requestable> iterator() {
		return new EmptyEventIterator();
	}

    @Override
	public String toString() {
		return "{empty}";
	}

    @Override
	public Requestable get(int index) {
		throw new ArrayIndexOutOfBoundsException();
	}

    @Override
	public int size() {
		return 0;
	}

    @Override
	public BEvent getEvent() throws BPJRequestableSetException {
        // TODO this seems like a design error - always throw an exception.
		throw new BPJRequestableSetException();
	}

    @Override
	public List<BEvent> getEventList() {
		return Collections.<BEvent>emptyList();
	}
    
    @Override
	public boolean isEvent() {
		return false;
	}

    @Override
    public void addEventsTo( List<BEvent> eventList ) {}
}

/**
 * An iterator over an empty set of events.
 */
class EmptyEventIterator implements Iterator<Requestable> {
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public BEvent next() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
