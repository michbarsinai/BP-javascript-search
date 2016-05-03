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
        bsync(noEvents, coldEvent, hotEvent); // block hot first, so as not to burn our thumb.
        bsync(noEvents, hotEvent, coldEvent);
    }
    bsync(allDone, emptySet, emptySet);
});