/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs.events;

/**
 * An event signaling that a chart has started.
 * 
 * @author michael
 */
public class ChartStartEvent extends LscEvent {
    
    public ChartStartEvent(String aChartId) {
        super(aChartId);
    }
    
    @Override
    public boolean equals( Object other ) {
        if ( other == null ) return false;
        if ( other == this ) return true;
        return (other instanceof ChartStartEvent) && super.equals(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
}
