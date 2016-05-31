// Added
lsc.startChart("lsc101");
// /Added

bpjs.registerBThread( function(){
  bsync( {waitFor:lsc.Enter('a@1'), block:lsc.Enabled(lsc.Message('a@1','b@1','ping!'))} );
});
bpjs.registerBThread( function(){
  bsync( {waitFor:lsc.Enter('b@1'), block:lsc.Enabled(lsc.Message('a@1','b@1','ping!'))} );
});
// messageCAB
bpjs.registerBThread( function(){
  bsync( {request:lsc.Enabled(lsc.Message('a@1','b@1','ping!')), block:[lsc.Message('a@1','b@1','ping!'), lsc.Leave('a@1'), lsc.Leave('b@1')]} );
  bsync( {request:lsc.Message('a@1','b@1','ping!'), block:[lsc.Leave('a@1'), lsc.Leave('b@1')]} );
});// /messageCAB

bpjs.registerBThread( function(){
  bsync( {waitFor:lsc.Enter('b@2'), block:lsc.Enabled(lsc.Message('b@2','a@2','pong!'))} );
});
bpjs.registerBThread( function(){
  bsync( {waitFor:lsc.Enter('a@2'), block:lsc.Enabled(lsc.Message('b@2','a@2','pong!'))} );
});
// messageCAB
bpjs.registerBThread( function(){
  bsync( {request:lsc.Enabled(lsc.Message('b@2','a@2','pong!')), block:[lsc.Message('b@2','a@2','pong!'), lsc.Leave('b@2'), lsc.Leave('a@2')]} );
  bsync( {request:lsc.Message('b@2','a@2','pong!'), block:[lsc.Leave('b@2'), lsc.Leave('a@2')]} );
});// /messageCAB

bpjs.registerBThread( function(){
  bsync( {waitFor:lsc.Enter('c@2'), block:lsc.Enabled(lsc.Message('c@2','a@4','done'))} );
});
bpjs.registerBThread( function(){
  bsync( {waitFor:lsc.Enter('a@4'), block:lsc.Enabled(lsc.Message('c@2','a@4','done'))} );
});
// messageCAB
bpjs.registerBThread( function(){
  bsync( {request:lsc.Enabled(lsc.Message('c@2','a@4','done')), block:[lsc.Message('c@2','a@4','done'), lsc.Leave('c@2'), lsc.Leave('a@4')]} );
  bsync( {request:lsc.Message('c@2','a@4','done'), block:[lsc.Leave('c@2'), lsc.Leave('a@4')]} );
});// /messageCAB

bpjs.registerBThread( function(){
  bSync( {waitFor:lsc.Start('lsc101')} );
  for ( var i=1; i<=4; i++) {
    bsync({request:lsc.Enter('a@'+i), block:[lsc.VISIBLE_EVENTS, lsc.End('lsc101')]});
    bsync({request:lsc.Leave('a@'+i), block:lsc.End('lsc101')});
  }
});
bpjs.registerBThread( function(){
  bSync( {waitFor:lsc.Start('lsc101')} );
  for ( var i=1; i<=3; i++) {
    bsync({request:lsc.Enter('b@'+i), block:[lsc.VISIBLE_EVENTS, lsc.End('lsc101')]});
    bsync({request:lsc.Leave('b@'+i), block:lsc.End('lsc101')});
  }
});
bpjs.registerBThread( function(){
  bSync( {waitFor:lsc.Start('lsc101')} );
  for ( var i=1; i<=2; i++) {
    bsync({request:lsc.Enter('c@'+i), block:[lsc.VISIBLE_EVENTS, lsc.End('lsc101')]});
    bsync({request:lsc.Leave('c@'+i), block:lsc.End('lsc101')});
  }
});

// Added
lsc.endChart();

bpjs.registerBThread( function(){
  bSync( {request:lsc.Start('lsc101')} );
  bSync( {request:lsc.End('lsc101')} );
});
// /Added
