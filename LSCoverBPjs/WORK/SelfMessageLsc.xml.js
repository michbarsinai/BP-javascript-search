// Transpiled 2016-06-27 07:44:40


bpjs.registerBThread('chart:lsc101', function(){
bsync({request:lsc.Start('lsc101')});
bsync({request:lsc.End('lsc101')});
});
bpjs.registerBThread( 'lifeline-A', function(){
  bsync( {waitFor:lsc.Start('lsc101')} );
  for ( var i=1; i<=1; i++) {
    bsync({request:lsc.Enter('A@'+i,'lsc101'), block:[lsc.visibleEvents, lsc.End('lsc101')]});
    bsync({request:lsc.Leave('A@'+i,'lsc101'), block:lsc.End('lsc101')});
  }
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('A@1','lsc101'), block:lsc.Enabled(lsc.Message('A@1','A@1','hello'))} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('A@1','lsc101'), block:lsc.Enabled(lsc.Message('A@1','A@1','hello'))} );
});
bpjs.registerBThread( 'msg-A@1->A@1', function(){
  bsync( {request:lsc.Enabled(lsc.Message('A@1','A@1','hello')), block:lsc.Message('A@1','A@1','hello')} );
  bsync( {request:lsc.Message('A@1','A@1','hello')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('A@1','A@1','hello'), block:lsc.Leave('A@1','lsc101')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('A@1','A@1','hello'), block:lsc.Leave('A@1','lsc101')} );
});
