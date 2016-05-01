/* global bpjs, noEvents, emptySet, hotEvent, coldEvent, allDone */

bpjs.registerBThread("HotBt", function () {
    bsync(hotEvent, emptySet, emptySet);
    bsync(hotEvent, emptySet, emptySet);
    bsync(hotEvent, emptySet, emptySet);
});

bpjs.registerBThread("ColdBt", function () {
    bsync(coldEvent, emptySet, emptySet);
    bsync(coldEvent, emptySet, emptySet);
    bsync(coldEvent, emptySet, emptySet);
});

bpjs.registerBThread("AlternatorBt", function () {
    for (i = 0; i < 3; i++) {
        testCall("marker1");
        bsync(noEvents, coldEvent, hotEvent); // block hot first, so as not to burn our thumb.
        testCall("marker2");
        bsync(noEvents, hotEvent, coldEvent);
        testCall("marker3");
    }
    bsync(allDone, emptySet, emptySet);
});