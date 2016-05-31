/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs.events;

/**
 * Base class for visible LSC events, which is more or less messages being passed.
 * 
 * @author michael
 */
public abstract class VisibleEvent extends LscEvent {
    
    public VisibleEvent(String aChartId) {
        super(aChartId);
    }
    
}
