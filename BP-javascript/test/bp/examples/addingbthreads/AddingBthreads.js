/* global bpjs, noEvents, emptySet, parentDone, kidADone, kidBDone */
/* 
 * This little app adds bthreads dynamically.
 */

bpjs.bplog("Program Loaded");

bpjs.registerBThread("parentBThread", function () {
    
    bpjs.bplog("parent started");
    
    // first one, text for behavior on the start() method.
    bpjs.registerBThread("kidA1", function() {
        bpjs.bplog("kid a1 started");
        bsync(kidADone, emptySet, parentDone);
    });
    bpjs.registerBThread("kidB1", function() {
        bpjs.bplog("kid b1 started");
        bsync(kidBDone, emptySet, parentDone);
    });
    bsync( parentDone, emptySet, emptySet );
    
    
    // second one, test for behavior on the resume() method.
    bpjs.registerBThread("kidA2", function() {
        bsync(kidADone, emptySet, parentDone);
    });
    bpjs.registerBThread("kidB2", function() {
        bsync(kidBDone, emptySet, parentDone);
    });
    bsync( parentDone, emptySet, emptySet );
    
});


