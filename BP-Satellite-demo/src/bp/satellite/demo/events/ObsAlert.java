package bp.satellite.demo.events;

import bp.events.BEvent;

/**
 * A class for New Obstacle Alert .
 */

public class ObsAlert extends BEvent{
	  public static int ObsStartTime;
	  public static int ObsEndTime;
	  public static int ObsPosition;

	  public ObsAlert(int ObsStartTime, int ObsEndTime, int ObsPosition) {
	        super();
	        ObsAlert.ObsStartTime = ObsStartTime;
	        ObsAlert.ObsEndTime = ObsEndTime;
	        ObsAlert.ObsPosition = ObsPosition;
	    }

	  @Override
	    public String toString() {
	        return  "ObsAlert(" + ObsStartTime + "," + ObsEndTime + "," + ObsPosition + ")";
		  //return  "ObsAlert";
	    }
}