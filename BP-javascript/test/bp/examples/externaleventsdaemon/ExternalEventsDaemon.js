/* global bpjs, noEvents, emptySet */

// Waits for three external events, handle each one. Then quits.

bpjs.setDaemonMode( true );

var in1a = bpjs.Event("in1a");
var in1b = bpjs.Event("in1b");
var ext1 = bpjs.Event("ext1");

bpjs.registerBThread("handler", function() {
    for ( var i=0; i<3; i++ ){
        bsync( {waitFor:ext1} );
        bsync( {request:in1a} );
        bsync( {request:in1b} );
    }
    bpjs.setDaemonMode( false );
});
