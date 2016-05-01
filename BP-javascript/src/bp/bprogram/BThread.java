package bp.bprogram;

import bp.events.BEvent;
import org.mozilla.javascript.*;

import java.io.InputStream;
import java.io.Serializable;

import static bp.BProgramControls.debugMode;
import bp.eventsets.EventSet;
import java.io.IOException;
import java.util.Set;

/**
 * A Javascript BThread wrapper. 
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
            System.out.println(this + ": " + string);
    }

    public void setupScope(Scriptable programScope) {
        scope = generateBThreadScope(programScope);
        if (entryPoint != null) {
            Scriptable funcScope = entryPoint.getParentScope();
            if (funcScope != programScope) {
                while (funcScope.getParentScope() != programScope) {
                    funcScope = funcScope.getParentScope();
                }
                funcScope.setParentScope(scope);
            } else {
                entryPoint.setParentScope(scope);
            }
        }
    }

    protected Scriptable generateBThreadScope(Scriptable programScope) {
        Scriptable tScope;
        InputStream script;
        Scriptable btThisScope = (Scriptable) Context.javaToJS(this, programScope);
        btThisScope.setPrototype(programScope);
        btThisScope.put("bt", btThisScope, (Scriptable) Context.javaToJS(this, programScope) );
        return btThisScope;
//        script = BThread.class.getResourceAsStream("highlevelidioms/breakupon.js");
//        tScope = generateSubScope(btThisScope, script, "breakupon");
//        try {
//            script.close();
//            script = BThread.class.getResourceAsStream("highlevelidioms/whileblocking.js");
//            tScope = generateSubScope(tScope, script, "whileblocking");
//            script.close();
//        } catch (IOException ex) {
//            throw new RuntimeException("Error closing input stream of internal JS files: " + ex.getMessage());
//        }
    }

    public Scriptable generateSubScope(Scriptable scope, InputStream ios,
                                       String scriptName) {
        Scriptable tScope = (Scriptable) BJavascriptProgram.evaluateInGlobalContext(
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
            enterZombieMode(); // If we got here, there was no bSync in the JS code.
            
        } catch (ContinuationPending pending) {
            continuation = pending;
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
        try {
            Object toResume = continuation.getContinuation();
            continuation = null;
            currentRwbStatement = null;
            openGlobalContext();
            Object eventInJS = Context.javaToJS(event, scope);
            globalContext.resumeContinuation(toResume, scope, eventInJS);
        
        } catch (ContinuationPending pending) {
            if ( currentRwbStatement==null ) {
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

    public void testCall( Object o ) {
        System.out.println("Test call:" + o);
    }
    
    public void bsync( RWBStatement aStatement ) {
        System.out.println("bsyncing with " + aStatement);
        currentRwbStatement = aStatement;
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
        System.err.println("sync coll" + requestedEvents);
        bsync( RWBStatement.make().request(requestedEvents)
                                  .waitFor(waitedEvents)
                                  .block(blockedEvents) );
    }
    
    public void bsync(BEvent aRequestedEvent,
                      EventSet waitedEvents,
                      EventSet blockedEvents) {
        bsync( RWBStatement.make().request(aRequestedEvent)
                                  .waitFor(waitedEvents)
                                  .block(waitedEvents));
    }
    
    private void closeGlobalContext() {
        Context.exit();
    }
    
    public void enterZombieMode() {
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

