package bp.tasks;

import bp.bprogram.BThread;

/**
 * A task to start a BThread.
 */
public class StartBThread extends BPTask {
    private final BThread bthread;

    public StartBThread(BThread aBThread) {
        bthread = aBThread;
    }

    @Override
    protected void run() {
        bthread.start();
    }
}
