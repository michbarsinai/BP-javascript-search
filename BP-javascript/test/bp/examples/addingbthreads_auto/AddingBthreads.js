/* global bpjs, noEvents, emptySet */
/* 
 * This little app adds bthreads dynamically.
 */

bpjs.bplog("Program Loaded");

// Define the events.
var kidADone  = bpjs.Event("kidADone");
var kidBDone  = bpjs.Event("kidBDone");
var parentDone= bpjs.Event("parentDone");

bpjs.registerBThread("parentBThread", function () {
    
    bpjs.bplog("parent started");
    
    // first one, text for behavior on the start() method.
    bpjs.registerBThread( function() {
        bpjs.bplog("kid a1 started");
        bsync(kidADone, emptySet, parentDone);
    });
    bpjs.registerBThread( function() {
        bpjs.bplog("kid b1 started");
        bsync(kidBDone, emptySet, parentDone);
    });
    bsync( parentDone, emptySet, emptySet );
    
    
    // second one, test for behavior on the resume() method.
    bpjs.registerBThread( function() {
        bsync(kidADone, emptySet, parentDone);
    });
    bpjs.registerBThread( function() {
        bsync(kidBDone, emptySet, parentDone);
    });
    bsync( parentDone, emptySet, emptySet );
    
});


