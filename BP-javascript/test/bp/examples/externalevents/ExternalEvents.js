/* global bpjs, noEvents, emptySet */

var in1a = bpjs.Event("in1a");
var in1b = bpjs.Event("in1b");
var ext1 = bpjs.Event("ext1");

bpjs.registerBThread("In1", function() {
    bsync( in1a, emptySet, emptySet );
    bsync( {waitFor:ext1} );
    bsync( in1b, emptySet, emptySet );
});
