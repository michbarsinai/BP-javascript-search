package bp.bprogram;

import bp.events.BEvent;
import bp.tasks.*;

import java.util.*;
import java.util.concurrent.*;

import static bp.BProgramControls.debugMode;
import bp.bprogram.listeners.BProgramListener;
import bp.eventselection.EventSelectionResult;
import bp.eventselection.EventSelectionResult.EmptyResult;
import bp.eventselection.EventSelectionResult.Selected;
import bp.eventselection.EventSelectionStrategy;
import bp.eventselection.SimpleEventSelectionStrategy;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;

/**
 * Base class for BPrograms. Contains the logic for managing {@link BThread}s and 
 * the main event loop. Lacks the technology-specific parts, such as Javascript scope setups.
 * 
 * @author michael
 */
public abstract class BProgram  {
    
    /**
     * "Poison pill" o insert to the external event queue. Used only to turn the daemon mode off.
     */
    private static final BEvent NO_MORE_DAEMON = new BEvent("NO_MORE_DAEMON");
    
    /**
     * A collection containing all the BThreads in the system. A BThread
     * adds itself to the list either - in its constructor and removes itself
     * when its run() function finishes - or a Java thread adds itself and
     * removes itself explicitly
     */
    public Set<BThread> bthreads;
    
    private String name;
    
    /**
     * When {@code true}, the bprogram waits for an external event when no internal ones are available.
     */
    private boolean daemonMode;
    private final ExecutorService executor = new ForkJoinPool();
    private EventSelectionStrategy eventSelectionStrategy;

    /** Events are enqueued here by external threads */
    private final BlockingQueue<BEvent> recentlyEnquqedExternalEvents = new LinkedBlockingQueue<>();
    
    /** At the BProgram's leisure, the external event are moved here, where they can be managed. */
    private final List<BEvent> enqueuedExternalEvents = new LinkedList<>();
    
    /** BThreads added between bsyncs are added here. */
    private final BlockingQueue<BThread> recentlyRegisteredBthreads = new LinkedBlockingDeque<>();
    
    private final List<BProgramListener> listeners = new ArrayList<>();
    
    private volatile boolean started = false;

    public BProgram() {
        this(BProgram.class.getSimpleName());
    }
    
    public BProgram( String aName ) {
        this( aName, new SimpleEventSelectionStrategy()  );
    }
    
    public BProgram( String aName, EventSelectionStrategy anEventSelectionStrategy ) {
        name = aName;
        bthreads = new HashSet<>();
        eventSelectionStrategy = anEventSelectionStrategy;
    }
   
    /**
     * Removes all BThreads that have finished executing.
     */
    public void bthreadCleanup() {
        for (Iterator<BThread> it = bthreads.iterator(); it.hasNext(); ) {
            BThread bt = it.next();
            if (!bt.isAlive()) {
                it.remove();
                listeners.forEach( l -> l.bthreadRemoved(this, bt) );
            }
        }
    }
    
     
    public void start() throws InterruptedException {
        
        listeners.forEach( l -> l.started(this) );
        started = true;
        
        executor.invokeAll( bthreads.stream()
                                .map( bt-> new StartBThread(bt) )
                                .collect(toList()) );
        startRecentlyRegisteredBThreads();
        bthreadCleanup();
        if ( bthreads.isEmpty() ) {
            // super corner case, where no bsyncs were called.
            listeners.forEach( l -> l.ended(this) );
        } else {
            do {
                mainEventLoop();
            } while ( isDaemonMode() && waitForExternalEvent() );
        }
    }
   
    
    final ResultHandler handler = new ResultHandler();
    
    /**
     * Advances the BProgram a single super-step, that is until there are 
     * no more internal events that can be selected.
     * 
     * @throws InterruptedException
     * @return The reason the super-step terminated.
     */
    public EventSelectionResult.EmptyResult mainEventLoop() throws InterruptedException {
        
        handler.endResult = null;
        while ( eventSelectionStrategy.select(createBSyncStatement()).accept(handler) ) {}
        listeners.forEach( l->l.superstepDone(this, handler.endResult) );
        return handler.endResult;
        
    }

    class ResultHandler implements EventSelectionResult.Visitor<Boolean> {
        EmptyResult endResult;
        
        @Override
        public Boolean visit(EventSelectionResult.SelectedExternal se) {
            try {
                enqueuedExternalEvents.remove( se.getIndex() );
                triggerEvent( se.getEvent() );
                bthreadCleanup();
                return true;
            } catch (InterruptedException ex) {
               throw new RuntimeException( ex );
            }
        }

        @Override
        public Boolean visit(Selected se) {
            try {
                triggerEvent( se.getEvent() );
                bthreadCleanup();
                return true;
            } catch (InterruptedException ex) {
               throw new RuntimeException( ex );
            }
        }

