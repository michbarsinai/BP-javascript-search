package bp.events;


import static bp.BProgramControls.debugMode;
import bp.eventsets.EventSet;
import java.util.HashMap;
import java.util.Map;
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
    
    /**
     * Name of the event. Public access, so that the Javascript code feels natural.
     */
    public final String name;
    
    /**
     * Extra data for the event. Public access, so that the Javascript code feels natural.
     */
    public final Map<String, Object> data;
    
    public static BEvent named( String aName ) {
        return new BEvent(aName);
    }
    
    public BEvent(String aName) {
        this( aName, new HashMap<>() );
    }
    
    public BEvent( String aName, Map<String,Object> someData ) {
        name = aName;
        data = (someData!=null)?someData : new HashMap<>();
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

    public Map<String, Object> getData() {
        return data;
    }
    
    
    
    @Override
    public boolean equals(Object obj) {
        if ( obj == this ) return true;
        if ( obj == null ) return false;
        if ( ! (obj instanceof BEvent) ) return false;
        
        BEvent other = (BEvent) obj;
        return name.equals(other.getName()) && data.equals( other.getData() );
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
