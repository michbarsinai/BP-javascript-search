/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs.events;

/**
 *
 * @author michael
 */
public class MessagePassedEvent extends VisibleEvent {
    
    private final String fromLoc;
    private final String toLoc;
    private final String content;

    public MessagePassedEvent(String fromLoc, String toLoc, String content, String aChartId) {
        super(aChartId);
        this.fromLoc = fromLoc;
        this.toLoc = toLoc;
        this.content = content;
    }

    public String getFromLoc() {
        return fromLoc;
    }

    public String getToLoc() {
        return toLoc;
    }

    public String getContent() {
        return content;
    }
    
    @Override
    public boolean equals( Object other ) {
        if ( other == null ) return false;
        if ( other == this ) return true;
        if ( other instanceof MessagePassedEvent ) {
            MessagePassedEvent otherMpe = (MessagePassedEvent) other;
            if ( ! fromLoc.equals(((MessagePassedEvent) other).getFromLoc()) ) return false;
            if ( ! toLoc.equals(((MessagePassedEvent) other).getToLoc()) )     return false;
            if ( ! content.equals(((MessagePassedEvent) other).getContent()) ) return false;
            return super.equals(otherMpe);
            
        } else {
            return false;
        }
        
    }

    @Override
    public int hashCode() {
        return toLoc.hashCode() ^ fromLoc.hashCode();
    }
    
    @Override
    protected String toStringExtras() {
        return getFromLoc() + "->" + getToLoc() + ": " + getContent();
    }
    
}
