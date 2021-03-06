(: Commonly used code generators :)
declare namespace lsc  = "/bp/js/lsc";

declare variable $INPUT_FILE := "__INPUT_FILE__"; (: Replaced by pre-processor to real file name :)

declare variable $nl as xs:string := "&#10;"; (: newline character :)

(: ***************** :
 : Constant Names    :
 : ***************** :)

(:: Statically generate a location id from a lifeline name and the location index. :)
declare function lsc:loc( $lifelineName as xs:string?, $idxName as xs:string? ) as xs:string? {
  concat( "'", $lifelineName, "@", $idxName, "'" )
};

(:: Dinamically generate a location id from a lifeline name and the location variable name. :)
declare function lsc:loc-js( $lifelineName as xs:string?, $varName as xs:string? ) as xs:string? {
  concat( "'", $lifelineName, "@'+", $varName )
};

(: ***************** :
 : Events            :
 : ***************** :)
declare function lsc:Message( $fromLoc as xs:string?, $toLoc as xs:string?, $content as xs:string? ) as xs:string? {
  concat( "lsc.Message(", $fromLoc, ",", $toLoc, ",'", $content, "')" )
};
declare function lsc:Sync( $locations as xs:string?, $chartId as xs:string? ) as xs:string? {
  concat( "lsc.Sync('", $locations, "','", $chartId, "')" )
};
declare function lsc:Enter( $locationName as xs:string, $chartId as xs:string ) as xs:string? {
  concat( "lsc.Enter(", $locationName, ",'", $chartId, "')" )
};
declare function lsc:Leave( $locationName as xs:string, $chartId as xs:string  ) as xs:string? {
  concat( "lsc.Leave(", $locationName, ",'", $chartId, "')" )
};
declare function lsc:Enabled( $eventString as xs:string ) as xs:string {
  concat( "lsc.Enabled(", $eventString, ")" )
};
declare function lsc:Start( $chartId as xs:string ) as xs:string {
  concat( "lsc.Start('", $chartId, "')" )
};
declare function lsc:End( $chartId as xs:string ) as xs:string {
  concat( "lsc.End('", $chartId, "')" )
};
declare function lsc:Done( $chartId as xs:string ) as xs:string {
  concat( "lsc.Done('", $chartId, "')" )
};

(: ***************** :
 : Utilities         :
 : ***************** :)

(: Put the passed value in Javascript qoutes. :)
declare function lsc:q( $v as xs:string ) as xs:string {
  concat("'", $v, "'")
};

(: Build the dictionary of subchart bottoms for lifelineCAB :)
declare function lsc:subchartBottomDictionary( $scBottoms as node()* ) as xs:string {
  string-join((
    "var scb={};",
    for $b in $scBottoms return concat("scb[", $b/@loc ,"]=", lsc:q($b/@subchart-id), ";")
  ),$nl)
};

(: return a path of ids, ending at the passed node. :)
declare function lsc:chartId( $nd as node()? ) as xs:string {
  let $prefix := if (boolean($nd/..)) then lsc:chartId($nd/..) else ""
  return string-join(($prefix, $nd/@id), "/")
};
