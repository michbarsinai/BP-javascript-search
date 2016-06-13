bpjs.registerBThread("Time and Position update", function () {
    var satPos = 0;
    var simTime = 0;

    while (true) {
        var LE = bsync({waitFor: [Tick]});
        if (Tick.contains(LE)) {
            satPos ++;
            simTime++;
            bsync({request: new PosUpdate(simTime,satPos), block: [Tick,TakePicture,StartSimulation]});
        }
    }
});

bpjs.registerBThread("Take Pictures", function () {
    while (true) {
        var e = bsync({waitFor: posupdate});
        if ((e.SatPos % 100) ==0) {
            bsync({request: TakePicture});
        }
    }
});
