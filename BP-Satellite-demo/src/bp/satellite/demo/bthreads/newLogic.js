bpjs.unRegisterBThread("Time and Position update Base");
bpjs.unRegisterBThread("Take Pictures Base");

bpjs.registerBThread("Time and Position update", function () {

    // Local state variables
    var satVel = 1;
    var satPos = posBt.satPos;
    var simTime = posBt.simTime;

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
        if ((e.SatPos % 100) < (e.SatVel)) {
            // Request to take a picture
            bsync({request: TakePicture, block: AnyPosUpdateEvent});
        }
    }
});
bpjs.registerBThread("Obstacle avoidance", function () {
    var flagobs = false;
    var flagvel = false;
    while (true) {
        var e = bsync({waitFor: [AnyPosUpdateEvent, AnyObsAlertEvent]});

        if (AnyPosUpdateEvent.contains(e) && (flagobs||flagvel)) {

            // Is the obstacle in the path of the satellite? 
            if ((e.SatVel >= ((obspos - e.SatPos) / (obsendtime - e.SimTime))) &&
                    (e.SatVel <= ((obspos - e.SatPos) / (obsstarttime - e.SimTime))) &&
                    (obsstarttime>=e.SimTime) && (obspos>=e.SatPos)) {

                // Slow the satelite down
                bsync({request: RThrust});

            } 
            if ((e.SatVel >= ((obspos - e.SatPos) / (obsendtime - e.SimTime))) &&
                    (e.SatVel <= ((obspos - e.SatPos) / (obsstarttime - e.SimTime))) &&
                    (obsstarttime>=e.SimTime) && (obspos>=e.SatPos)) {

                // Slow the satelite down
                bsync({request: RThrust});
                flagvel=true;

           }
           if(e.SimTime>=obsendtime && e.SatVel<1 && flagvel) {
               bsync({request: LThrust}); 
           }else if(flagvel==true && e.SatVel==1 && flagobs == false ) {
               flagvel=false;
               bsync({request:VelRecovery });
           }else{

                // Mark that the risk is removed
                bsync({request: ObsAvoided});
                flagobs = false;
            }
        } else if (AnyObsAlertEvent.contains(e) && !flagobs) {
            flagobs = true;
            flagvel=true;
            var obspos = e.ObsPosition;
            var obsstarttime = e.ObsStartTime;
            var obsendtime = e.ObsEndTime;
        }
    }
});
