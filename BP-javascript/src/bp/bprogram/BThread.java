package bp.bprogram;

import bp.events.BEvent;
import org.mozilla.javascript.*;

import java.io.InputStream;
import java.io.Serializable;

import bp.bprogram.jsproxy.BThreadJsProxy;
import java.util.Optional;

/**
 * A Javascript BThread (NOT a Java thread!).
 *
 * @author orelmosheweinstock
 * @author Michael
 */
public class BThread implements Serializable {

    /**
     * The Javascript function that will be called when {@code this} BThread
     * runs.
     */
    private Function entryPoint;

    private String name;
    private Scriptable scope;
    private ContinuationPending continuation;

    private RWBStatement currentRwbStatement;
    private boolean alive = true;
    private Context globalContext;
    private final BThreadJsProxy proxy = new BThreadJsProxy(this);
    
    /**
     * BThreads may specify a function that runs when they are removed because
     * of a {@code breakUpon} statement.
     */
    private Optional<Function> breakUponHandler = Optional.empty();

    public BThread(String aName, Function anEntryPoint) {
        name = aName;
        entryPoint = anEntryPoint;
    }

    public BThread() {
        this(BThread.class.getName(), null);
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
            enterZombieMode(); // If we got here, there was no bSync in the JS code.

        } catch (ContinuationPending pending) {
            continuation = pending;
            if (currentRwbStatement == null) {
                System.err.println("Bthread " + getName() + " got a continuation but no RWBStatement.");
                throw new IllegalStateException("BSync called but no statement registered");
            }

        } finally {
            closeGlobalContext();
        }
    }

    /**
     * Resumes the Javascript program, returning the passed object as the return
     * value of the {@code bSync} call that created the continuation.
     *
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
            if (currentRwbStatement == null) {
                System.err.println("Bthread " + getName() + " got a continuation but no RWBStatement.");
                throw new IllegalStateException("BSync called but no statement registered");
            }
            continuation = pending;
            return continuation;

        } finally {
            closeGlobalContext();
        }

        enterZombieMode();
        return null;
    }

    public void bsync( RWBStatement aStatement ) {
        if ( !isAlive() ) {
            throw new IllegalStateException("Removed BThread cannot call bsync. Consider using enqueueExternalEvent.");
        }
        currentRwbStatement = aStatement.setBthread(this);
        openGlobalContext();
        ContinuationPending capturedContinuation = globalContext.captureContinuation();
        closeGlobalContext();
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
        currentRwbStatement = null;
        continuation = null;
        alive = false;
    }

    public RWBStatement getCurrentRwbStatement() {
        return currentRwbStatement;
    }

    public void setCurrentRwbStatement(RWBStatement stmt) {
        currentRwbStatement = stmt;
        if ( currentRwbStatement.getBthread() != this ) {
            currentRwbStatement.setBthread(this);
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
    
}
