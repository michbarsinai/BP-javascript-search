package bprobocode.events.output;

import bprobocode.events.QuantifiedEvent;

/**
 * Created by orelmosheweinstock on 6/14/15.
 */
public class Fire extends QuantifiedEvent {
    public Fire(int pixels) {
        super(pixels, true);
        _name = "Fire";
    }
}
