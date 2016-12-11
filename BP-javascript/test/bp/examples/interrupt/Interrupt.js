/* global bpjs, noEvents, emptySet  */

/*
 * In this test, we expect two "breaking event"s, and no forbidden ones.
 */

var breakingEvent = bpjs.Event("breaking");
var forbiddenEvent = bpjs.Event("forbidden");

bpjs.registerBThread("requestor", function () {
  // request hotEvent twice.
    bsync( {request:breakingEvent} );
    bsync( {request:breakingEvent} );
});

bpjs.registerBThread("InterruptOnFirst", function () {
  bsync({interrupt:breakingEvent});
  bsync({request:forbiddenEvent});
});

bpjs.registerBThread("InterruptOnSecond", function () {
  bsync({waitFor:breakingEvent});
  bsync({interrupt:breakingEvent});
  bsync({request:forbiddenEvent} );
});
