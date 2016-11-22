// Transpiled 2016-06-30 10:06:02


bpjs.registerBThread('chart:/lscWithLoop', function(){
  bsync({request:lsc.Start('/lscWithLoop')});
  bsync({request:lsc.End('/lscWithLoop')});
});
bpjs.registerBThread( 'lifeline-A', function(){
var scb={};
scb[3]='theLoop';
  bsync( {waitFor:lsc.Start('/lscWithLoop')} );
  for ( var i=1; i<=3; i++) {
    if (typeof scb[i] !== 'undefined'){
       bsync({waitFor:lsc.Done('/lscWithLoop/'+scb[i]), block:lsc.End('/lscWithLoop')});
}
    bsync({request:lsc.Enter('A@'+i,'/lscWithLoop'), block:[lsc.visibleEvents, lsc.End('/lscWithLoop')]});
    bsync({request:lsc.Leave('A@'+i,'/lscWithLoop'), block:lsc.End('/lscWithLoop')});
  }
});
bpjs.registerBThread( 'lifeline-B', function(){
var scb={};
scb[3]='theLoop';
  bsync( {waitFor:lsc.Start('/lscWithLoop')} );
  for ( var i=1; i<=3; i++) {
    if (typeof scb[i] !== 'undefined'){
       bsync({waitFor:lsc.Done('/lscWithLoop/'+scb[i]), block:lsc.End('/lscWithLoop')});
}
    bsync({request:lsc.Enter('B@'+i,'/lscWithLoop'), block:[lsc.visibleEvents, lsc.End('/lscWithLoop')]});
    bsync({request:lsc.Leave('B@'+i,'/lscWithLoop'), block:lsc.End('/lscWithLoop')});
  }
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('A@1','/lscWithLoop'), block:lsc.Enabled(lsc.Message('A@1','B@1','hello'))} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('B@1','/lscWithLoop'), block:lsc.Enabled(lsc.Message('A@1','B@1','hello'))} );
});
bpjs.registerBThread( 'msg-A@1->B@1', function(){
  bsync( {request:lsc.Enabled(lsc.Message('A@1','B@1','hello')), block:lsc.Message('A@1','B@1','hello')} );
  bsync( {request:lsc.Message('A@1','B@1','hello')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('A@1','B@1','hello'), block:lsc.Leave('A@1','/lscWithLoop')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('A@1','B@1','hello'), block:lsc.Leave('B@1','/lscWithLoop')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Leave('A@2','/lscWithLoop'), block:lsc.Enabled(lsc.Start('/lscWithLoop/theLoop'))} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Leave('B@2','/lscWithLoop'), block:lsc.Enabled(lsc.Start('/lscWithLoop/theLoop'))} );
});
bpjs.registerBThread('loop:/lscWithLoop/theLoop', function(){
  bsync({request:lsc.Enabled(lsc.Start('/lscWithLoop/theLoop'))});
  for (var loopCtrl=0; loopCtrl<3; loopCtrl++) {
bpjs.registerBThread( 'lifeline-A', function(){
var scb={};
  bsync( {waitFor:lsc.Start('/lscWithLoop/theLoop')} );
  for ( var i=1; i<=1; i++) {
    if (typeof scb[i] !== 'undefined'){
       bsync({waitFor:lsc.Done('/lscWithLoop/theLoop/'+scb[i]), block:lsc.End('/lscWithLoop/theLoop')});
}
    bsync({request:lsc.Enter('A@'+i,'/lscWithLoop/theLoop'), block:[lsc.visibleEvents, lsc.End('/lscWithLoop/theLoop')]});
    bsync({request:lsc.Leave('A@'+i,'/lscWithLoop/theLoop'), block:lsc.End('/lscWithLoop/theLoop')});
  }
});
bpjs.registerBThread( 'lifeline-B', function(){
var scb={};
  bsync( {waitFor:lsc.Start('/lscWithLoop/theLoop')} );
  for ( var i=1; i<=1; i++) {
    if (typeof scb[i] !== 'undefined'){
       bsync({waitFor:lsc.Done('/lscWithLoop/theLoop/'+scb[i]), block:lsc.End('/lscWithLoop/theLoop')});
}
    bsync({request:lsc.Enter('B@'+i,'/lscWithLoop/theLoop'), block:[lsc.visibleEvents, lsc.End('/lscWithLoop/theLoop')]});
    bsync({request:lsc.Leave('B@'+i,'/lscWithLoop/theLoop'), block:lsc.End('/lscWithLoop/theLoop')});
  }
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('B@1','/lscWithLoop/theLoop'), block:lsc.Enabled(lsc.Message('B@1','A@1','world'))} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Enter('A@1','/lscWithLoop/theLoop'), block:lsc.Enabled(lsc.Message('B@1','A@1','world'))} );
});
bpjs.registerBThread( 'msg-B@1->A@1', function(){
  bsync( {request:lsc.Enabled(lsc.Message('B@1','A@1','world')), block:lsc.Message('B@1','A@1','world')} );
  bsync( {request:lsc.Message('B@1','A@1','world')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('B@1','A@1','world'), block:lsc.Leave('B@1','/lscWithLoop/theLoop')} );
});
bpjs.registerBThread( 'block-until', function(){
  bsync( {waitFor:lsc.Message('B@1','A@1','world'), block:lsc.Leave('A@1','/lscWithLoop/theLoop')} );
});
    bsync({request:lsc.Start('/lscWithLoop/theLoop'), block:lsc.visibleEvents});
    bsync({request:lsc.End('/lscWithLoop/theLoop'), block:lsc.visibleEvents});
  }
  bsync({request:lsc.Done('/lscWithLoop/theLoop'), block:lsc.visibleEvents});
});
