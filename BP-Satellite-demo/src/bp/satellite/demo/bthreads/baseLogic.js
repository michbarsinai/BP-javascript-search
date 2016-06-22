bpjs.registerBThread("Time and Position update Base", function () {
    posBt=this;
    
    this.satPos = 0;
    this.simTime = 0;

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
        if ((e.SatPos % 100) == 0) {
            bsync({request: TakePicture});
        }
    }
});
