package bp.satellite.demo.events;

import bp.events.BEvent;

/**
 * A class for Position Update .
 */

public class PosUpdate extends BEvent{
	  public double SatPos;
	  public double SatVel;
          public int SimTime;

	  public PosUpdate(int SimTime, double SatPos, double SatVel) {
	        super();
                this.SimTime = SimTime;
	        this.SatPos = SatPos;
	        this.SatVel = SatVel;
	    }

	  @Override
	    public String toString() {
	        return  "PosUpdate(" + SimTime + "," + SatPos  + "," + SatVel + ")";
		  //return  "PosUpdate";
	    }
}