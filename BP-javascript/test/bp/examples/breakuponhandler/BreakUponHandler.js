/* global bp */

bp.registerBThread(function () {
    var testValue = "internalValue";
    setBreakUponHandler( function(evt){
       bp.enqueueExternalEvent(evt); 
       bp.enqueueExternalEvent(bp.Event(testValue)); 
    });
    bsync({breakUpon:bp.Event("boom")});
});

bp.registerBThread(function(){
   bsync({request:bp.Event("boom")});
});
