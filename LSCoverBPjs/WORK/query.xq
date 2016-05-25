(: This file generates code for lifeline CABs. :)
declare variable $nl as xs:string := "&#10;";
declare function local:Event( $eventName as xs:string? ) as xs:string? {
  concat("bpjs.Event('", $eventName, "')" )
};
declare function local:DynamicEvent( $eventName as xs:string?, $eventId as xs:string ) as xs:string? {
  concat("bpjs.Event('", $eventName, "'+",$eventId,")" )
};

declare function local:lifelineCAB( $chartId as xs:string?, $name as xs:string?, $locationCount as xs:integer ) as xs:string? {
  let $enterEventNameSeed := concat("enter|", $name, "@")
  let $leaveEventNameSeed := concat("leave|", $name, "@")
  return concat(
    "bpjs.registerBThread( function(){", $nl,
    "  bSync( {waitFor:", local:Event(concat("chartStart|", $chartId)),"});", $nl,
    "  for ( var i=1; i<=", $locationCount, "; i++) {", $nl,
    (: Dealing with waiting for sub-charts goes here. :)
    "     bsync({request:", local:DynamicEvent($enterEventNameSeed,"i"), ", block:[VisibleEvents, ", local:Event(concat("chartEnd|",$chartId)), "]});", $nl,
    "     bsync({request:", local:DynamicEvent($leaveEventNameSeed,"i"), "'+i), block:", local:Event(concat("chartEnd|",$chartId)), "});", $nl,
    "  }", $nl,
    "})"
  )
};

for $ll in doc("SimpleLSC.xml")/lsc/lifeline
let $name := data($ll/@name)
let $chartId := data($ll/../@id)
let $count := xs:integer($ll/@location-count)
return local:lifelineCAB($chartId, $name, $count)
