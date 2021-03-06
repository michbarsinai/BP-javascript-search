/* global bp, noEvents, emptySet */
/* 
 * This little app adds bthreads dynamically.
 */

bp.log.info("Program Loaded");

// Define the events.
var kidADone  = bp.Event("kidADone");
var kidBDone  = bp.Event("kidBDone");
var parentDone= bp.Event("parentDone");

bp.registerBThread("parentBThread", function () {
    
    bp.log.info("parent started");
    
    // first one, text for behavior on the start() method.
    bp.registerBThread( function() {
        bp.log.info("kid a1 started");
        bsync(kidADone, emptySet, parentDone);
    });
    bp.registerBThread( function() {
        bp.log.info("kid b1 started");
        bsync(kidBDone, emptySet, parentDone);
    });
    bsync( parentDone, emptySet, emptySet );
    
    
    // second one, test for behavior on the resume() method.
    bp.registerBThread( function() {
        bsync(kidADone, emptySet, parentDone);
    });
    bp.registerBThread( function() {
        bsync(kidBDone, emptySet, parentDone);
    });
    bsync( parentDone, emptySet, emptySet );
    
});


