package bp.bprogram;

import bp.events.BEvent;
import java.util.Objects;
import bp.eventsets.EventSet;
import bp.eventsets.ExplicitEventSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import static bp.eventsets.Events.emptySet;

/**
 * A statement a BTHread makes about what events it requests, waits for, and blocks.
 * The set of parameters for a [@code bSync} call.
 * 
 * @author michael
 */
public class RWBStatement {
   
    /**
     * The event requested by this statement
     */
    private final Set<BEvent> request;
    
    /**
     * The events waited for (wake thread up when these happen).
     */
    private final EventSet waitFor;
    
    /**
     * The events blocked while this statement is active.
     */
    private final EventSet block;
    
    /**
     * If any of these events happen, the stating thread wants to be terminated.
     */
    private final EventSet breakUpon;    
    
    private BThread bthread;
    
    /**
     * Creates a new request where all fields are set to {@link none}. To be
     * used as a DSL like manner:
     * <code>
     * RWBStatement myStatement = make().request( XX ).waitFor( YY ).block( ZZZ );
     * </code>
     * @param creator the {@link BThread} that created this statument.
     * @return an empty statement
     */
    public static RWBStatement make(BThread creator) {
        return new RWBStatement(Collections.emptySet(), emptySet, emptySet, emptySet).setBthread(creator);
    }
    public static RWBStatement make() {
        return new RWBStatement(Collections.emptySet(), emptySet, emptySet, emptySet);
    }
    
    
    
    public RWBStatement(Collection<? extends BEvent> request, EventSet waitFor, EventSet block, EventSet except) {
        this.request = new HashSet<>(request);
        this.waitFor = waitFor;
        this.block = block;
        this.breakUpon = except;
    }

    public RWBStatement(Collection<? extends BEvent> request, EventSet waitFor, EventSet block) {
        this(request, waitFor, block, emptySet);
    }
    
    public boolean shouldWakeFor( BEvent anEvent ) {
        return request.contains(anEvent) || waitFor.contains(anEvent);
    }
    
    /**
     * Creates a new {@link RWBStatement} based on {@code this}, with the 
     * request updated to the {@code toRequest} parameter.
     * @param toRequest the request part of the new statement
     * @return a new statement
     */
    public RWBStatement request( Collection<? extends BEvent> toRequest ) {
        return new RWBStatement(toRequest, getWaitFor(), getBlock(), getBreakUpon());
    }
    public RWBStatement request( BEvent requestedEvent ) {
        Set<BEvent> toRequest = new HashSet<>();
        toRequest.add(requestedEvent);
        return new RWBStatement(toRequest, getWaitFor(), getBlock(), getBreakUpon());
    }
    public RWBStatement request( ExplicitEventSet ees ) {
        return new RWBStatement(ees.getCollection(), getWaitFor(), getBlock(), getBreakUpon());
    }
    
    public RWBStatement waitFor( EventSet events ) {
        return new RWBStatement(getRequest(), events, getBlock(), getBreakUpon());
    }

    public RWBStatement block( EventSet events ) {
        return new RWBStatement(getRequest(), getWaitFor(), events, getBreakUpon());
    }
    
    public RWBStatement breakUpon( EventSet events ) {
        return new RWBStatement(getRequest(), getWaitFor(), getBlock(), events);
    }
    
    public Collection<BEvent> getRequest() {
        return request;
    }

    public EventSet getWaitFor() {
        return waitFor;
    }

    public EventSet getBlock() {
        return block;
    }

    public EventSet getBreakUpon() {
        return breakUpon;
    }

    public BThread getBthread() {
        return bthread;
    }

    public RWBStatement setBthread(BThread bthread) {
        this.bthread = bthread;
        return this;
    }
    
    @Override
    public String toString() {
        return String.format("[RWBStatement r:%s w:%s b:%s x:%s]", getRequest(), getWaitFor(), getBlock(), getBreakUpon());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.request);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (! (obj instanceof RWBStatement)) {
            return false;
        }
        final RWBStatement other = (RWBStatement) obj;
        if (!Objects.equals(this.getRequest(), other.getRequest())) {
            return false;
        }
        if (!Objects.equals(this.getWaitFor(), other.getWaitFor())) {
            return false;
        }
        if (!Objects.equals(this.getBlock(), other.getBlock())) {
            return false;
        }
        return Objects.equals(this.getBreakUpon(), other.getBreakUpon());
    }

}
