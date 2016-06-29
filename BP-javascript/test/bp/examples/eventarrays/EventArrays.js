/* global bpjs */


var anEventSet = bpjs.EventSet( "visible", function(e) {
    return e.data !== null ? e.data.eventType === "visible" : false;
});

bpjs.registerBThread( "requestor1", function(){
   bsync({request: bpjs.Event("e11")});
});
bpjs.registerBThread( "requestor2", function(){
   bsync({request: bpjs.Event("e21")});
});

bpjs.registerBThread( "blocker", function(){
    bsync({
        waitFor: [ bpjs.Event("e11"), bpjs.Event("e12"), anEventSet],
        block: [bpjs.Event("e21"), bpjs.Event("e22"), anEventSet ]
    });
});
