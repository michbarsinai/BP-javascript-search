
declare function local:ll( $aLl as node() ) as xs:string {
  concat( "lifeline ", $aLl/@name )
};

declare function local:msg( $aMsg as node() ) as xs:string {
  concat( "message ", $aMsg/@content )
};

declare function local:render-childs( $lsc as node() ) as xs:string* {
  for $nd in $lsc/node()
  let $v := local:dispatch($nd)
  return $v
};

declare function local:lsc( $anLsc as node() ) as xs:string {
  string-join((
    concat( "start-chart ", $anLsc/@id ),
    local:render-childs($anLsc),
    concat( "end-chart ", $anLsc/@id )
  ), $nl)
};


declare function local:dispatch( $nd as node() ) as xs:string* {
  typeswitch( $nd )
    case element(lifeline) return local:ll($nd)
    case element(message)  return local:msg($nd)
    case element(lsc)      return local:lsc($nd)
    default return "ERROR: NO DISPATCH DESTINATION FOUND"
};

local:dispatch(doc($INPUT_FILE)/lsc)
