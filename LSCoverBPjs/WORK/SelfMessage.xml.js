// Transpiled 2016-06-27 07:36:23


bpjs.registerBThread('chart:lsc101', function(){
bsync({request:lsc.Start('lsc101')});
bsync({request:lsc.End('lsc101')});
});
bpjs.registerBThread( 'lifeline-A', function(){
  bsync( {waitFor:lsc.Start('lsc101')} );
  for ( var i=1; i<=2; i++) {
    bsync({request:lsc.Enter('A@'+i,'lsc101'), block:[lsc.visibleEvents, lsc.End('lsc101')]});
    bsync({request:lsc.Leave('A@'+i,'lsc101'), block:lsc.End('lsc101')});
  }
});
bpjs.registerBThread( 'lifeline-B', function(){
  bsync( {waitFor:lsc.Start('lsc101')} );
  for ( var i=1; i<=2; i++) {
    bsync({request:lsc.Enter('B@'+i,'lsc101'), block:[lsc.visibleEvents, lsc.End('lsc101')]});
    bsync({request:lsc.Leave('B@'+i,'lsc101'), block:lsc.End('lsc101')});
  }
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('A@1','lsc101'), block:lsc.Enabled(lsc.Message('A@1','B@1','hello'))} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('B@1','lsc101'), block:lsc.Enabled(lsc.Message('A@1','B@1','hello'))} );
});
bpjs.registerBThread( 'msg-A@1->B@1', function(){
  bsync( {request:lsc.Enabled(lsc.Message('A@1','B@1','hello')), block:lsc.Message('A@1','B@1','hello')} );
  bsync( {request:lsc.Message('A@1','B@1','hello')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('A@1','B@1','hello'), block:lsc.Leave('A@1','lsc101')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('A@1','B@1','hello'), block:lsc.Leave('B@1','lsc101')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('B@2','lsc101'), block:lsc.Enabled(lsc.Message('B@2','A@2','world'))} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('A@2','lsc101'), block:lsc.Enabled(lsc.Message('B@2','A@2','world'))} );
});
bpjs.registerBThread( 'msg-B@2->A@2', function(){
  bsync( {request:lsc.Enabled(lsc.Message('B@2','A@2','world')), block:lsc.Message('B@2','A@2','world')} );
  bsync( {request:lsc.Message('B@2','A@2','world')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('B@2','A@2','world'), block:lsc.Leave('B@2','lsc101')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('B@2','A@2','world'), block:lsc.Leave('A@2','lsc101')} );
});
