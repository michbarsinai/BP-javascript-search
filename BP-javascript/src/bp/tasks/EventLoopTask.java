package bp.tasks;

import bp.Arbiter;
import bp.BEvent;
import bp.BPApplication;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by moshewe on 04/07/2015.
 */
public class EventLoopTask extends BPTask {

    protected BPApplication _bp;
    protected Arbiter _arbiter;

    public EventLoopTask(BPApplication bp, Arbiter arbiter) {
        this._bp = bp;
        this._arbiter = arbiter;
    }

    @Override
    public void run() {
        while (true) {
            bplog( "-----" + _bp.getBThreads().size() + " BThreads");
            if ( _bp.getBThreads().isEmpty() ) {
                bplog("=== ALL DONE!!! ===");
                return;
            }

            BEvent next = _arbiter.nextEvent();
            if (next == null) {
                bplog("no event chosen, waiting for an external event to be fired...");
                next = _bp.dequeueExternalEvent();
            }
            
            // TODO notify the event logger (if any) of the selected event.
            bplog(next + " is an output event.");
            _bp.emit(next);

            try {
                _bp.triggerEvent(next);
            } catch (InterruptedException ex) {
                Logger.getLogger(EventLoopTask.class.getName()).log(Level.SEVERE, null, ex);
            }
            _bp.bthreadCleanup();
        }
    }
}
