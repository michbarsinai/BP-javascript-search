/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs.events;

import bp.events.BEvent;
import java.util.Optional;

/**
 * Base class for events in the {@code lscoverbpjs} package.
 * 
 * @author michael
 */
public abstract class LscEvent extends BEvent  {
    
    private Optional<String> chartId;
    
    protected LscEvent( String aChartId ) {
        this(Optional.ofNullable(aChartId));
    }
    
    protected LscEvent( Optional<String> aChartId ) {
        chartId = aChartId;
    }
    
    public Optional<String> getChartId() {
        return chartId;
    }

    public void setChartId(Optional<String> chartId) {
        this.chartId = chartId;
    }
    
    @Override
    public boolean equals( Object otherObj ) {
        return (otherObj instanceof LscEvent) 
                && chartId.equals(((LscEvent)otherObj).getChartId());
    }
    
    @Override
    public int hashCode() {
        return getChartId().hashCode();
    }
    
    @Override
    public String toString() {
        String[] cn = getClass().getCanonicalName().split("\\.");
        String extras = toStringExtras().trim();
        return String.format("[%s%s%s]", 
                cn[cn.length-1], 
                (extras.isEmpty() ? "" : " "),
                extras);
        
    }
    
    protected String toStringExtras() {
        return "";
    }
    
}
