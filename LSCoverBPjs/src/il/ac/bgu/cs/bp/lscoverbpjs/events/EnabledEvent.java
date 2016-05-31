/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs.events;

import java.util.Objects;

/**
 * A sort of meta-event, signifying that the {@link #subject} event is 
 * now enabled.
 * 
 * @author michael
 */
public class EnabledEvent extends LscEvent {
    
    private final LscEvent subject;
    
    public EnabledEvent(LscEvent aSubject) {
        super(aSubject.getChartId());
        subject = aSubject;
    }

    public LscEvent getSubject() {
        return subject;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.subject);
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
        if ( ! (obj instanceof EnabledEvent) ) {
            return false;
        }

        final EnabledEvent other = (EnabledEvent) obj;
        if ( Objects.equals(this.subject, other.subject) ) {
            return super.equals(other);
            
        } else {
            return true;
        }
    }
    
    @Override
    protected String toStringExtras() {
        return subject.toString();
    }
    
}
