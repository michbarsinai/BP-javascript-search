package bp.bprogram.runtimeengine;

import bp.events.BEvent;
import org.mozilla.javascript.*;

import java.io.InputStream;
import java.io.Serializable;

import bp.bprogram.runtimeengine.jsproxy.BThreadJsProxy;
import java.util.Optional;

/**
 * The state of a BThread at {@code bsync}.
 *
 * @author orelmosheweinstock
 * @author Michael
 */
public class BThreadSyncSnapshot implements Serializable {

    /**
     * The Javascript function that will be called when {@code this} BThread
     * runs.
     */
    private Function entryPoint;

    private String name;
    private Scriptable scope;
    private ContinuationPending continuation;

    private BSyncStatement bSyncStatement;
    private boolean alive = true;
    private Context globalContext; // TODO needed?
    private final BThreadJsProxy proxy = new BThreadJsProxy(this);
    
    /**
     * BThreads may specify a function that runs when they are removed because
     * of a {@code breakUpon} statement.
     */
    private Optional<Function> breakUponHandler = Optional.empty();

    public BThreadSyncSnapshot(String aName, Function anEntryPoint) {
        name = aName;
        entryPoint = anEntryPoint;
    }

    public BThreadSyncSnapshot() {
        this(BThreadSyncSnapshot.class.getName(), null);
    }
    
    public BThreadSyncSnapshot copyWith( ContinuationPending aContinuation, BSyncStatement aStatement ) {
        BThreadSyncSnapshot retVal = new BThreadSyncSnapshot(name, entryPoint);
        retVal.globalContext = globalContext; // TODO needed?
        retVal.continuation = aContinuation;
        retVal.setBreakUponHandler(getBreakUponHandler());
        retVal.setupScope(scope.getParentScope());

        retVal.bSyncStatement = aStatement;
        aStatement.setBthread(retVal);
        
        return retVal;
    }
    
    public void setupScope(Scriptable programScope) {
        scope = (Scriptable) Context.javaToJS(proxy, programScope);
        scope.setPrototype(programScope);

        // This is a break from JS's semantics, but we have to do it.
        // In JS, inner functions know about variables in their syntactical parents.
        // For BThread functions we break this, and make them a top-level scope. This
        // works for us since the only communication between BThreads is via events,
        // so in particular they can't share variables.
        entryPoint.setParentScope(scope);
    }

    public void bsync( BSyncStatement aStatement ) {
        if ( !isAlive() ) { // TODO: this might be redaundant now that we delete bsync from the scope of breakupon handlers.
            throw new IllegalStateException("Removed BThread cannot call bsync. Consider using enqueueExternalEvent.");
        }
        bSyncStatement = aStatement.setBthread(this);
        openGlobalContext(); // TODO try without the global context field, openingna dn clsing it. Might be already delt with at the BPTask level.
        ContinuationPending capturedContinuation = globalContext.captureContinuation();
        closeGlobalContext();
        capturedContinuation.setApplicationState(aStatement);
        throw capturedContinuation;
    }
    
    private void openGlobalContext() {
        globalContext = ContextFactory.getGlobal().enterContext();
        globalContext.setOptimizationLevel(-1); // must use interpreter mode
    }
    
    private void closeGlobalContext() {
        Context.exit();
    }

    public void enterZombieMode() {
        bSyncStatement = null;
        continuation = null;
        alive = false;
    }

    public BSyncStatement getBSyncStatement() {
        return bSyncStatement;
    }

    public void setBSyncStatement(BSyncStatement stmt) {
        bSyncStatement = stmt;
        if ( bSyncStatement.getBthread() != this ) {
            bSyncStatement.setBthread(this);
        }
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

    public void setAlive(boolean b) {
        this.alive = b;
    }

    public Optional<Function> getBreakUponHandler() {
        return breakUponHandler;
    }

    public void setBreakUponHandler(Function aBreakUponHandler) {
        setBreakUponHandler(Optional.ofNullable(aBreakUponHandler));
    }
    
    public void setBreakUponHandler(Optional<Function> aBreakUponHandler) {
        breakUponHandler = aBreakUponHandler;
    }

    public Scriptable getScope() {
        return scope;
    }

    public Function getEntryPoint() {
        return entryPoint;
    }
    
}