        @Override
        public Boolean visit(EventSelectionResult.Deadlock dl) {
            endResult = dl;
            return false;
        }

        @Override
        public Boolean visit(EventSelectionResult.NoneRequested ne) {
            endResult = ne;
            return false;
        }
        
    }
    
    protected BSyncState createBSyncStatement() {
        recentlyEnquqedExternalEvents.drainTo(enqueuedExternalEvents);
        if ( enqueuedExternalEvents.remove( NO_MORE_DAEMON ) ) {
            daemonMode=false;
        }
        return new BSyncState( currentStatements(), Collections.unmodifiableList(enqueuedExternalEvents) );
    }
    
    /**
     * Creates a list of the current {@link RWBStatements} of the current BThreads.
     * Note that the order in the list is arbitrary.
     * @return list of statements in arbitrary order.
     */
    protected List<RWBStatement> currentStatements() { 
        return bthreads.stream()
                .map( BThread::getCurrentRwbStatement )
                .collect( toList() );
    }
   
    private void startRecentlyRegisteredBThreads() throws InterruptedException {
        
        add( recentlyRegisteredBthreads );
        recentlyRegisteredBthreads.forEach( bt -> setupAddedBThread(bt) );
                
        executor.invokeAll( recentlyRegisteredBthreads.stream()
                                .map( bt-> new StartBThread(bt) )
                                .collect(toList()) );
        recentlyRegisteredBthreads.clear();
    }
    
    protected abstract void setupAddedBThread( BThread bt );
    
    /**
     * Awakens BThreads that waited for/requested this event in their last bsync,
     * and waits for them to terminate.
     * 
     * @param anEvent The event to trigger. Cannot be {@code null}.
     * @throws java.lang.InterruptedException
     */
    public void triggerEvent(BEvent anEvent) throws InterruptedException {
        if ( anEvent == null ) {
            throw new IllegalArgumentException("Cannot trigger a null event.");
        }        
        listeners.forEach( l->l.eventSelected(this, anEvent) );
        
        bthreads.forEach( bt -> {if (bt.getCurrentRwbStatement()==null) {
            System.out.println(bt.getName() + " Has null stmt");
        }});
        
        Collection<ResumeBThread> resumes = bthreads.stream()
                .filter( bt->bt.getCurrentRwbStatement().shouldWakeFor(anEvent) )
                .map( bt->new ResumeBThread(bt, anEvent) )
                .collect( toList() );
        executor.invokeAll(resumes);
        startRecentlyRegisteredBThreads();
    }
    
    public void add(Collection<BThread> bts) {
        bts.forEach( bt -> add(bt) );
    }

    public void add(BThread bt) {
        bthreads.add(bt);
        listeners.forEach( l -> l.bthreadAdded(this, bt) );
    }
    
    /**
     * Registers a BThread into the program. If the program started, the BThread will
     * take part in the current bstep.
     * 
     * @param bt 
     */
    public void registerBThread(BThread bt) {
        if (started) {
            bplog("Queued " + bt.getName());
            recentlyRegisteredBthreads.add(bt);
        } else {
            add(bt);
        }
    }
    
    /**
     * a method that sends events as input for the application
     *
     * @param e
     */
    public void enqueueExternalEvent(BEvent e) {
        recentlyEnquqedExternalEvents.add(e);
    }
    
    private boolean waitForExternalEvent() throws InterruptedException {
        final BEvent newEvent = recentlyEnquqedExternalEvents.take();
        if ( newEvent == NO_MORE_DAEMON ) {
            daemonMode = false;
            return false;
        } else {
            enqueuedExternalEvents.add(newEvent);
            return true;
        }
    }
    
    
    public void bplog(String string) {
        if (debugMode)
            System.out.println("[" + this + "]: " + string);
    }

    public Set<BThread> getBThreads() {
        return bthreads;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the given bprogram's _name
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }

    public <R extends BProgramListener> R addListener( R aListener ) {
        listeners.add(aListener);
        return aListener;
    }
    
    public void removeListener( BProgramListener aListener ) {
        listeners.remove(aListener);
    }

    public void setDaemonMode(boolean newDaemonMode) {
        if ( daemonMode && !newDaemonMode ) {
            daemonMode = false;
            recentlyEnquqedExternalEvents.add(NO_MORE_DAEMON);
        } else {
            daemonMode = newDaemonMode;
        }
    }

    public boolean isDaemonMode() {
        return daemonMode;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /// Debugging Stuff.
    
    /**
     * Utility function (for debugging purposes) that prints the ordered list of
     * active be-threads.
     */
    public void printAllBThreads() {
        int c = 0;
        for (BThread bt : getBThreads()) {
            bplog("\t" + (c++) + ":" + bt);
        }
    }
    
}
