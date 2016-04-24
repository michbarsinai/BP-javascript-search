package bp.events;

import bp.BEvent;
import java.util.Objects;

/**
 * 
 *
 * @author moshe
 * @author Michael
 * @param <T> The type of object being wrapped.
 */
public abstract class WrapperEvent<T> extends BEvent {

    protected T wrapped;
    
    public WrapperEvent(T obj) {
        wrapped = obj;
    }

    public T getWrappedObject() {
        return wrapped;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( o==this ) return true;
        if ( o == null ) return false;
        if ( o instanceof WrapperEvent ) {
            WrapperEvent other = (WrapperEvent) o;
            return Objects.equals(getWrappedObject(), other.getWrappedObject());
            
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return getWrappedObject()!=null ? getWrappedObject().hashCode() : 0;
    }
    
}
