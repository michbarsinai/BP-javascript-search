# BP-javascript
Implementation of BP in JS on top of Java using Rhino.


## Updates

### 2016-09-13
#### Client code / Javascript
* Updated the logging mechanism from global, single level to 3-level. Code change required: `bplog("hello") -> bp.log.info("hello)`. Also supports `warn` and `fine`.
* `bpjs` is deprecated (but still works). Please use `bp` now.
* positional bsync deprecated (but still works). Please use the named-argument variant `bsync({request:....})`.
* BThread is not exposed in Javascript via `bt` (that was never used).
* BThreads can now enqueue external events using `bp.enqueueExternalEvent()`.
* BThreads can now specify a function that will be executed if they are removed because an event in their `breakUpon` event set was selected. Use `setBreakUponHandler( function(event){...} )`.

#### Engine/General
* Restructured the engine with JS proxies - javascript code has no direct interaction with the Java engine parts!
* More unit tests and examples

