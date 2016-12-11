/* global bp */

bp.registerBThread(function () {
    var testValue = "internalValue";
    setInterruptHandler( function(evt){
       bp.enqueueExternalEvent(evt); 
       bp.enqueueExternalEvent(bp.Event(testValue)); 
    });
    bsync({interrupt:bp.Event("boom")});
});

bp.registerBThread(function(){
   bsync({request:bp.Event("boom")});
});
