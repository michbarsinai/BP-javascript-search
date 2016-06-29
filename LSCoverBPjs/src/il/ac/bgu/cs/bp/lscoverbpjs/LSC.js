/* global bpjs */
/* 
 * Library for LSC atoms, such as events and event sets.
 * 
 * Author: Michael Bar-Sinai
 */

var lsc = (function(){
    var V_VISIBLE = "visible";
    var V_HIDDEN = "hidden";
    return {
      
     visibleEvents: bpjs.EventSet( "VisibleEvents", function(e){
         return (e.data !== null) ? e.data.visibility === V_VISIBLE : false;
     }),
     
     hiddenEvents: bpjs.EventSet( "HiddenEvents", function(e){
        return (e.data !== null) ? e.data.visibility === V_HIDDEN : false;
     }),
     
     terminationEvents: function( chartId ) {
         return bpjs.EventSet( "terminations(" + chartId + ")", function(e) {
            return e.data.group==="termination" && e.data.chartId===chartId; 
         });
     },
     
     /**
      * Creates an event set containing all leave evetns from the passed location 
      * list in the given chart.
      * @param {String} locationList list of locations, comma separated.
      * @param {type} chartId id of chart the locations are in
      * @returns {EventSet}
      */
     leaveEvents: function( locationList, chartId ) {
       return bpjs.EventSet( "leaveEvents(" + locationList +")", function(e){
           return (e.data.type === "leave") 
                   && (e.data.chartId === chartId)
                   && (locationList.indexOf(e.data.location) !== -1);
       });  
     },
     
     Message: function(fromLoc, toLoc, content ) {
         return bpjs.Event(fromLoc + "->" + toLoc + ":" + content, 
                            {type:"message", visibiliy:V_VISIBLE, 
                             content:content, from:fromLoc, to:toLoc});
     },
     
     Sync: function( syncId, chartId ){
         return bpjs.Event("Sync<" + syncId + ">", 
                            { type:"sync", visibility: V_HIDDEN, chartId:chartId});
     },
     
     Enabled: function(e) { 
         return bpjs.Event("Enabled(" + e.name + ")", {type:"enabled", event:e});
     },
     
     Enter: function( loc, chartId ) {
         return bpjs.Event("Enter("+loc+")@"+chartId, {type:"enter", location:loc, chartId:chartId});
     },
     
     Leave: function( loc, chartId ) {
         return bpjs.Event("Leave("+loc+")@"+chartId, {type:"leave", location:loc, chartId:chartId});
     },
     
     Start: function( chartId ) {
         return bpjs.Event("ChartStart(" + chartId + ")", {type:"start", chartId:chartId, visibility:V_HIDDEN});
     },
     
     End: function( chartId ) {
         return bpjs.Event("ChartEnd(" + chartId + ")",   {group:"termination", type:"end", chartId:chartId, visibility:V_HIDDEN});
     },
     
     Done: function( chartId ) {
         return bpjs.Event("ChartDone(" + chartId + ")",   {group:"termination", type:"done", chartId:chartId, visibility:V_HIDDEN});
     },
     
     name: function(){ return "LSCoBPJS"; }
      
    };
})();