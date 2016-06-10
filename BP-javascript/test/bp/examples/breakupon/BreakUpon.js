/* global bpjs, noEvents, emptySet  */

/*
 * In this test, we expect two "breaking event"s, and no forbidden ones.
 */

var breakingEvent = bpjs.Event("breaking");
var forbiddenEvent = bpjs.Event("forbidden");

bpjs.registerBThread("requestor", function () {
  // request hotEvent three times, in different verbosities.
    bsync( {request:breakingEvent} );
    bsync( {request:breakingEvent} );
});

bpjs.registerBThread("BreakOnFirst", function () {
  bsync({breakUpon:breakingEvent});
  bsync({request:forbiddenEvent});
});

bpjs.registerBThread("BreakOnSecond", function () {
  bsync({waitFor:breakingEvent});
  bsync({breakUpon:breakingEvent});
  bsync({request:forbiddenEvent} );
});
