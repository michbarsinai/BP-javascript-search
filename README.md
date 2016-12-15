# BPjs: A Javascript-based Behavioral Programming Library

This repository contains a javascript-based [BP](www.b-prog.org) library. It uses the [Rhino](https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino) engine, as some of its features require continuations and Rhino seems to be the only one to support it as this point.

**Current version lives in [BP-javascript](BP-javascript).** The other BP libraries will later be merged or updated.

This library was originally created my @moshewe, but was refactored extensively by @michbarsinai, who currently maintains it.

## Change log for the BPjs library.

### 2016-12-16
* :sparkles: A class for running bpjs files.
* :thumbsup: Efficient use of `Context`, so that we don't open and exit it all the time.
* :put_litter_in_its_place: More removal of more unused code. We now have less unused code. Less is more, especially with unused code.

### 2016-12-11
* :arrows_counterclockwise: `breakUpon` is now `interrupt`. This leaves `breakUpon` to be later used as a language construct rather than a `bsync` parameter.

### 2016-12-05
* :put_litter_in_its_place: :sparkles: :elephant: Big refactoring and clean-up towards enabling search. `BThread`s removed from engine - main new concept is that an execution of a BThread is a series of `BThreadSyncSnapshot`, advanced/connected by `BPEngineTask`s. A BProgram is an execution environment for multiple BThreads (plus some state and management code).

### 2016-09-18
* :bug: `breakUpon` handlers are evaluated in the `BThread`'s context, rather than in the `BProgram` one.

### 2016-09-13
#### Client code / Javascript
* :sparkles: Updated the logging mechanism from global, single level to 3-level. Code change required: `bplog("hello") -> bp.log.info("hello)`. Also supports `warn` and `fine`.
* :put_litter_in_its_place: `bpjs` is deprecated (but still works). Please use `bp` now.
* :put_litter_in_its_place: positional bsync deprecated (but still works). Please use the named-argument variant `bsync({request:....})`.
* :put_litter_in_its_place: BThread is not exposed in Javascript via `bt` (that was never used).
* :sparkles: BThreads can now enqueue external events using `bp.enqueueExternalEvent()`.
* :sparkles: BThreads can now specify a function that will be executed if they are removed because an event in their `breakUpon` event set was selected. Use `setBreakUponHandler( function(event){...} )`.

#### Engine/General
* :sparkles: Restructured the engine with JS proxies - javascript code has no direct interaction with the Java engine parts!
* :thumbsup: More unit tests and examples



### 2016-06-11
* :sparkles: BEvents now have an associated `data` object. See example [here](BP-javascript/test/bp/examples/eventswithdata/EventsWithData.js)

* :sparkles: New way of creating `BEvent`s: a static method named `named`:

  ````java
  new BEvent("1stEvent") // old
  BEvent.named("1stEvent") // new and English-like.
  ````

  Future usaged include the ability to reuse event instances, but we're not there yet.

* :sparkles: Added support for Javascript definition of event sets:

  ````javascript
  var sampleSet = bpjs.EventSet( function(e){
    return e.getName().startsWith("1st");
  } );
  ````

### 2016-06-10
* :sparkles: Support for `breakUpon` in `bsync`s:

  ````javascript
  bsync( {request:A, waitFor:B, block:C, breakUpon:D})
  ````
* :sparkles: `SingleResourceBProgram` - a convenience class for the common case of having a BProgram that consists of a
    single file.

### 2016-06-01
* :arrows_counterclockwise: BProgram's `setupProgramScope` gets a scope as parameter. So no need to call `getGlobalScope`, and it's clearer what to do.
* :sparkles: `RWBStatement` now knows which BThread instantiated it
* :sparkles: When a program deadlock, `StreamLoggerListener` would print the `RWBStatement`s of all `BThreads`.



Legend:
* :arrows_counterclockwise: Change
* :sparkles:New feature
* :put_litter_in_its_place: Deprecation
