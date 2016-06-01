(: Including file lsc-bpj.xql :)

(: later - this is the ceMesageCAB. later we'll need to add the others :)
declare function local:messageCAB($fromLoc as xs:string?, $toLoc as xs:string, $content as xs:string)
as xs:string? {
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

for $msg at $msgIdx in doc($INPUT_FILE)/lsc/message
let $fromLoc := lsc:loc($msg/@from, $msg/@fromloc)
let $toLoc := lsc:loc($msg/@to, $msg/@toloc)
let $content := $msg/@content
let $msgEnabled := lsc:Enabled(lsc:Message($fromLoc, $toLoc, $content))
return string-join((
  lsc:blockUntilCAB( $msgEnabled, lsc:Enter($fromLoc) ),
  lsc:blockUntilCAB( $msgEnabled, lsc:Enter($toLoc) ),
  local:messageCAB( $fromLoc, $toLoc, $content )
), $nl )
