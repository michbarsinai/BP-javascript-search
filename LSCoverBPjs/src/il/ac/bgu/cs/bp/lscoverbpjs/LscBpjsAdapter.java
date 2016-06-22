/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs;

import bp.events.BEvent;
import bp.eventsets.EventSet;
import bp.eventsets.Events;
import il.ac.bgu.cs.bp.lscoverbpjs.events.ChartEndEvent;
import il.ac.bgu.cs.bp.lscoverbpjs.events.ChartStartEvent;
import il.ac.bgu.cs.bp.lscoverbpjs.events.EnabledEvent;
import il.ac.bgu.cs.bp.lscoverbpjs.events.LocationEvent;
import il.ac.bgu.cs.bp.lscoverbpjs.events.LscEvent;
import il.ac.bgu.cs.bp.lscoverbpjs.events.MessagePassedEvent;
import il.ac.bgu.cs.bp.lscoverbpjs.events.VisibleEvent;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Java-side support for LSC-over-BPjs programs. Methods of this class
 * are called from the Javascript programs with the prefix {@code lsc.}.
 * 
 * <em>NOTE:
 *     Method names are capitalized according to their usage in Javascript.</em>
 * 
 * @author michael
 */
public class LscBpjsAdapter {
    
    private final Deque<String> chartIdStack = new LinkedList<>();
    
    // OK
    public final EventSet VISIBLE_EVENTS = Events.ofClass(VisibleEvent.class);
    
    public BEvent Message( String fromLoc, String toLoc, String content ) {
        return new MessagePassedEvent(fromLoc, toLoc, content, chartIdStack.peek());
    }
    
    // OK
    public BEvent Enabled( LscEvent evt ) {
        return new EnabledEvent(evt);
    }
    
    // OK
    public BEvent Enter( String location ) {
        return new LocationEvent.Enter(location, chartIdStack.peek());
    }
    
    // OK
    public BEvent Leave( String location ) {
        return new LocationEvent.Leave(location, chartIdStack.peek());
    }
    
    // OK
    public BEvent Start( String chartName ) {
        return new ChartStartEvent(chartName);
    }
    
    // OK
    public BEvent End( String chartName ) {
        return new ChartEndEvent(chartName);
    }
    
    public void startChart( String chartId ) {
        chartIdStack.push( chartId );
    }
    
    public void endChart() {
        chartIdStack.pop();
    }
    
    
}
