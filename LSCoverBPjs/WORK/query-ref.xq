(: for $msg in doc("SimpleLSC.xml")//message
return $msg :)

(: These two are generalu the same, excpet for the sorting. :)
(: doc("books.xml")/bookstore/book[price>30] :)
(: for $book in doc("books.xml")/bookstore/book
where $book/price>30
order by $book/title
return $book/title :)

(: <ul>
{
  for $book in doc("books.xml")/bookstore/book
  where $book/price>30
  order by $book/title
  return <li>{data($book/title)}</li>
}
</ul> :)

declare variable $nl as xs:string := "&#10;";

declare function local:testFunc($p as xs:string ?)
as xs:string? {
  let $g := $p
  return fn:substring($g,5)
};

declare function local:streamAtt($att as xs:string ) as xs:string {
  string-join( tokenize($att, ","), $nl)
};

declare function local:location($loc as xs:string?) as xs:string {
  concat("* Location: ", $loc, ".")
};

(:<ul>
{
  for $book at $num in doc("books.xml")/bookstore/book
  let $t := (1 to 5)
  let $title := data($book/title)
  let $testFuncVal := local:testFunc($title)
  order by $book/title
  return if ( $book/@category="WEB" )
  then <ans>
          <li class="web">{$num}) {data($title)}</li>
          {$testFuncVal}
       </ans>
  else <li class="{data($book/@category)}">{$num}) {data($title)}<div>{$t}</div></li>
}
</ul>
:)
(:
local:location(doc("books.xml")//bookstore/@locations)
:)
for $l in tokenize(doc("books.xml")//bookstore/@locations, ",")
return local:location($l)