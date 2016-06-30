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
              ", block:", $messageEvent,"} );",  $nl,
    "  bsync( {request:", $messageEvent,"} );",  $nl,
    "});"
  )
};

declare function lsc:syncCAB($locations as xs:string, $chartId as xs:string) as xs:string {
  let $syncEvent := lsc:Sync($locations, $chartId)
  let $btName := concat("sync<", $locations , ">")
  return concat(
    "bpjs.registerBThread( '", $btName, "', function(){", $nl,
    "  bsync({request:", lsc:Enabled($syncEvent), ", block:", $syncEvent, "});", $nl,
    "  bsync({request:", $syncEvent, "});", $nl,
    "}); "
  )
};

declare function lsc:lifelineCAB( $name as xs:string?, $chartId as xs:string?, $locationCount as xs:integer, $subchart-bottoms as node()* ) as xs:string? {
  string-join((
    concat("bpjs.registerBThread( 'lifeline-", $name,"', function(){"),
    lsc:subchartBottomDictionary($subchart-bottoms),
    concat("  bsync( {waitFor:", lsc:Start($chartId),"} );"),
    concat("  for ( var i=1; i<=", $locationCount, "; i++) {"),
           "    if (typeof scb[i] !== 'undefined'){",
    concat("       bsync({waitFor:lsc.Done('", $chartId ,"/'+scb[i]), block:", lsc:End($chartId), "});"),
           "}",
    concat("    bsync({request:", lsc:Enter(lsc:loc-js($name,"i"),$chartId), ", block:[lsc.visibleEvents, ", lsc:End($chartId), "]});"),
    concat("    bsync({request:", lsc:Leave(lsc:loc-js($name,"i"),$chartId), ", block:", lsc:End($chartId), "});"),
    "  }",
    "});"), $nl
  )
};

declare function lsc:chartCAB( $chartId as xs:string ) as xs:string {
  string-join((
    concat("bpjs.registerBThread('chart:", $chartId,"', function(){"),
    concat("  bsync({request:", lsc:Start($chartId), "});"),
    concat("  bsync({request:", lsc:End($chartId), "});"),
           "});"),$nl)
};

declare function lsc:loopCAB( $loopId as xs:string, $ctrl as xs:string, $loop as node() ) as xs:string {
  string-join((
      concat("bpjs.registerBThread('loop:", $loopId, "', function(){"),
      concat("  bsync({request:" , lsc:Enabled(lsc:Start($loopId)), "});"),
      (: concat("  bsync({request:" , lsc:Enabled(lsc:Start($loopId)),
               ", block:lsc.leaveEvents(", lsc:q($loop/@locations), ", ", lsc:q(lsc:chartId($loop/..)), ")});"), :)
      concat("  for (var loopCtrl=0; loopCtrl<", $ctrl, "; loopCtrl++) {"),
      local:render-childs($loop),
      concat("    bsync({request:lsc.Start(", lsc:q(lsc:chartId($loop)), "), block:lsc.visibleEvents});"),
      concat("    bsync({request:", lsc:End(lsc:chartId($loop)), ", block:lsc.visibleEvents});"),
             "  }",
      concat("  bsync({request:", lsc:Done(lsc:chartId($loop)), ", block:lsc.visibleEvents});"),
             "});"
  ), $nl)
};
