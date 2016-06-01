(: This file generates code for lifeline CABs. :)

(: Including file lsc-bpj.xql :)
declare function local:lifelineCAB( $chartId as xs:string?, $name as xs:string?, $locationCount as xs:integer ) as xs:string? {
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

for $ll in doc($INPUT_FILE)/lsc/lifeline
let $name := data($ll/@name)
let $chartId := data($ll/../@id)
let $count := xs:integer($ll/@location-count)
return local:lifelineCAB($chartId, $name, $count)
