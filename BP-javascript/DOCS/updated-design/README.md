# Updated Design

The main reason for updating the design is to allow both the search and the normal executions use the same core classes. This design is based around the concept of `BThreadState`s and the transitions they go through. The idea is that the same
state can be advanced to multiple states using multiple transitions, each parametrized with a different event. This way,
normal execution is basically just another non-exhaustive search.

## Mathematical Concept
```
BTInitialState --start-> BTAtSyncState --continue(event)-> BTAtSyncState...
```
