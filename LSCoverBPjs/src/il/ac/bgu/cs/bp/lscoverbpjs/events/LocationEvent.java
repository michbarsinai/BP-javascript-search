/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs.events;

import java.util.Objects;
import java.util.Optional;

/**
 * Events for a Lifeline entering or leaving a location.
 * @author michael
 */
public abstract class LocationEvent extends LscEvent {
    
    public static class Enter extends LocationEvent {
        public Enter(String location, String aChartId) {
            super(location, aChartId);
        }
    }
    
    public static class Leave extends LocationEvent {
        public Leave(String location, String aChartId) {
            super(location, aChartId);
        }
    }
    
    private final String location;

    public LocationEvent(String location, String aChartId) {
        super(aChartId);
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    @Override
    protected String toStringExtras() {
        return getLocation();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.location);
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LocationEvent other = (LocationEvent) obj;
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        return super.equals(obj);
    }
    
    
    
}
