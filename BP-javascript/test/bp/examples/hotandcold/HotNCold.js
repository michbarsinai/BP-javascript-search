/* global bp, emptySet, noEvents */

var coldEvent = bp.Event("coldEvent");
var hotEvent = bp.Event("hotEvent");

bp.registerBThread("HotBt", function() {
    bsync(hotEvent, emptySet, emptySet);
    bsync(hotEvent, emptySet, emptySet);
    bsync(hotEvent, emptySet, emptySet);
});

bp.registerBThread("ColdBt", function() {
    bsync(coldEvent, emptySet, emptySet);
    bsync(coldEvent, emptySet, emptySet);
    bsync(coldEvent, emptySet, emptySet);
});

bp.registerBThread("AlternatorBt", function() {
    for (i = 0; i < 3; i++) {
        bsync({waitFor:coldEvent, block:hotEvent}); // block hot first, so as not to burn our thumb.
        bsync({waitFor:hotEvent, block:coldEvent});
    }
    bsync(bp.Event("allDone"), emptySet, emptySet);
});
