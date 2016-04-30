package bp.tasks;

import bp.BEvent;
import bp.BThread;

/**
 * A task to resume a BThread from a BSync operation.
 */
public class ResumeBThread extends BPTask {
    private final BThread bt;
    private final BEvent event;

    public ResumeBThread(BThread aBThread, BEvent selectedEvent) {
        bt = aBThread;
        event = selectedEvent;
    }

    @Override
    protected void run() {
         bt.resume(event);
    }

    @Override
    public String toString() {
        return String.format("[Resume: %s event:%s]", bt, event);
    }
}
