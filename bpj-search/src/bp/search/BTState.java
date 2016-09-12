package bp.search;

import bp.bprogram.BThread;
import bp.bprogram.RWBStatement;
import bp.search.bthreads.SimulatorBThread;
import org.mozilla.javascript.ContinuationPending;

/**
 * A class for capturing be-thread states.
 */
public class BTState {

    /**
     * The be-thread whose state is captured
     */
    public final BThread bt;
//    public boolean simMode;

    /**
     * Continuation object.
     */
    protected ContinuationPending cont;

    /**
     * Temporary storage for bpSync parameters
     */
    public transient RWBStatement currentRwbStatement;

    public BTState(BThread bt) {
        this.bt = bt;
        currentRwbStatement = bt.getCurrentRwbStatement();
        this.cont = bt.getContinuation();
    }

    // need to implement visitor someday
    public BTState(SimulatorBThread bt) {
        this((BThread) bt);
//        this.simMode = bt._simMode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bt);
        sb.append("\n");
        for (int i = 0; i < bt.toString().length(); i++) {
            sb.append("-");
        }
        sb.append("\n");
        sb.append("R=").append(bt.getCurrentRwbStatement().getRequest());
        sb.append("\n");
        sb.append("W=").append(bt.getCurrentRwbStatement().getWaitFor());
        sb.append("\n");
        sb.append("B=").append(bt.getCurrentRwbStatement().getBlock());
        sb.append("\n");
        return sb.toString();
    }

    public void restore() {
        bt.setCurrentRwbStatement(currentRwbStatement);
        bt.setContinuation(cont);
        bt.setAlive(true);
    }

    private void bplog(String s) {
        System.out.println("[BTState:"
                + bt.getName() + "]: ");
    }

}
