package bp;

import bp.exceptions.BPJRequestableSetException;

import java.util.ArrayList;
import java.util.Iterator;

import static bp.BProgramControls.debugMode;
import bp.eventsets.Requestable;
import bp.eventsets.EventSet;
import java.util.List;
import java.util.Objects;

/**
 * A base class for events
 */
@SuppressWarnings("serial")
public class BEvent implements EventSet,
        Requestable, Comparable<BEvent> {

    protected String _name;
    // TODO: Remove the "output Event" field. This should be done by a filter on the selected event, e.g. in a listener on the BProgram.
    protected boolean _outputEvent = false;

    public BEvent(String name, boolean outputEvent) {
        _name = name;
        _outputEvent = outputEvent;
    }

    public BEvent() {
        this(null);
    }

    public BEvent(String aName) {
        this(aName, false);
    }
    
    /**
     * Object initializer for getting a default event name, 
     * if needed.
     */
    {
        if ( _name==null ) {
            _name = getClass().getSimpleName();
        }
    }
    
    public boolean isOutputEvent() {
        return _outputEvent;
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
        return _name;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
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
        return _name.equals(other.getName());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this._name);
        return hash;
    }

    @Override
    public int compareTo(BEvent e) {
        return _name.compareTo(e.getName());
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
