// Transpiled 2016-06-29 11:13:54


bpjs.registerBThread('chart:lsc101', function(){
  bsync({request:lsc.Start('lsc101')});
  bsync({request:lsc.End('lsc101')});
});
var scb={};
bpjs.registerBThread( 'lifeline-a', function(){
  bsync( {waitFor:lsc.Start('lsc101')} );
  for ( var i=1; i<=2; i++) {
    if (typeof scb[i] !== 'undefined'){
       bsync({waitFor:lsc.End(scb[i]), block:lsc.End('lsc101')});
}
    bsync({request:lsc.Enter('a@'+i,'lsc101'), block:[lsc.visibleEvents, lsc.End('lsc101')]});
    bsync({request:lsc.Leave('a@'+i,'lsc101'), block:lsc.End('lsc101')});
  }
});
var scb={};
bpjs.registerBThread( 'lifeline-b', function(){
  bsync( {waitFor:lsc.Start('lsc101')} );
  for ( var i=1; i<=2; i++) {
    if (typeof scb[i] !== 'undefined'){
       bsync({waitFor:lsc.End(scb[i]), block:lsc.End('lsc101')});
}
    bsync({request:lsc.Enter('b@'+i,'lsc101'), block:[lsc.visibleEvents, lsc.End('lsc101')]});
    bsync({request:lsc.Leave('b@'+i,'lsc101'), block:lsc.End('lsc101')});
  }
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('a@1','lsc101'), block:lsc.Enabled(lsc.Message('a@1','a@1','ping'))} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('a@1','lsc101'), block:lsc.Enabled(lsc.Message('a@1','a@1','ping'))} );
});
bpjs.registerBThread( 'msg-a@1->a@1', function(){
  bsync( {request:lsc.Enabled(lsc.Message('a@1','a@1','ping')), block:lsc.Message('a@1','a@1','ping')} );
  bsync( {request:lsc.Message('a@1','a@1','ping')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('a@1','a@1','ping'), block:lsc.Leave('a@1','lsc101')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('a@1','a@1','ping'), block:lsc.Leave('a@1','lsc101')} );
});
bpjs.registerBThread( 'sync<a@2,b@1>', function(){
  bsync({request:lsc.Enabled(lsc.Sync('a@2,b@1','lsc101')), block:lsc.Sync('a@2,b@1','lsc101')});
  bsync({request:lsc.Sync('a@2,b@1','lsc101')});
}); 
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('a@2','lsc101'), block:lsc.Enabled(lsc.Sync('a@2,b@1','lsc101'))} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('b@1','lsc101'), block:lsc.Enabled(lsc.Sync('a@2,b@1','lsc101'))} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Sync('a@2,b@1','lsc101'), block:lsc.Leave('a@2','lsc101')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Sync('a@2,b@1','lsc101'), block:lsc.Leave('b@1','lsc101')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('b@2','lsc101'), block:lsc.Enabled(lsc.Message('b@2','b@2','pong'))} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('b@2','lsc101'), block:lsc.Enabled(lsc.Message('b@2','b@2','pong'))} );
});
bpjs.registerBThread( 'msg-b@2->b@2', function(){
  bsync( {request:lsc.Enabled(lsc.Message('b@2','b@2','pong')), block:lsc.Message('b@2','b@2','pong')} );
  bsync( {request:lsc.Message('b@2','b@2','pong')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('b@2','b@2','pong'), block:lsc.Leave('b@2','lsc101')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('b@2','b@2','pong'), block:lsc.Leave('b@2','lsc101')} );
});
