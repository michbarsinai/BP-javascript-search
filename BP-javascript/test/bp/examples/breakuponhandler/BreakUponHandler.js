/* global bp */

bp.registerBThread(function () {
    setBreakUponHandler( function(evt){
       bp.enqueueExternalEvent(evt); 
    });
    bsync({breakUpon:bp.Event("boom")});
});

bp.registerBThread(function(){
   bsync({request:bp.Event("boom")});
});
