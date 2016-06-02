(:# import lsc-cabs.xql :)

(: Generate the JS for the passed message XML node. :)
declare function local:message( $msg as node() ) as xs:string {
  let $fromLoc := lsc:loc($msg/@from, $msg/@fromloc)
  let $toLoc := lsc:loc($msg/@to, $msg/@toloc)
  let $content := $msg/@content
  let $msgEnabled := lsc:Enabled(lsc:Message($fromLoc, $toLoc, $content))
  return string-join((
    lsc:blockUntilCAB( $msgEnabled, lsc:Enter($fromLoc) ),
    lsc:blockUntilCAB( $msgEnabled, lsc:Enter($toLoc) ),
    lsc:messageCAB( $fromLoc, $toLoc, $content )
  ), $nl )
};

declare function local:lifeline( $ll as node() ) as xs:string {
  let $name := data($ll/@name)
  let $chartId := data($ll/../@id)
  let $count := xs:integer($ll/@location-count)
  return lsc:lifelineCAB($chartId, $name, $count)
};

declare function local:render-childs( $lsc as node() ) as xs:string* {
  for $nd in $lsc/node()
  let $v := local:dispatch($nd)
  return $v
};

declare function local:lsc( $lsc as node() ) as xs:string {
  string-join((
    concat("lsc.startChart('",$lsc/@id,"');"),
    lsc:chartCAB($lsc/@id),
    local:render-childs($lsc),
    "lsc.endChart();"
  ), $nl)
};

declare function local:dispatch( $nd as node() ) as xs:string {
  typeswitch( $nd )
    case element(lifeline) return local:lifeline($nd)
    case element(message)  return local:message($nd)
    case element(lsc)      return local:lsc($nd)
    default return concat( "ERROR: NO DISPATCH DESTINATION FOUND for ", $nd)
};

local:dispatch(doc($INPUT_FILE)/lsc)
