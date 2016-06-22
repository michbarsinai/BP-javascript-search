/* global bpjs */
/* 
 * Library for LSC atoms, such as events and event sets.
 * 
 * Author: Michael Bar-Sinai
 */

var LSC = (function(){
    var V_VISIBLE = "visible";
    var V_HIDDEN = "hidden";
    return {
      
     visibleEvents: bpjs.EventSet( function(e){
         return e.data.visibility === V_VISIBLE;
     }),
     
     hiddenEvents: bpjs.EventSet( function(e){
        return e.data.visibility === V_HIDDEN;
     }),
     
     Enabled: function(e) { 
         return bpjs.Event("Enabled(" + e.name + ")", {type:"enabled", vent:e});
     },
     
     Enter: function( loc, chartId ) {
         return bpjs.Event("Enter("+loc+")@"+chartId, {type:"enter", location:loc, chartId:chartId});
     },
     
     Leave: function( loc, chartId ) {
         return bpjs.Event("Leave("+loc+")@"+chartId, {type:"leave", location:loc, chartId:chartId});
     },
     
     Start: function( chartId ) {
         return bpjs.Event("ChartStart(" + chartId + ")", {type:"start", chartId:chartId});
     },
     
     End: function( chartId ) {
         return bpjs.Event("ChartEnd(" + chartId + ")", {type:"end", chartId:chartId});
     },
     
     Message: function( fromLoc, toLoc, content, chartId ) {
         return bpjs.Event("Message(" + fromLoc +"->" + toLoc + ")", 
                           {type:"message", from:fromLoc, to:toLoc, content:content, chartId:chartId} );
     },
     
     name: function(){ return "LSCoBPJS"; }
      
    };
})();