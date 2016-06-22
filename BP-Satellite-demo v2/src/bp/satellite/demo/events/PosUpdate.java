package bp.satellite.demo.events;

import bp.events.BEvent;

/**
 * A class for Position Update .
 */

public class PosUpdate extends BEvent{
	  public double SatPos;
          public int SimTime;

	  public PosUpdate(int SimTime, double SatPos) {
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