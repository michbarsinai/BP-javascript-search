package bp;

import bp.eventSets.EventSetConstants;
import bp.eventSets.EventSetInterface;
import bp.eventSets.RequestableInterface;
import org.mozilla.javascript.*;

import javax.naming.OperationNotSupportedException;
import java.io.InputStream;
import java.io.Serializable;

import static bp.BProgramControls.debugMode;
import static bp.eventSets.EventSetConstants.none;

/**
 * A Java BThread that wraps a Javascript function.
 * 
 * @author orelmosheweinstock
 * @author Michael
 */
public class BThread implements Serializable {

    /** The javascript function that will be called when {@code this} BThread runs. */
    private Function entryPoint;
    
    private String name;
    private Scriptable scope;
    protected ContinuationPending continuation;
    
    // TODO CONTPOINT: replace these with the RWBStatement object!
    protected RequestableInterface _request;
    protected EventSetInterface _wait;
    protected EventSetInterface _block;
    protected boolean alive = true;
    private Context globalContext;


    public BThread(String name, Function func) {
        _request = none;
        _wait = none;
        _block = none;
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


    public RequestableInterface getRequestedEvents() {
        return _request;
    }

    public void setRequestedEvents(RequestableInterface requestedEvents) {
        _request = requestedEvents;
    }

    public EventSetInterface getWaitedEvents() {
        return _wait;
    }

    public void setWaitedEvents(EventSetInterface watchedEvents) {
        _wait = watchedEvents;
    }

    public EventSetInterface getBlockedEvents() {
        return _block;
    }

    public void setBlockedEvents(EventSetInterface blockedEvents) {
        _block = blockedEvents;
    }

    public boolean isRequested(BEvent event) {
        return _request.contains(event);
    }

    public boolean isWaited(BEvent event) {
        return _wait.contains(event);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "[" + name + "]";
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
        alive = false;
        zombie();
        return null;
    }

    private void resumeContinuation(Object eventInJS) {
        globalContext.resumeContinuation(continuation.getContinuation(), scope,
                eventInJS);
    }

    public BEvent bsync(Object obj1, EventSetInterface waitedEvents,
                        EventSetInterface blockedEvents) throws OperationNotSupportedException {
        String explanation = "requestedEvents not of type " +
                "RequestableInterface not supported.";
        throw new OperationNotSupportedException(explanation);
    }

    public BEvent bsync(RequestableInterface requestedEvents,
                        EventSetInterface waitedEvents,
                        EventSetInterface blockedEvents) {
        _request = requestedEvents;
        _wait = waitedEvents;
        _block = blockedEvents;
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

    public void zombie() {
        _request = EventSetConstants.none;
        _wait = EventSetConstants.none;
        _block = EventSetConstants.none;
        continuation = null;
    }

    public void revive() {
        alive = true;
    }
    
}

