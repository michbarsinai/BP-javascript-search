package bp.events;


import static bp.BProgramControls.debugMode;
import bp.eventsets.EventSet;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A base class for events. Each event has a name, which serves as a 
 * way of testing for equality. Events that are initialized without a name are 
 * given unique name. 
 * 
 * Each event implicitly defines a singleton {@link EventSet}, which contains
 * only itself.
 */
@SuppressWarnings("serial")
public class BEvent implements Comparable<BEvent>, EventSet {

    private static final AtomicInteger INSTANCE_ID_GEN = new AtomicInteger(0);
    
    private final String name;
    
    public static BEvent named( String aName ) {
        return new BEvent(aName);
    }
    
    public BEvent(String name) {
        this.name = name;
    }

    public BEvent() {
        this( BEvent.class.getSimpleName() + "#" + INSTANCE_ID_GEN.incrementAndGet() );
    }
    
    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
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

    @Override
    public boolean contains(Object o) {
        return equals(o);
    }
}
