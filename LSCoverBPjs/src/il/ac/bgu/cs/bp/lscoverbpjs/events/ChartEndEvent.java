/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs.events;

/**
 *
 * @author michael
 */
public class ChartEndEvent extends LscEvent {
    
    public ChartEndEvent(String aChartId) {
        super(aChartId);
    }
    
    @Override
    public boolean equals( Object other ) {
        if ( other == null ) return false;
        if ( other == this ) return true;
        return (other instanceof ChartEndEvent) && super.equals(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
}
