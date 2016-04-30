package bp;

import bp.tasks.*;

import java.util.*;
import java.util.concurrent.*;

import static bp.BProgramControls.debugMode;
import bp.eventselection.EventSelectionResult;
import bp.eventselection.EventSelectionResult.Selected;
import bp.eventselection.EventSelectionStrategy;
import bp.eventselection.SimpleEventSelectionStrategy;
import bp.eventsets.ComposableEventSet;
import bp.eventsets.EventSets;
import java.util.stream.Collectors;
import bp.eventsets.Requestable;
import bp.eventsets.EventSet;
import static java.util.stream.Collectors.toList;

public abstract class BPApplication  {

    /**
     * A collection containing all the BThreads in the system. A BThread
     * adds itself to the list either - in its constructor and removes itself
     * when its run() function finishes - or a Java thread adds itself and
     * removes itself explicitly
     */
    public transient Set<BThread> bthreads;
    
    /**
     * Stores the strings of the events that occurred in this run
     * TODO remove, replace with a listener/logger.
     */
    public transient LinkedList<BEvent> eventLog = new LinkedList<>();
    
    /**
     * Program name is set to be the simple class name by default.
     */
    protected transient String name;
    private Arbiter arbiter;
    private BlockingQueue<BEvent> inputEventQueue;
    private BlockingQueue<BEvent> outputEventQueue;
    protected ExecutorService executor;
    
    // TODO probably replace with a full enum of PRE_RUN, IN_STEP, IDLE.
    private volatile boolean started = false;

    public BPApplication() {
        this( BPApplication.class.getSimpleName());
    }
    
    public BPApplication( String aName ) {
        this( aName, new Arbiter() );
    }
    
    public BPApplication( String aName, Arbiter anArbiter ) {
        name = aName;
        bthreads = new HashSet<>();
        arbiter = anArbiter;
        arbiter.setProgram(this);
        inputEventQueue = new LinkedBlockingQueue<>();
        outputEventQueue = new LinkedBlockingQueue<>();
        executor = new ForkJoinPool();
    }

    /**
     * @return an set of all enabled events that are requestable
     */
    public Set<BEvent> requestedAndNotBlockedEvents() {
        Set<Requestable> requested = bthreads.stream().map( BThread::getCurrentRwbStatement )
                .filter( stmt -> stmt!=null )
                .map( RWBStatement::getRequest )
                .filter( r -> r != EventSets.none )
                .collect( Collectors.toSet() );
        
        EventSet blockedSets = ComposableEventSet.anyOf(
                bthreads.stream().map( BThread::getCurrentRwbStatement )
                .filter( stmt -> stmt!=null )
                .map( RWBStatement::getBlock )
                .filter( r -> r != EventSets.none )
                .collect( Collectors.toSet() ) );
        
        Set<Requestable> canRequest = requested.stream().filter( req -> !blockedSets.contains(req) ).collect( Collectors.toSet() );
        List<BEvent> events = new ArrayList<>();
        canRequest.forEach( r -> r.addEventsTo(events) );
        
        return new HashSet<>(events);
    }

    /**
     * A function that checks if an event is blocked by some b-thread.
     *
     * @param e An event.
     * @return true if the event is blocked by some be-thread.
     */
    public boolean isBlocked(BEvent e) {
        return getBThreads().stream()
                .anyMatch( bt -> bt.getCurrentRwbStatement().getBlock().contains(e) );
    }

    /**
     * @return all waited-for events when program is idle
     */
    public Collection<EventSet> getWatchedEventSets() {
        Collection<EventSet> ret = new ArrayList<>();
        for (BThread bt : bthreads) {
            ret.add(bt.getCurrentRwbStatement().getWaitFor());
        }
        return ret;
    }

    /**
     * @return all events that are requested but blocked when program is idle
     */
    public Collection<BEvent> getRequestedBlockedEvents() {
        Collection<BEvent> blocked = new ArrayList<>();
        for (BThread bt : bthreads) {
            Iterator<Requestable> it = bt.getCurrentRwbStatement().getRequest().iterator();
            while (it.hasNext()) {
                Requestable req = it.next();
                if (req.isEvent()) {
                    BEvent e = (BEvent) req;
                    for (BThread other : bthreads) {
                        if (other.getCurrentRwbStatement().getBlock().contains(e)) {
                            blocked.add(e);
                        }
                    }
                }
            }
        }
        return blocked;
    }

