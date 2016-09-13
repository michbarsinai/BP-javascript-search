/* global bp */

bp.registerBThread(function () {
    setBreakUponHandler( function(evt){
       bsync({breakUpon:bp.Event("boom")}); // Blows up, can't call bsync here.
    });
    bsync({breakUpon:bp.Event("boom")});
});

bp.registerBThread(function(){
   bsync({request:bp.Event("boom")});
});
