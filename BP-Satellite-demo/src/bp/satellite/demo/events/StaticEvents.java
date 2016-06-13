package bp.satellite.demo.events;

import bp.events.BEvent;
import bp.eventsets.EventsOfClass;



public class StaticEvents {
    static public BEvent StartSimulation = new BEvent("StartSimulation");
    static public BEvent Tick = new BEvent("Tick");
    static public BEvent LThrust = new BEvent("LThrust");
    static public BEvent RThrust = new BEvent("RThrust");
    static public BEvent TakePicture = new BEvent("TakePicture");
    static public BEvent ObsAvoided = new BEvent("ObsAvoided");
    
    public static final EventsOfClass AnyObsAlertEvent = new EventsOfClass(ObsAlert.class);
    public static final EventsOfClass AnyPosUpdateEvent = new EventsOfClass(PosUpdate.class);
    
}
