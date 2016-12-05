package bp.bprogram.runtimeengine.listeners;

import bp.bprogram.runtimeengine.BProgram;
import bp.bprogram.runtimeengine.BThreadSyncSnapshot;
import bp.events.BEvent;
import bp.eventselection.EventSelectionResult;
import bp.eventselection.EventSelectionResult.EmptyResult;
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
        out.println("---:" + bp.getName() + " Started");
    }

    @Override
    public void ended(BProgram bp) {
        out.println("---:" + bp.getName() + " Ended");
    }

    @Override
    public void eventSelected(BProgram bp, BEvent theEvent) {
        out.println(" --:" + bp.getName() + " Event " + theEvent.toString());
    }

    @Override
    public void superstepDone(BProgram bp, EventSelectionResult.EmptyResult emptyResult) {
        out.println("---:" + bp.getName() + " SuperstepDone " + emptyResult.toString());
        emptyResult.accept(new EmptyResult.VoidVisitor(){
            @Override
            protected void visitImpl(EventSelectionResult.SelectedExternal se) {}

            @Override
            protected void visitImpl(EventSelectionResult.Selected se) {}

            @Override
            protected void visitImpl(EventSelectionResult.Deadlock dl) {
                // print the RBW statements
                bp.currentStatements().forEach( rwbs -> {
                    if ( rwbs == null ) {
                        out.println("XX NULL RWBStatement");
                    } else {
                        if ( rwbs.getBthread() != null ) {
                            out.println("* " + rwbs.getBthread().getName());
                        } else {
                            out.println("* @@unnamed");
                        }
                        out.println(" request: " + rwbs.getRequest() );
                        out.println(" waitFor: " + rwbs.getWaitFor());
                        out.println("   block: " + rwbs.getBlock());
                    }
                });
            }

            @Override
            protected void visitImpl(EventSelectionResult.NoneRequested nr) {}
        });
    }

    @Override
    public void bthreadAdded(BProgram bp, BThreadSyncSnapshot theBThread) {
        out.println("  -:" + bp.getName() + " Added " + theBThread.getName());
    }

    @Override
    public void bthreadRemoved(BProgram bp, BThreadSyncSnapshot theBThread) {
        out.println("  -:" + bp.getName() + " Removed " + theBThread.getName());
    }
    
}