    public void bthreadCleanup() {
        for (Iterator<BThread> it = bthreads.iterator(); it.hasNext(); ) {
            BThread bt = it.next();
            if (!bt.isAlive()) {
                bplog("-- Removing Bthread " + bt.getName() );
                it.remove();
            }
        }
    }
    
    /**
     * Advances the BProgram a single super-step, that is until there are 
     * no more internal events that can be selected.
     * 
     * @throws InterruptedException
     * @return The reason the super-step terminated.
     */
    public EventSelectionResult superStep() throws InterruptedException {
        
        EventSelectionStrategy ess = new SimpleEventSelectionStrategy();
        
        executor.invokeAll( bthreads.stream()
                                .map( bt-> new StartBThread(bt) )
                                .collect(toList()) );
        
        bthreadCleanup();
        EventSelectionResult esr = ess.select(currentStatements());    
        while( esr instanceof Selected  ) {
            BEvent selectedEvent = ((Selected)esr).getEvent();
            triggerEvent(selectedEvent);
            bthreadCleanup();
            esr = ess.select(currentStatements());
        }
        return esr;
    }
    
    private Collection<RWBStatement> currentStatements() { 
        return bthreads.stream()
                .map( BThread::getCurrentRwbStatement )
                .collect( toList() );
    }
    
    public void start() throws InterruptedException {
        bplog("********* Starting " + bthreads.size()
                + " scenarios  **************");
        started = true;
        executor.invokeAll( 
            bthreads.stream()
                    .map( bt-> new StartBThread(bt) )
                    .collect(toList()) );
        bplog("********* " + bthreads.size()
                + " scenarios started **************");
        executor.submit(new EventLoopTask(this, arbiter));
    }
    
    /**
     * Awakens BThreads that waited for/requested this event in their last bsync,
     * and waits for them to terminate.
     * 
     * @param anEvent The event to trigger. Cannot be {@code null}.
     */
    public void triggerEvent(BEvent anEvent) throws InterruptedException {
        if ( anEvent == null ) {
            throw new IllegalArgumentException("Cannot trigger a null event.");
        }
        
        eventLog.add(anEvent); // TODO replace with listener notification
        bplog("Event #" + eventLog.size() + ": " + anEvent);
        Collection<ResumeBThread> resumes = bthreads.stream()
                .filter( bt->bt.getCurrentRwbStatement().shouldWakeFor(anEvent) )
                .map( bt->new ResumeBThread(bt, anEvent) )
                .collect( toList() );            
        executor.invokeAll(resumes);
    }
    
    public void add(Collection<BThread> bts) {
        bthreads.addAll(bts);
    }

    public void add(BThread bt) {
        bthreads.add(bt);
    }
    
    public void registerBThread(BThread bt) {
        add(bt);
        if (started) {
            executor.submit(new StartBThread(bt));
        }
    }
    
    public void emit(BEvent e) {
        bplog("emitted " + e);
        outputEventQueue.add(e);
    }
    
    /**
     * a method that sends events as input for the application
     *
     * @param e
     */
    public void enququeExternalEvent(BEvent e) {
        inputEventQueue.add(e);
    }
    
    public BEvent dequeueExternalEvent() {
        BEvent e = null;
        try {
            e = inputEventQueue.take();
            bplog("dequeued input event " + e);
        } catch (InterruptedException ie) {
            // TODO Auto-generated catch block
            ie.printStackTrace();
        }

        return e;
    }

    public BEvent readOutputEvent() throws InterruptedException {
        return outputEventQueue.take();
    }

    public void setArbiter(Arbiter arbiter) {
        this.arbiter = arbiter;
        arbiter.setProgram(this);
    }
    
    public void bplog(String string) {
        if (debugMode)
            System.out.println("[" + this + "]: " + string);
    }

    public Set<BThread> getBThreads() {
        return bthreads;
    }

    public BEvent getLastEvent() {
        return eventLog.peekLast();
    }

    public Arbiter getArbiter() {
        return arbiter;
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

    public void printEventLog() {

        System.out.println("\n ***** Printing last " + eventLog.size()
                + " choice points out of " + eventLog.size() + ":");

        for (BEvent ev : eventLog)
            System.out.println(ev);

        System.out.println("***** end event bplog ******");
    }
    
}
