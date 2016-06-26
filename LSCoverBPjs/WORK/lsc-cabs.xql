(:# import lsc-atoms.xql :)

(: blockUntilCAB from  the paper. :)
declare function lsc:blockUntilCAB($toBlock as xs:string?, $untilEvent as xs:string?) as xs:string? {
  concat(
    "bpjs.registerBThread( 'block-until', function(){", $nl,
    "  bsync( {waitFor:", $untilEvent,
    ", block:", $toBlock ,"} );",  $nl,
    "});"
  )
};

(: later - this is the ceMesageCAB. later we'll need to add the others :)
declare function lsc:messageCAB($fromLoc as xs:string?, $toLoc as xs:string, $content as xs:string) as xs:string? {
  let $messageEvent := lsc:Message($fromLoc, $toLoc, $content)
  let $btName := concat("msg-", fn:substring-before(fn:substring-after($fromLoc,"'"),"'"),
                          "->", fn:substring-before(fn:substring-after($toLoc,"'"),"'") )
  return concat(
    "bpjs.registerBThread( '", $btName, "', function(){", $nl,
    "  bsync( {request:", lsc:Enabled($messageEvent),
    ", block:[", string-join((
        $messageEvent, lsc:Leave($fromLoc), lsc:Leave($toLoc)),", ")
    ,"]} );",  $nl,
    "  bsync( {request:", $messageEvent,
    ", block:[", string-join((lsc:Leave($fromLoc), lsc:Leave($toLoc)),", ")
    ,"]} );",  $nl,
    "});"
  )
};

declare function lsc:lifelineCAB( $name as xs:string?, $chartId as xs:string?, $locationCount as xs:integer ) as xs:string? {
  string-join((
    concat("bpjs.registerBThread( 'lifeline-", $name,"', function(){"),
    concat("  bsync( {waitFor:", lsc:Start($chartId),"} );"),
    concat("  for ( var i=1; i<=", $locationCount, "; i++) {"),
    (: Dealing with waiting for sub-charts goes here. :)
    concat("    bsync({request:", lsc:Enter(lsc:loc-js($name,"i")), ", block:[lsc.VISIBLE_EVENTS, ", lsc:End($chartId), "]});"),
    concat("    bsync({request:", lsc:Leave(lsc:loc-js($name,"i")), ", block:", lsc:End($chartId), "});"),
    "  }",
    "});"), $nl
  )
};

declare function lsc:chartCAB( $chartId as xs:string ) as xs:string {
  string-join((
    concat("bpjs.registerBThread('chart:", $chartId,"', function(){"),
    concat("bsync({request:", lsc:Start($chartId), "});"),
    concat("bsync({request:", lsc:End($chartId), "});"),
    "});"),$nl)
};
