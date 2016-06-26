package bp.events;


import static bp.BProgramControls.debugMode;
import bp.eventsets.EventSet;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 * A base class for events. Each event has a name and optional data, which is a 
 * Javascript object.
 * 
 * For two events to be equal, they names and data have to match.
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
    public final Optional<Object> maybeData;
    
    public static BEvent named( String aName ) {
        return new BEvent(aName);
    }
    
    public BEvent(String aName) {
        this( aName, null );
    }
    
    public BEvent( String aName, Object someData ) {
        name = aName;
        maybeData = Optional.ofNullable(someData);
    }
    
    public BEvent() {
        this( BEvent.class.getSimpleName() + "#" + INSTANCE_ID_GEN.incrementAndGet() );
    }
    
    @Override
    public String toString() {
        return "[BEvent name:" + name + maybeData.map(d -> " data:" + d).orElse("") + "]";
    }

    public String getName() {
        return name;
    }

    public Object getData() {
        return maybeData.orElse(null);
    }

    public Optional<Object> getMaybeData() {
        return maybeData;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        // Circuit breakers
        if ( obj == this ) return true;
        if ( obj == null ) return false;
        if ( ! (obj instanceof BEvent) ) return false;
        
        BEvent other = (BEvent) obj;
        // simple cases
        if ( ! name.equals(other.name)  ) return false;
        if ( maybeData.isPresent() != other.getMaybeData().isPresent() ) return false;
        
        // OK, might need to delve into Javascript semantics.
        if ( maybeData.isPresent() ) {
            Object ourData = getData();
            Object theirData = other.getData();
            if ( ! (ourData.getClass().isAssignableFrom(theirData.getClass())
                     || theirData.getClass().isAssignableFrom(ourData.getClass())) ) {
                return false; // not same type of data.
            }
            
            // Evaluate datas.
            return jsObjectsEqual(ourData, theirData);
            
        } else {
            // whew
            return true;
        }
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
    
    private boolean jsObjectsEqual( Object o1, Object o2 ) {
        if ( o1==o2 ) return true;
        if ( o1==null ^ o2==null ) return false;
        if ( ! o1.getClass().equals(o2.getClass()) ) return false;
        
        // established: o1 and o2 are non-null and of the same class.
        if ( o1 instanceof ScriptableObject ) {
            return jsScriptableObjectEqual( (ScriptableObject)o1, (ScriptableObject)o2 );
        } else {
            // use direct JS evaluation
            Context cx = Context.enter();
            try { 
                
                Scriptable scope = cx.initStandardObjects();
                scope.put("o1", scope, Context.javaToJS(o1, scope));
                scope.put("o2", scope, Context.javaToJS(o2, scope));
                Object res = cx.evaluateString(scope, "o1===o2", "<comparison code>",1,null);
                
                return (Boolean)res;                
                
            } finally {
                Context.exit();
            }

        }
        
        
    }

    private boolean jsScriptableObjectEqual(ScriptableObject o1, ScriptableObject o2) {
        Object[] o1Ids = o1.getIds();
        Object[] o2Ids = o2.getIds();
        if ( o1Ids.length != o2Ids.length ) return false;
        return Stream.of(o1Ids).allMatch( id -> jsObjectsEqual(o1.get(id), o2.get(id)) )
                && Stream.of(o2Ids).allMatch( id -> jsObjectsEqual(o1.get(id), o2.get(id)) );
    }
}