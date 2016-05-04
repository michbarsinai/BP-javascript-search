/* global bpjs, noEvents, emptySet, in1a, in1b, in1c, in2a, in2b, in2c, ext1, ext2 */

// Waits for three external events, handle each one. Then quits.

bpjs.setDaemonMode( true );

bpjs.registerBThread("handler", function() {
    for ( var i=0; i<3; i++ ){
        bsync( noEvents, ext1, emptySet );
        bsync( in1a, emptySet, emptySet );
        bsync( in1b, emptySet, emptySet );
    }
    bpjs.setDaemonMode( false );
});


