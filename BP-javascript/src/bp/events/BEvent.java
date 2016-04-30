package bp.events;

import bp.exceptions.BPJRequestableSetException;

import java.util.ArrayList;
import java.util.Iterator;

import static bp.BProgramControls.debugMode;
import bp.eventsets.Requestable;
import bp.eventsets.EventSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A base class for events. Each event has a name, which serves as a 
 * way of testing for equality. Events that are initialized without a name are 
 * given unique name. 
 */
@SuppressWarnings("serial")
public class BEvent implements EventSet, Requestable, Comparable<BEvent> {

    private static final AtomicInteger INSTANCE_ID_GEN = new AtomicInteger(0);
    
    private final String name;
    
    public BEvent(String name) {
        this.name = name;
    }

    public BEvent() {
        this( BEvent.class.getSimpleName() + "#" + INSTANCE_ID_GEN.incrementAndGet() );
    }
    
    @Override
    public boolean contains(Object o) {
        return equals(o);
    }

    @Override
    public Iterator<Requestable> iterator() {
        return new SingleEventIterator(this);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    @Override
    public BEvent get(int index) {
        if (index == 0) return this;
        throw (new ArrayIndexOutOfBoundsException());
    }

    public boolean add(Requestable r) throws BPJRequestableSetException {
        throw new BPJRequestableSetException();

    }

    @Override
    public boolean isEvent() {
        return true;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public BEvent getEvent() throws BPJRequestableSetException {
        return this;
    }

    @Override
    public ArrayList<BEvent> getEventList() {
        ArrayList<BEvent> list = new ArrayList<>();
        addEventsTo(list);
        return list;
    }

    @Override
    public void addEventsTo(List<BEvent> list) {
        list.add(this);
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == this ) return true;
        if ( obj == null ) return false;
        if ( ! (obj instanceof BEvent) ) return false;
        
        BEvent other = (BEvent) obj;
        return name.equals(other.getName());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public int compareTo(BEvent e) {
        return name.compareTo(e.getName());
    }

    protected void bplog(String string) {
        if (debugMode)
            System.out.println(this + ": " + string);
    }

}

/**
 * An iterator over a single event object. Allows to view an event as a
 * (singleton) set.
 */
class SingleEventIterator implements Iterator<Requestable> {
    BEvent e;

    public SingleEventIterator(BEvent e) {
        this.e = e;
    }

    @Override
    public boolean hasNext() {
        return e != null;
    }

    @Override
    public BEvent next() {
        BEvent tmp = e;
        e = null;
        return tmp;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
