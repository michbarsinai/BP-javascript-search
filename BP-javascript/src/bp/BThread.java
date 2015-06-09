package bp;

import bp.eventSets.EventSetConstants;
import bp.eventSets.EventSetInterface;
import bp.eventSets.RequestableInterface;
import org.mozilla.javascript.*;

import java.io.InputStream;
import java.io.Serializable;

import static bp.BProgramControls.debugMode;
import static bp.eventSets.EventSetConstants.none;

/**
 * Created by orelmosheweinstock on 3/24/15.
 */
public class BThread implements Serializable {

    public Function _func = null;
    protected String _name = this.getClass().getSimpleName();
    transient protected BProgram bp = null;
    public Scriptable _scope;
    protected ContinuationPending _cont;
    protected RequestableInterface _request;
    protected EventSetInterface _wait;
    protected EventSetInterface _block;
    protected boolean _alive = true;


    public BThread() {
        _request = none;
        _wait = none;
        _block = none;
    }

    public BThread(String name, Function func) {
        this();
        _name = name;
        _func = func;
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
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public String toString() {
        return "[" + _name + "]";
    }

    public void bplog(String string) {
        if (debugMode)
            System.out.println(this + ": " + string);
    }

    public boolean isAlive() {
        return _alive;
    }

    public ContinuationPending getCont() {
        return _cont;
    }

    public void setCont(ContinuationPending cont) {
        _cont = cont;
    }

    public void setupScope(Scriptable programScope) {
        generateBThreadScope(programScope);
        if (_func != null) {
            Scriptable funcScope = _func.getParentScope();
            if (funcScope != programScope) {
                while (funcScope.getParentScope() != programScope) {
                    funcScope = funcScope.getParentScope();
                }
                funcScope.setParentScope(_scope);
            } else {
                _func.setParentScope(_scope);
            }
        }
    }

    protected void generateBThreadScope(Scriptable programScope) {
        Context cx = ContextFactory.getGlobal().enterContext();
        Scriptable btThisScope = (Scriptable) Context.javaToJS(this, programScope);
        InputStream script;
        btThisScope.setPrototype(programScope);
        _scope = btThisScope;
        script = BThread.class.getResourceAsStream("highlevelidioms/breakupon.js");
        Scriptable tScope;
        tScope = (Scriptable) evaluateInBThreadScope(script,
                "breakupon");
        tScope.setPrototype(_scope);
        _scope = tScope;
        script = BThread.class.getResourceAsStream("highlevelidioms/whileblocking.js");
        tScope = (Scriptable) evaluateInBThreadScope(script,
                "whileblocking");
        tScope.setPrototype(_scope);
        _scope = tScope;
    }

    public Object evaluateInBThreadScope(InputStream script,
                                         String scriptname) {
        return BPJavascriptApplication.evaluateInGlobalContext(
                _scope, script, scriptname);
    }

    public Object evaluateInBThreadScope(String path) {
        return BPJavascriptApplication.evaluateInGlobalContext(
                _scope, path);
    }

    public void start() {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        try {
            bplog("started!");
//            cx.executeScriptWithContinuations(_script, _scope);
            cx.callFunctionWithContinuations(_func, _scope,
                    new Object[0]);
        } catch (ContinuationPending pending) {
            _cont = pending;
        } finally {
            Context.exit();
        }
    }

    public ContinuationPending resume(BEvent event) {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        try {
            Object eventInJS = Context.javaToJS(event, _scope);
            cx.resumeContinuation(_cont.getContinuation(), _scope,
                    eventInJS);
        } catch (ContinuationPending pending) {
            _cont = pending;
            return _cont;
        } finally {
            Context.exit();
        }

        bplog(" I'm over!");
        _alive = false;
        zombie();
        return null;
    }

    public BEvent bsync(RequestableInterface requestedEvents,
                        EventSetInterface waitedEvents,
                        EventSetInterface blockedEvents) {
        _request = requestedEvents;
        _wait = waitedEvents;
        _block = blockedEvents;
        bplog("bsyncing with " + requestedEvents + ", " +
                waitedEvents + ", " + blockedEvents);
        Context cx = ContextFactory.getGlobal().enterContext();
        try {
            ContinuationPending pending = cx.captureContinuation();
            throw pending;
        } finally {
            Context.exit();
        }
    }

    public void zombie() {
        _request = EventSetConstants.none;
        _wait = EventSetConstants.none;
        _block = EventSetConstants.none;
        _cont = null;
    }

    public void revive() {
        _alive = true;
    }
}

