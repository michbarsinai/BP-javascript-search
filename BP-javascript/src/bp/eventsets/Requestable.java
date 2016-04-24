package bp.eventsets;

import bp.BEvent;
import bp.exceptions.BPJRequestableSetException;

import java.util.Iterator;
import java.util.List;

/**
 * An interface for what can be requested in bSync call. 
 * 
 */
public interface Requestable extends Iterable<Requestable>, EventSet {

    @Override
	public Iterator<Requestable> iterator();

	public Requestable get(int index);

	public boolean isEvent();

	public int size();

    @Override
	public boolean contains(Object o);

	public BEvent getEvent() throws BPJRequestableSetException;
	
	public List<BEvent> getEventList();
		
	public void addEventsTo(List<BEvent> destinationList);
}
