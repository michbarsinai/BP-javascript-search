(:# import lsc-cabs.xql :)

(:
This file contains the high-level functions that resemble the pseudo code in the
paper.

Functions in this file process XML nodes and call lower-level functions that
create the CABs, which create the actual BP/Javascript code.
:)


(: Generate the JS for the passed message XML node. :)
(: TODO need to use lifeline names, not locations. Enabled events should have chart ids. :)
declare function local:message( $msg as node() ) as xs:string {
  let $fromLoc := lsc:loc($msg/@from, $msg/@fromloc)
  let $toLoc := lsc:loc($msg/@to, $msg/@toloc)
  let $content := $msg/@content
  let $msgEvent := lsc:Message($fromLoc, $toLoc, $content)
  let $msgEnabled := lsc:Enabled($msgEvent)
  let $chartId := $msg/../@id
  return string-join((
    lsc:blockUntilCAB( $msgEnabled, lsc:Enter($fromLoc, $chartId) ),
    lsc:blockUntilCAB( $msgEnabled, lsc:Enter($toLoc, $chartId) ),
    lsc:messageCAB( $fromLoc, $toLoc, $content ),
    lsc:blockUntilCAB( lsc:Leave($fromLoc, $chartId), $msgEvent ),
    lsc:blockUntilCAB( lsc:Leave($toLoc, $chartId), $msgEvent )
  ), $nl )
};

declare function local:sync( $sync as node() ) as xs:string {
  let $syncEvent := lsc:Sync( $sync/@locations, $sync/../@id )
  let $syncBlockers := for $loc in tokenize($sync/@locations,",")
      return lsc:blockUntilCAB( lsc:Enabled($syncEvent), lsc:Enter(lsc:q($loc), $sync/../@id) )
  let $leaveBlockers := for $loc in tokenize($sync/@locations,",")
      return lsc:blockUntilCAB( lsc:Leave(lsc:q($loc), $sync/../@id), $syncEvent )
  return string-join((
    lsc:syncCAB($sync/@locations, $sync/../@id),
    $syncBlockers,
    $leaveBlockers
  ),$nl)
};

declare function local:lifeline( $ll as node() ) as xs:string {
  lsc:lifelineCAB( data($ll/@name),
                   data($ll/../@id),
                   xs:integer($ll/@location-count) )
};

declare function local:render-childs( $lsc as element() ) as xs:string* {
  for $nd in $lsc/node() return local:dispatch($nd)
};

declare function local:loop( $loop as item() ) as xs:string {
  let $loopStartEvent := lsc:Start( $loop/@id )
  let $loopBlockers := for $loc in tokenize($loop/@locations,",")
      return lsc:blockUntilCAB( lsc:Enabled($loopStartEvent), lsc:Enter(lsc:q($loc), $loop/../@id) )
  return string-join((
    $loopBlockers,
    lsc:loopCAB(data($loop/@id), $loop/@control, $loop)
  ), $nl)
};

declare function local:lsc( $lsc as item() ) as xs:string {
  string-join((
    lsc:chartCAB($lsc/@id),
    local:render-childs($lsc)
  ), $nl)
};

(:
Generate the Javascript code containing the BThreads for the passed node.
:)
declare function local:dispatch( $nd as element() ) as xs:string {
  typeswitch( $nd )
    case element(lifeline) return local:lifeline($nd)
    case element(message)  return local:message($nd)
    case element(sync)     return local:sync($nd)
    (: sync, loop, subchart ....:)
    case element(loop)     return local:loop($nd)
    case element(lsc)      return local:lsc($nd)
    default return concat( "ERROR: NO DISPATCH DESTINATION FOUND for ", $nd)
};

local:dispatch(doc($INPUT_FILE)//lsc)
