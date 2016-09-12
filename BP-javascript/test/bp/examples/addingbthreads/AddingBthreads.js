/* global bpjs, noEvents, emptySet */
/* 
 * This little app adds bthreads dynamically.
 */

bpjs.log.info("Program Loaded");

// Define the events.
var kidADone = bpjs.Event("kidADone");
var kidBDone = bpjs.Event("kidBDone");
var parentDone = bpjs.Event("parentDone");

bpjs.registerBThread("parentBThread", function () {
    
    bpjs.log.info("parent started");
    
    // first one, text for behavior on the start() method.
    bpjs.registerBThread("kidA1", function() {
        bpjs.log.info("kid a1 started");
        bsync(kidADone, emptySet, parentDone);
    });
    bpjs.registerBThread("kidB1", function() {
        bpjs.log.info("kid b1 started");
        bsync(kidBDone, emptySet, parentDone);
    });
    bsync(parentDone, emptySet, emptySet);


    // second one, test for behavior on the resume() method.
    bpjs.registerBThread("kidA2", function () {
        bsync(kidADone, emptySet, parentDone);
    });
    bpjs.registerBThread("kidB2", function () {
        bsync(kidBDone, emptySet, parentDone);
    });
    bsync(parentDone, emptySet, emptySet);

});


