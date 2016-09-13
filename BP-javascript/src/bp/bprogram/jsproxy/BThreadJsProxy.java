/*
 *  Author: Michael Bar-Sinai
 */
package bp.bprogram.jsproxy;

import bp.bprogram.BThread;
import bp.bprogram.RWBStatement;
import bp.events.BEvent;
import bp.eventsets.ComposableEventSet;
import bp.eventsets.EventSet;
import bp.eventsets.Events;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

/**
 * The Javascript interface of a {@link BThread}.
 * @author michael
 */
public class BThreadJsProxy {
    
    private final BThread bthread;

    public BThreadJsProxy(BThread bthread) {
        this.bthread = bthread;
    }
    
    public void bsync( NativeObject jsRWB ) {
        Map<String, Object> jRWB = (Map)Context.jsToJava(jsRWB, Map.class);
        
        RWBStatement stmt = RWBStatement.make();
        Object req = jRWB.get("request");
        if ( req != null ) {
            if ( req instanceof BEvent ) {
                stmt = stmt.request((BEvent)req);
            } else if ( req instanceof NativeArray ) {
                NativeArray arr = (NativeArray) req;
                stmt = stmt.request(
                        Arrays.asList( arr.getIndexIds() ).stream()
                              .map( i -> (BEvent)arr.get(i) )
                              .collect( toSet() ));
            }
        }
        
        stmt = stmt.waitFor( convertToEventSet(jRWB.get("waitFor")) )
                   .block( convertToEventSet(jRWB.get("block")) )
                   .breakUpon(convertToEventSet(jRWB.get("breakUpon")) );
        
        bthread.bsync( stmt );
        
    }
    
    private EventSet convertToEventSet( Object jsObject ) {
        if ( jsObject == null ) return Events.emptySet;
        
        // This covers event sets AND events.
        if ( jsObject instanceof EventSet ) {
            return (EventSet)jsObject;
        
        } else if ( jsObject instanceof NativeArray ) {
            NativeArray arr = (NativeArray) jsObject;
            if ( Stream.of(arr.getIds()).anyMatch( id -> arr.get(id)==null) ) {
                throw new RuntimeException("EventSet Array contains null sets.");
            }
            return ComposableEventSet.anyOf(
              Arrays.asList(arr.getIndexIds()).stream()
                    .map( i ->(EventSet)arr.get(i) )
                    .collect( toSet() ) );
        } else {
            final String errorMessage = "Cannot convert " + jsObject + " of class " + jsObject.getClass() + " to an event set";
            Logger.getLogger(BThread.class.getName()).log(Level.SEVERE, errorMessage);
            throw new IllegalArgumentException( errorMessage);
        }
    }
    
        /**
     * BSync call, used by the JS programs. Works as follows:
     * <ol>
     * <li>Creates an {@link RWBStatement} using the parameters</li>
     * <li>Captures a continuation</li>
     * <li>Cleans up current Javascript context</li>
     * <li>Throws the continuation</li>
     * </ol>
     * @param requestedEvents 
     * @param waitedEvents
     * @param blockedEvents
     * @deprecated use the named arguments version
     */
    public void bsync(Set<? extends BEvent> requestedEvents,
                      EventSet waitedEvents,
                      EventSet blockedEvents) {
        System.err.println("warning: positional bsync (bsync(r,w,b)) is deprecated. Use named arguemnt bsync (bsync({requested:...})) instead.");
        bthread.bsync( RWBStatement.make().request(requestedEvents)
                                  .waitFor(waitedEvents)
                                  .block(blockedEvents) );
    }
    
     /**
      * @deprecated use the named arguments version
      */
    public void bsync(BEvent aRequestedEvent,
                      EventSet waitedEvents,
                      EventSet blockedEvents) {
        System.err.println("warning: positional bsync (bsync(r,w,b)) is deprecated. Use named arguemnt bsync (bsync({requested:...})) instead.");
        bthread.bsync( RWBStatement.make().request(aRequestedEvent)
                                  .waitFor(waitedEvents)
                                  .block(blockedEvents) );
    }
    
    
    public void setBreakUponHandler( Object aPossibleHandler ) {
        bthread.setBreakUponHandler(
                (aPossibleHandler instanceof Function) 
                    ? Optional.of((Function) aPossibleHandler)
                    : Optional.empty() );
    }
}
