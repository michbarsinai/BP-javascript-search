# BPjs: A Javascript-based Behavioral Programming Library

This repository contains a javascript-based [BP](www.b-prog.org) library. It uses the [Rhino](https://developer.mozilla.org/en-US/docs/Mozilla/Projects/Rhino) engine, as some of its features require continuations and Rhino seems to be the only one to support it as this point.

**Current version lives in [BP-javascript](BP-javascript).** The other BP libraries will later be merged or updated.

This library was originally created my @moshewe, but was refactored extensively by @michbarsinai, who currently maintains it.

## Change log for the BPjs library.

### 2016-06-11
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
