package bp;

import bp.tasks.*;

import java.util.*;
import java.util.concurrent.*;

import static bp.BProgramControls.debugMode;
import bp.eventsets.ComposableEventSet;
import bp.eventsets.EventSets;
import java.util.stream.Collectors;
import bp.eventsets.Requestable;
import bp.eventsets.EventSet;

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
     * TODO remove, replace with a logger BThread.
     */
    public transient Deque<BEvent> eventLog = new LinkedList<>();
    
    /**
     * Program name is set to be the simple class name by default.
     */
    protected transient String name;
    private Arbiter arbiter;
    private BlockingQueue<BEvent> inputEventQueue;
    private BlockingQueue<BEvent> outputEventQueue;
    protected ExecutorService executor;
    
    // TODO probably replace with a full enum of PRE_RUN, IN_STEP, IDLE.
    private boolean started = false;

    public BPApplication() {
        this(null);
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
     * Set fields unset by constructors of this class or its subclasses.
     */
    {
        if ( getName() == null ) {
            setName( this.getClass().getSimpleName() );
        }
    }
    
    public void bplog(String string) {
        if (debugMode)
            System.out.println("[" + this + "]: " + string);
    }

    public Set<BThread> getBThreads() {
        return bthreads;
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
     * @return the given bprogram's _name
     */
    public String getName() {
        return name;
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

    public void setDebugMode(boolean mode) {
        // TODO implement?
    }

    /**
     * Sets the error that occurred during the run, to make BPApplication terminate
     * at the next bSync and print the error.
     *
     * @param error An Object of the error occurred during the run - better have
     *              an informative toString().
     */
    public static void setError(Object error) {
        // TODO implement?
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Get all waited-for events when program is idle
     *
     * @return
     */
    public Collection<EventSet> getWatchedEventSets() {
        Collection<EventSet> ret = new ArrayList<>();
        for (BThread bt : bthreads) {
            ret.add(bt.getCurrentRwbStatement().getWaitFor());
        }
        return ret;
    }

    /**
     * Get all events that are requested but blocked when program is idle
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
        for (Iterator<BThread> it = bthreads.iterator();
             it.hasNext(); ) {
            BThread bt = it.next();
            if (!bt.isAlive()) {
                it.remove();
            }
        }
    }

    /**
     * Used by arbiters to notify programs of events triggered.
     *
     * @param lastEvent
     */
    public void triggerEvent(BEvent lastEvent) {
        String st;
        if (lastEvent != null) {
            eventLog.add(lastEvent);
            st = "Event #" + eventLog.size() + ": " + getLastEvent();
            bplog(st);
            bplog(">> starting bthread wakeup");
            // Interrupt and notify the be-threads that need to be
            // awaken
            Collection<ResumeBThread> resumes
                    = new LinkedList<>();
            for (BThread bt : bthreads) {
                resumes.add(new ResumeBThread(bt, lastEvent));
            }
            List<Future<Void>> futures = null;
            try {
                futures = executor.invokeAll(resumes);
                for (Future future : futures) {
                    future.get();
                }
            } catch (InterruptedException e) {
                bplog("INVOKING BTHREAD RESUMES INTERRUPTED");
                e.printStackTrace();
            } catch (ExecutionException e) {
                bplog("EXCEPTION WHILE EXECUTING BTHREAD");
                e.printStackTrace();
            }
            bplog("<< finished bthread wakeup");
        } else { // lastEvent == null -> deadlock?
            st = "No events chosen. Waiting for external event or stuck in bsync...?";
            bplog(st);
        }
    }

    public BEvent getLastEvent() {
        return eventLog.peekLast();
    }

    public Arbiter getArbiter() {
        return arbiter;
    }

    public void add(Collection<BThread> bts) {
        bthreads.addAll(bts);
    }

    public void add(BThread bt) {
        bthreads.add(bt);
    }

    /**
     * a method that sends events as input for the application
     *
     * @param e
     */
    public void fire(BEvent e) {
//        bplog(e + " fired!");
        inputEventQueue.add(e);
    }

    public BEvent getInputEvent() {
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

    public void emit(BEvent e) {
        bplog("emitted " + e);
        outputEventQueue.add(e);
    }

    public BEvent readOutputEvent() throws InterruptedException {
        BEvent take = outputEventQueue.take();
        return take;
    }

    public void start() throws InterruptedException {
        bplog("********* Starting " + bthreads.size()
                + " scenarios  **************");

        Collection<StartBThread> startBtTasks = new ArrayList<>(bthreads.size());
        bthreads.stream().map( bt-> new StartBThread(bt) )
                          .forEach( startBtTasks::add );
        
        executor.invokeAll(startBtTasks);
        bplog("********* " + bthreads.size()
                + " scenarios started **************");
        started = true;
        executor.execute(new EventLoopTask(this, arbiter));
    }

    public void registerBThread(BThread bt) {
        add(bt);
        if (started) {
            executor.execute(new StartBThread(bt));
        }
    }
    
    
    public void setArbiter(Arbiter arbiter) {
        this.arbiter = arbiter;
        arbiter.setProgram(this);
    }


}
