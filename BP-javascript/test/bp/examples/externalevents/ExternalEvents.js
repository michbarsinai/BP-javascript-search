/* global bpjs, noEvents, emptySet, in1a, in1b, in1c, in2a, in2b, in2c, ext1, ext2 */


bpjs.registerBThread("In1", function() {
    bsync( in1a, emptySet, emptySet );
    bsync( noEvents, ext1, emptySet );
    bsync( in1b, emptySet, emptySet );
});
