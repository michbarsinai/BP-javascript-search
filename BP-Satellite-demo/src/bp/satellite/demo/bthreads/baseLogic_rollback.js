bpjs.unRegisterBThread("Time and Position update");
bpjs.unRegisterBThread("Take Pictures");
bpjs.unRegisterBThread("Obstacle avoidance");
bpjs.unRegisterBThread("RollBack Enable Disable");


bpjs.registerBThread("Time and Position update Base", function () {
    posBt = this;

    this.satPos = posBtN.satPos;
    this.simTime = posBtN.simTime;
    
    while (true) {
        bsync({waitFor: [Tick]});

        this.satPos++;
        this.simTime++;
        bsync({request: new PosUpdate(this.simTime, this.satPos), block: [Tick, TakePicture, StartSimulation]});
    }
});

bpjs.registerBThread("Take Pictures Base", function () {
    while (true) {
        var e = bsync({waitFor: AnyPosUpdateEvent});
        if ((e.SatPos % 100) < (e.SatVel)) {
            bsync({request: TakePicture});
        }
    }
});
