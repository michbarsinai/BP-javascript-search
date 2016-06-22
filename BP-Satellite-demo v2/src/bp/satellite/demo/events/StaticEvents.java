package bp.satellite.demo.events;

import bp.events.BEvent;
import bp.eventsets.EventsOfClass;



public class StaticEvents {
    static public BEvent StartSimulation = new BEvent("StartSimulation");
    static public BEvent Tick = new BEvent("Tick");
    static public BEvent TakePicture = new BEvent("TakePicture");
    
    public static final EventsOfClass PosUpdateEvent = new EventsOfClass(PosUpdate.class);
    
}
