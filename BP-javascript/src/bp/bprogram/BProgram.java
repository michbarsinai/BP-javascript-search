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
import static java.util.stream.Collectors.toList;

/**
 * Base class for BPrograms. Contains the logic for managing {@link BThread}s and 
 * the main event loop. Lacks the technology-specific parts, such as Javascript scope setups.
 * 
 * @author michael
 */
public abstract class BProgram  {

    /**
     * A collection containing all the BThreads in the system. A BThread
     * adds itself to the list either - in its constructor and removes itself
     * when its run() function finishes - or a Java thread adds itself and
     * removes itself explicitly
     */
    public Set<BThread> bthreads;
    
    private String name;
    private final BlockingQueue<BEvent> inputEventQueue = new LinkedBlockingQueue<>();
    private final ExecutorService executor = new ForkJoinPool();
    private EventSelectionStrategy eventSelectionStrategy;
    
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
        
        executor.invokeAll( bthreads.stream()
                                .map( bt-> new StartBThread(bt) )
                                .collect(toList()) );
        
        bthreadCleanup();
        if ( bthreads.isEmpty() ) {
            listeners.forEach( l -> l.ended(this) );
        } else {
            superStep();
        }
    }
   
    
    
    /**
     * Advances the BProgram a single super-step, that is until there are 
     * no more internal events that can be selected.
     * 
     * @throws InterruptedException
     * @return The reason the super-step terminated.
     */
    public EventSelectionResult.EmptyResult superStep() throws InterruptedException {
        
        EventSelectionResult esr = eventSelectionStrategy.select(currentStatements());    
        while( esr instanceof Selected  ) {
            BEvent selectedEvent = ((Selected)esr).getEvent();
            triggerEvent(selectedEvent);
            bthreadCleanup();
            esr = eventSelectionStrategy.select(currentStatements());
        }
        final EmptyResult endEvent = (EmptyResult)esr;
        listeners.forEach( l->l.superstepDone(this, endEvent) );
        return endEvent;
    }
    
    protected Collection<RWBStatement> currentStatements() { 
        return bthreads.stream()
                .map( BThread::getCurrentRwbStatement )
                .collect( toList() );
    }
   
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
    }
    
    public void add(Collection<BThread> bts) {
        bts.forEach( bt -> add(bt) );
    }

    public void add(BThread bt) {
        bthreads.add(bt);
        listeners.forEach( l -> l.bthreadAdded(this, bt) );
    }
    
    public void registerBThread(BThread bt) {
        add(bt);
        if (started) {
            // FIXME this means we need to wait for this BThread to terminate. Superstep needs updating.
            executor.submit(new StartBThread(bt));
        }
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
        try {
            return inputEventQueue.take();
        } catch (InterruptedException ie) {
            return null;
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
