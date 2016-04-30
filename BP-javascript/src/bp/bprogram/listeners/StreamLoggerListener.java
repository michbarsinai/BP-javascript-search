package bp.bprogram.listeners;

import bp.bprogram.BProgram;
import bp.bprogram.BThread;
import bp.events.BEvent;
import bp.eventselection.EventSelectionResult;
import java.io.PrintStream;

/**
 * Listens to a BProgram, sends the events to an output stream.
 * Defaults to {@code System.out}.
 * @author michael
 */
public class StreamLoggerListener implements BProgramListener {
    
    private final PrintStream out;
    
    public StreamLoggerListener( PrintStream aStream ){
        out = aStream;
    }
    
    public StreamLoggerListener() {
        this( System.out );
    }

    @Override
    public void started(BProgram bp) {
        out.println("***:" + bp.getName() + " Started");
    }

    @Override
    public void ended(BProgram bp) {
        out.println("***:" + bp.getName() + " Ended");
    }

    @Override
    public void eventSelected(BProgram bp, BEvent theEvent) {
        out.println(" **:" + bp.getName() + " Event " + theEvent.toString());
    }

    @Override
    public void superstepDone(BProgram bp, EventSelectionResult.EmptyResult emptyResult) {
        out.println(" **:" + bp.getName() + " SuperstepDone " + emptyResult.toString());
    }

    @Override
    public void bthreadAdded(BProgram bp, BThread theBThread) {
        out.println("  *:" + bp.getName() + " Added " + theBThread.getName());
    }

    @Override
    public void bthreadRemoved(BProgram bp, BThread theBThread) {
        out.println("  *:" + bp.getName() + " Removed " + theBThread.getName());
    }
    
}
