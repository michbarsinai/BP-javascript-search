bpjs.registerBThread("Time and Position update", function () {

// Local state variables
    var satVel = 1;
    var satPos = 0;
    var simTime = 0;
    // An infinite loop for state update
    while (true) {

        // Wait for events that affect the state of the b-thread
        var e = bsync({waitFor: [Tick, LThrust, RThrust]});
        // Update the state based on the fired event
        if (e.equals(Tick)) {
            // Update position and time
            satPos += satVel;
            simTime++;
            // Request to fire a position update event
            bsync({
                request: new PosUpdate(simTime, satPos, satVel),
                block: [LThrust, RThrust, TakePicture, AnyObsAlertEvent, ObsAvoided, StartSimulation]});
        } else if (e.equals(LThrust)) {
            // Update Satelite velocity
            satVel += 0.1;
        } else if (e.equals(RThrust)) {
            // Update Satelite velocity
            satVel -= 0.1;
        }
    }
});
bpjs.registerBThread("Take Pictures", function () {
    while (true) {
        var e = bsync({waitFor: AnyPosUpdateEvent});
        if ((e.SatPos % 100) < e.SatVel) {
            // Request to take a picture
            bsync({request: TakePicture, block: AnyPosUpdateEvent});
        }
    }
});
bpjs.registerBThread("Obstacle avoidance", function () {
    var flag = false;
    while (true) {
        var e = bsync({waitFor: [AnyPosUpdateEvent, AnyObsAlertEvent]});

        if (AnyPosUpdateEvent.contains(e) && flag) {

            // Is the obstacle in the path of the satellite? 
            if ((e.SatVel >= ((obspos - e.SatPos) / (obsendtime - e.SimTime))) &&
                    (e.SatVel <= ((obspos - e.SatPos) / (obsstarttime - e.SimTime)))) {

                // Slow the satelite down
                bsync({request: RThrust});

            } else {

                // Mark that the risk is removed
                bsync({request: ObsAvoided});
                flag = false;
            }

        } else if (AnyObsAlertEvent.contains(e) && !flag) {
            flag = true;
            var obspos = e.ObsPosition;
            var obsstarttime = e.ObsStartTime;
            var obsendtime = e.ObsEndTime;
        }
    }
});
