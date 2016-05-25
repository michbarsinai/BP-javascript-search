declare variable $nl as xs:string := "&#10;";

declare function local:Event( $eventName as xs:string? ) as xs:string? {
  concat("bpjs.Event('", $eventName, "')" )
};

declare function local:blockUntilCAB($toBlock as xs:string?, $untilEvent as xs:string?)
as xs:string? {
  string-join((
    "bpj.registerBThread( function(){", $nl,
    "  ", "bsync( {waitFor:", local:Event($untilEvent),
    ", block:", local:Event($toBlock) ,"} );",  $nl,
    "});"
  ),"")
};

declare function local:messageCAB($event as xs:string?, $content as xs:string?)
as xs:string? {
  string-join((
    "bpj.registerBThread( function(){", $nl,
    "  ", "bsync( {request:bpjs.Event('", $event ,"')});",  $nl,
    "});"
  ),"")
};

for $msg at $msgIdx in doc("SimpleLSC.xml")/lsc/message
let $fromLocEvent := concat("enter|", $msg/@from, "@", $msg/@fromloc)
let $toLocEvent := concat("enter|", $msg/@to, "@", $msg/@toloc)
let $msgEvent := concat("messageEvent", "|", $msgIdx)
return string-join((
  local:blockUntilCAB( $msgEvent, $fromLocEvent),
  local:blockUntilCAB( $msgEvent, $toLocEvent),
  local:messageCAB( $msgEvent, $msg/@content ),
), $nl )
