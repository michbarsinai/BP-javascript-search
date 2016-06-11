/* global bpjs */

/*
 * This BProgram uses the data field of events.
 */

var visibleEvents = bpjs.EventSet( function(e) {
    return e.data.eventType === "visible";
});
var hiddenEvents = bpjs.EventSet( function(e) {
    return e.data.eventType === "hidden";
});

// create and request a visible event.
bpjs.registerBThread( "requestor", function(){
   bsync({request: bpjs.Event("e1", {eventType:"visible", nextEventName:"e2"})});
});

// This BThread waits for the first visible event, then
// requests an event based on the selected event.
bpjs.registerBThread( "waitForVisible", function(){
    var e = bsync({waitFor:visibleEvents});
    bsync({request:bpjs.Event(e.data.nextEventName, {eventType:"hidden"})});
});

// Waits for a hidden event, and requests an event based on it.
bpjs.registerBThread( "waitForHidden", function(){
    var ve = bsync({waitFor:visibleEvents});
    var he = bsync({waitFor:hiddenEvents});
    bsync({request:bpjs.Event(ve.name + he.name)});
});