package bp.tasks;

import bp.BEvent;
import bp.BThread;

/**
 * Created by moshewe on 06/07/2015.
 */
public class ResumeBThread extends BPTask {
    private final BThread bt;
    private final BEvent event;

    public ResumeBThread(BThread _bt, BEvent _event) {
        this.bt = _bt;
        this.event = _event;
    }

    @Override
    public void run() {
        if ( bt.getCurrentRwbStatement().shouldWakeFor(event) ) {
            bt.resume(event);
        }
    }

    @Override
    public String toString() {
        return "Resume" + bt;
    }
}
