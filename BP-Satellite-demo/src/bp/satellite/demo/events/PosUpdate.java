package bp.satellite.demo.events;

import bp.events.BEvent;

/**
 * A class for Position Update .
 */
public class PosUpdate extends BEvent {

    public float SatPos;
    public float SatVel;
    public int SimTime;

    public PosUpdate(int SimTime, float SatPos, float SatVel) {
        super();
        this.SimTime = SimTime;
        this.SatPos = SatPos;
        this.SatVel = SatVel;
    }

    public PosUpdate(int SimTime, float SatPos) {
        super();
        this.SimTime = SimTime;
        this.SatPos = SatPos;
        this.SatVel = 1.0f;
    }

    @Override
    public String toString() {
        return "PosUpdate(" + SimTime + "," + SatPos + "," + SatVel + ")";
    }
}
