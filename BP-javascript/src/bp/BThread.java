package bp;

import bp.eventsets.EventSets;
import org.mozilla.javascript.*;

import javax.naming.OperationNotSupportedException;
import java.io.InputStream;
import java.io.Serializable;

import static bp.BProgramControls.debugMode;
import static bp.eventsets.EventSets.none;
import bp.eventsets.Requestable;
import bp.eventsets.EventSet;

/**
 * A Java BThread that wraps a Javascript function containing BP code.
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

    public BThread(String name, Function func) {
        this.name = name;
        entryPoint = func;
    }

    public BThread() {
        this(BThread.class.getName(), null);
    }

    private void openGlobalContext() {
        globalContext = ContextFactory.getGlobal().enterContext();
        globalContext.setOptimizationLevel(-1); // must use interpreter mode
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "[BThread: " + name + "]";
    }

    public void bplog(String string) {
        if (debugMode)
            System.out.println(this + ": " + string);
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
        Scriptable btThisScope = (Scriptable) Context.javaToJS(this,
                programScope);
        btThisScope.setPrototype(programScope);
        script = BThread.class.getResourceAsStream("highlevelidioms/breakupon.js");
        tScope = generateSubScope(btThisScope, script, "breakupon");
        script = BThread.class.getResourceAsStream("highlevelidioms/whileblocking.js");
        tScope = generateSubScope(tScope, script, "whileblocking");
        return tScope;
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
            bplog("started!");
            globalContext.callFunctionWithContinuations(entryPoint, scope,
                    new Object[0]);
        } catch (ContinuationPending pending) {
            continuation = pending;
        } finally {
            closeGlobalContext();
        }
    }

    private void closeGlobalContext() {
        Context.exit();
    }
    
    // TODO return Optional
    public ContinuationPending resume(BEvent event) {
        try {
            openGlobalContext();
            Object eventInJS = Context.javaToJS(event, scope);
            resumeContinuation(eventInJS);
        
        } catch (ContinuationPending pending) {
            continuation = pending;
            return continuation;
        
        } finally {
            closeGlobalContext();
        }
        
        bplog("Done");
        enterZombieMode();
        return null;
    }

    private void resumeContinuation(Object eventInJS) {
        globalContext.resumeContinuation(continuation.getContinuation(), scope, eventInJS);
    }

    // TODO what is this?
    public BEvent bsync(Object obj1, EventSet waitedEvents,
                        EventSet blockedEvents) throws OperationNotSupportedException {
        String explanation = "requestedEvents not of type " +
                "RequestableInterface not supported.";
        throw new OperationNotSupportedException(explanation);
    }

    public BEvent bsync(Requestable requestedEvents,
                        EventSet waitedEvents,
                        EventSet blockedEvents) {
        currentRwbStatement = RWBStatement.make().request(requestedEvents)
                                                .waitFor(waitedEvents)
                                                .block(blockedEvents);
        bplog("bsyncing with " + requestedEvents + ", " +
                waitedEvents + ", " + blockedEvents);
        try {
            openGlobalContext();
            ContinuationPending pending =
                    globalContext.captureContinuation();
            throw pending;
        } finally {
            closeGlobalContext();
        }
    }

    public void enterZombieMode() {
        currentRwbStatement = RWBStatement.make();
        continuation = null;
        alive = false;
    }

    public void revive() {
        alive = true;
    }

    public RWBStatement getCurrentRwbStatement() {
        return currentRwbStatement;
    }
    
}

