package tictactoe.bThreads;

import static bp.eventSets.EventSetConstants.none;
import static tictactoe.events.StaticEvents.gameOver;
import static tictactoe.events.StaticEvents.OEvents;
import static tictactoe.events.StaticEvents.XEvents;
import bp.BThread;
import bp.eventSets.EventSet;
import bp.exceptions.BPJException;

/**
 * BThread that blocks players from playing when its not their turn.
 */
public class EnforceTurns extends BThread {

	public void runBThread() throws BPJException {
		interruptingEvents = new EventSet(gameOver);
		while (true) {
			bsync(none, XEvents, OEvents);
			bsync(none, OEvents, XEvents);

		}
	}

}