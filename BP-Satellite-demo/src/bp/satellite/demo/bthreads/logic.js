bpjs.registerBThread("Time and Position update", function () {
    var satVel = 1;
    var satPos = 0;
    var simTime = 0;

    while (true) {
        var LE = bsync({waitFor: [Tick, LThrust, RThrust]});
        if (Tick.contains(LE)) {
            satPos += satVel;
            simTime++;
            bsync({request: new PosUpdate(simTime,satPos, satVel), block: [LThrust,RThrust,TakePicture,obsalert,ObsAvoided,StartSimulation]});
        } else if (LThrust.contains(LE)) {
            satVel += 0.1;
        } else {
            satVel -= 0.1;
        }


    }
});

bpjs.registerBThread("Take Pictures", function () {
    var oldpos=0;
    while (true) {
        var e = bsync({waitFor: posupdate});
        if ((e.SatPos % 100) < e.SatVel) {
            if (e.SatPos-oldpos<100){  
            }else{
            bsync({request: TakePicture});
            oldpos=e.SatPos-e.SatVel;
            }
        }
    }
});

bpjs.registerBThread("Obstacle avoidance", function () {
 var flag = 0;
    while (true) {
        var e = bsync({waitFor: [posupdate, obsalert]});
        /* Is the obstacle in the path of the Satellite? */
        if (posupdate.contains(e) & flag==1 & (e.SatVel>=((obspos-e.SatPos)/(obsendtime-e.SimTime))) &  (e.SatVel<=((obspos-e.SatPos)/(obsstarttime-e.SimTime)))){
            bsync({request: RThrust});
        }else if (posupdate.contains(e) & flag==1 & ((e.SatVel<((obspos-e.SatPos)/(obsendtime-e.SimTime)))|e.SatVel>((obspos-e.SatPos)/(obsstarttime-e.SimTime)))){
           bsync({request: ObsAvoided}); 
           flag=0;
        } else if (obsalert.contains(e) & flag==0){
            flag=1;
            var obspos=e.ObsPosition;
            var obsstarttime=e.ObsStartTime;
            var obsendtime=e.ObsEndTime;
        }
    }
});
