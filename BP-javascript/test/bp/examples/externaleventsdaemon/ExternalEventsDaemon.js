/* global bpjs, noEvents, emptySet */

// Waits for three external events, handle each one. Then quits.

bpjs.setDaemonMode( true );

var in1a = bpjs.Event("in1a");
var in1b = bpjs.Event("in1b");
var ext1 = bpjs.Event("ext1");

bpjs.registerBThread("handler", function() {
    for ( var i=0; i<3; i++ ){
        bsync( noEvents, ext1, emptySet );
        bsync( in1a, emptySet, emptySet );
        bsync( in1b, emptySet, emptySet );
    }
    bpjs.setDaemonMode( false );
});
