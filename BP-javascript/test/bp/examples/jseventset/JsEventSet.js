/*  global bpjs */

// This script tests the JsEventSet class in the wild.
// Assuming that Javascript is wild. It is.

var firsts = bpjs.EventSet( function(e){
    return e.getName().startsWith("1st");
} );

var firstEvent = bpjs.Event("1stEvent");
var secondEvent = bpjs.Event("2ndEvent");

bpjs.registerBThread("first", function() {
    bsync({request:firstEvent});
});

/**
 * This Bthread will wait for all events that fall into
 * {@code eventSet}, and then will request the second event.
 */
bpjs.registerBThread("second", function() {
    bsync({waitFor:firsts});
    bsync({request:secondEvent});
});