package bp.satellite.demo.events;

import bp.events.BEvent;

/**
 * A class for Position Update .
 */

public class PosUpdate extends BEvent{
	  public float SatPos;
          public int SimTime;

	  public PosUpdate(int SimTime, float SatPos) {
	        super();
                this.SimTime = SimTime;
	        this.SatPos = SatPos;
	    }

	  @Override
	    public String toString() {
	        return  "PosUpdate(" + SimTime + "," + SatPos  + ")";
		  //return  "PosUpdate";
	    }
}