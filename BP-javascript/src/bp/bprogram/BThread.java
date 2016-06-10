package bp.bprogram;

import bp.events.BEvent;
import org.mozilla.javascript.*;

import java.io.InputStream;
import java.io.Serializable;

import static bp.BProgramControls.debugMode;
import bp.eventsets.ComposableEventSet;
import bp.eventsets.EventSet;
import bp.eventsets.Events;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toSet;

/**
 * A Javascript BThread (NOT a Java thread!). 
 * 
 * @author orelmosheweinstock
 * @author Michael
 */
public class BThread implements Serializable {

    /** The Javascript function that will be called when {@code this} BThread runs. */
    private Function entryPoint;
    
    private String name;
    private Scriptable scope;
    private ContinuationPending continuation;
    
    private RWBStatement currentRwbStatement;
    private boolean alive = true;
    private Context globalContext;

    public BThread(String aName, Function anEntryPoint) {
        name = aName;
        entryPoint = anEntryPoint;
    }

    public BThread() {
        this(BThread.class.getName(), null);
    }

    private void openGlobalContext() {
        globalContext = ContextFactory.getGlobal().enterContext();
        globalContext.setOptimizationLevel(-1); // must use interpreter mode
    }

    public void bplog(String string) {
        if (debugMode)
            System.out.println(getName() + ": " + string);
    }

    public void setupScope(Scriptable programScope) {
        scope = (Scriptable) Context.javaToJS(this, programScope);
        scope.setPrototype(programScope);
        scope.put("bt", scope, (Scriptable) Context.javaToJS(this, programScope) );
        
        // This is a break from JS's semantics, but we have to do it.
        // In JS, inner functions know about variables in their syntactical parents.
        // For BThread functions we break this, and make them a top-level scope. This
        // works for us since the only communication between BThreads is via events,
        // so in particular they can't share variables.
        entryPoint.setParentScope(scope);
    }

    public Scriptable generateSubScope(Scriptable scope, InputStream ios,
                                       String scriptName) {
        Scriptable tScope = (Scriptable) BProgram.evaluateInGlobalContext(
                scope,
                ios,
                scriptName);
        tScope.setPrototype(scope);
        return tScope;
    }

    public void start() {
        try {
            openGlobalContext();
            continuation = null;
            globalContext.callFunctionWithContinuations(entryPoint, scope, new Object[0]);
            bplog("Done - no bsyncs!");
            enterZombieMode(); // If we got here, there was no bSync in the JS code.
            
        } catch (ContinuationPending pending) {
            continuation = pending;
            if ( currentRwbStatement==null ) {
                System.err.println("Bthread " + getName() + " got a continuation but no RWBStatement.");
                throw new IllegalStateException("BSync called but no statement registered");
            }
            
        } finally {
            closeGlobalContext();
        }
    }
    
    /**
     * Resumes the Javascript program, returning the passed object
     * as the return value of the {@code bSync} call that created the continuation.
     * @param event The selected event.
     * @return A pending continuation, or {@code null}.
     */
    // TODO return Optional
    public ContinuationPending resume(BEvent event) {
        bplog("resuming");
        try {
            Object toResume = continuation.getContinuation();
            continuation = null;
            currentRwbStatement = null;
            openGlobalContext();
            Object eventInJS = Context.javaToJS(event, scope);
            globalContext.resumeContinuation(toResume, scope, eventInJS);
        
        } catch (ContinuationPending pending) {
            if ( currentRwbStatement==null ) {
                System.err.println("Bthread " + getName() + " got a continuation but no RWBStatement.");
                throw new IllegalStateException("BSync called but no statement registered");
            }
            continuation = pending;
            return continuation;
        
        } finally {
            closeGlobalContext();
        }
        
        bplog("Done");
        enterZombieMode();
        return null;
    }

    public void bsync( RWBStatement aStatement ) {
        currentRwbStatement = aStatement;
        bplog("bsyncing with " + currentRwbStatement);
        openGlobalContext();
        ContinuationPending capturedContinuation = globalContext.captureContinuation();
        closeGlobalContext();
        throw capturedContinuation;
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
     */
    public void bsync(Set<? extends BEvent> requestedEvents,
                      EventSet waitedEvents,
                      EventSet blockedEvents) {
        bsync( RWBStatement.make(this).request(requestedEvents)
                                  .waitFor(waitedEvents)
                                  .block(blockedEvents) );
    }
    
    public void bsync(BEvent aRequestedEvent,
                      EventSet waitedEvents,
                      EventSet blockedEvents) {
        bsync( RWBStatement.make(this).request(aRequestedEvent)
                                  .waitFor(waitedEvents)
                                  .block(blockedEvents));
    }
    
     public void bsync( NativeObject jsRWB ) {
        Map<String, Object> jRWB = (Map)Context.jsToJava(jsRWB, Map.class);
        
        RWBStatement stmt = RWBStatement.make(this);
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
        stmt.setBthread(this);
        
        bsync( stmt );
        
    }
    
    private EventSet convertToEventSet( Object jsObject ) {
        if ( jsObject == null ) return Events.emptySet;
        
        // This covers event sets AND events.
        if ( jsObject instanceof EventSet ) {
            return (EventSet)jsObject;
        
        } else if ( jsObject instanceof NativeArray ) {
            NativeArray arr = (NativeArray) jsObject;
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
    
    private void closeGlobalContext() {
        Context.exit();
    }
    
    public void enterZombieMode() {
        bplog("Entering Zombie mode");
        currentRwbStatement = null;
        continuation = null;
        alive = false;
    }

    public RWBStatement getCurrentRwbStatement() {
        return currentRwbStatement;
    }
    
    public boolean isAlive() {
        return alive;
    }

    public ContinuationPending getContinuation() {
        return continuation;
    }

    public void setContinuation(ContinuationPending cont) {
        continuation = cont;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "[BThread: " + name + "]";
    }

}

