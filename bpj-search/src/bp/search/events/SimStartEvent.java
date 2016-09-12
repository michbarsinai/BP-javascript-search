package bp.search.events;

import bp.events.BEvent;

/**
 * Created by orelmosheweinstock on 4/9/15.
 */
public class SimStartEvent extends BEvent {

    private static SimStartEvent _instance;

    static {
        _instance = new SimStartEvent();
    }

    private SimStartEvent() {
        super("SimStart");
    }

    public static SimStartEvent getInstance() {
        return _instance;
    }

//    public SimStartEvent(String _name) {
//        this();
//    }
}
