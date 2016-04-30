package bp;

import bp.exceptions.BPJRequestableSetException;

import java.util.Iterator;
import java.util.Set;

import static bp.BProgramControls.debugMode;

/**
 * Default _arbiter - triggers events according to the RWB semantics but promises
 * nothing as to the order of events triggered.
 *
 * @author moshewe
 */
public class Arbiter {

    protected BPApplication _app;

    public BPApplication getProgram() {
        return _app;
    }

    public void setProgram(BPApplication program) {
        this._app = program;
    }

    protected void bplog(String s) {
        if (debugMode)
            System.out.println("[" + getProgram() + ":" + this + "]: " + s);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    /**
     * Choose the next event to be fired, from the events offered by
     * {@link #_app}'s {@link BThread}s.
     *
     * @return an event to be triggered, or {@code null} if no event can be selected.
     * @throws BPJRequestableSetException
     */
    public BEvent nextEvent() {
        BEvent ec = selectEventFromProgram();
        bplog("Event chosen from program is " + ec);
        return ec;

    }

    protected BEvent selectEventFromProgram() {
        Set<BEvent> legals = _app.requestedAndNotBlockedEvents();
        Iterator<BEvent> it = legals.iterator();
        return it.hasNext() ? it.next() : null;
    }
}