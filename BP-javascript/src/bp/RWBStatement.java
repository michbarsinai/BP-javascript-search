package bp;

import static bp.eventSets.EventSetConstants.none;
import bp.eventSets.EventSetInterface;
import bp.eventSets.RequestableInterface;
import java.util.Objects;

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
    private final RequestableInterface request;
    
    /**
     * The events waited for (wake thread up when these happen).
     */
    private final EventSetInterface waitFor;
    
    /**
     * The events blocked while this statement is active.
     */
    private final EventSetInterface block;
    
    /**
     * If any of these events happen, the stating thread wants to be terminated.
     */
    private final EventSetInterface except;    
    
    /**
     * Creates a new request where all fields are set to {@link none}. To be
     * used as a DSL like manner:
     * <code>
     * RWBStatement myStatement = make().request( XX ).waitFor( YY ).block( ZZZ );
     * </code>
     * @return an empty statement
     */
    public static RWBStatement make() {
        return new RWBStatement(none, none, none, none);
    }
    
    public RWBStatement(RequestableInterface request, EventSetInterface waitFor, EventSetInterface block, EventSetInterface except) {
        this.request = request;
        this.waitFor = waitFor;
        this.block = block;
        this.except = except;
    }

    public RWBStatement(RequestableInterface request, EventSetInterface waitFor, EventSetInterface block) {
        this( request, waitFor, block, none);
    }
    
    /**
     * Creates a new {@link RWBStatement} based on {@code this}, with the 
     * request updated to the {@code toRequest} parameter.
     * @param toRequest the request part of the new statement
     * @return a new statement
     */
    public RWBStatement request( RequestableInterface toRequest ) {
        return new RWBStatement(request, getWaitFor(), getBlock(), getExcept());
    }
    
    /**
     * @see #request
     */
    public RWBStatement waitFor( EventSetInterface events ) {
        return new RWBStatement(getRequest(), events, getBlock(), getExcept());
    }
    
    /**
     * @see #request
     */
    public RWBStatement block( EventSetInterface events ) {
        return new RWBStatement(getRequest(), getWaitFor(), block, getExcept());
    }
    
    /**
     * @see #request
     */
    public RWBStatement except( EventSetInterface events ) {
        return new RWBStatement(getRequest(), getWaitFor(), getBlock(), except);
    }
    
    public RequestableInterface getRequest() {
        return request;
    }

    public EventSetInterface getWaitFor() {
        return waitFor;
    }

    public EventSetInterface getBlock() {
        return block;
    }

    public EventSetInterface getExcept() {
        return except;
    }
    
    @Override
    public String toString() {
        return String.format("[RWBStatement r:%s w:%s b:%s e:%s]", getRequest(), getWaitFor(), getBlock(), getExcept());
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
        if (!Objects.equals(this.getExcept(), other.getExcept())) {
            return false;
        }
        return true;
    }
    
    
   
}
