package bp.bprogram;

import bp.bprogram.exceptions.BProgramException;
import bp.bprogram.jsproxy.BProgramJsProxy;
import bp.events.BEvent;
import bp.tasks.*;

import java.util.*;
import java.util.concurrent.*;

import bp.bprogram.listeners.BProgramListener;
import bp.eventselection.EventSelectionResult;
import bp.eventselection.EventSelectionResult.EmptyResult;
import bp.eventselection.EventSelectionResult.Selected;
import bp.eventselection.EventSelectionStrategy;
import bp.eventselection.SimpleEventSelectionStrategy;
import bp.eventsets.Events;
import static bp.eventsets.Events.all;
import static bp.eventsets.Events.emptySet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import static java.nio.file.Files.readAllBytes;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.toList;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import static java.util.stream.Collectors.toSet;
import static java.nio.file.Paths.get;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.WrappedException;

/**
 * Base class for BPrograms. Contains the logic for managing {@link BThread}s
 * and the main event loop. Lacks the technology-specific parts, such as
 * Javascript scope setups.
 *
 * @author michael
 */
public abstract class BProgram {

    /**
     * "Poison pill" to insert to the external event queue. Used only to turn the
     * daemon mode off.
     */
    private static final BEvent NO_MORE_DAEMON = new BEvent("NO_MORE_DAEMON");
    public static final String GLOBAL_SCOPE_INIT = "BPJavascriptGlobalScopeInit";

    public static Object evaluateInGlobalContext(Scriptable scope, InputStream ios, String scriptName) {
        InputStreamReader streamReader = new InputStreamReader(ios);
        BufferedReader br = new BufferedReader(streamReader);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("error while reading javascript fom stream", e);
        }
        String script = sb.toString();
        return evaluateInGlobalContext(scope, script, scriptName);
    }

    public static Object evaluateInGlobalContext(Scriptable scope, URI path) {
        Path pathObject = get(path);
        try {
            String script = new String(readAllBytes(pathObject));
            return evaluateInGlobalContext(scope, script, pathObject.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error while evaluating in global context: " + e.getMessage(), e);
        }
    }

    public static Object evaluateInGlobalContext(Scriptable scope, String script, String scriptName) {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        final Object result = cx.evaluateString(scope, script, scriptName, 1, null);
        Context.exit();
        return result;
    }

    /**
     * A collection containing all the BThreads in the system. A BThread adds
     * itself to the list either - in its constructor and removes itself when
     * its run() function finishes - or a Java thread adds itself and removes
     * itself explicitly
     */
    public Set<BThread> bthreads;

    private String name;

    /**
     * When {@code true}, the bprogram waits for an external event when no
     * internal ones are available.
     */
    private boolean daemonMode;
    private final ExecutorService executor = new ForkJoinPool();
    private EventSelectionStrategy eventSelectionStrategy;

    /**
     * Events are enqueued here by external threads
     */
    private final BlockingQueue<BEvent> recentlyEnquqedExternalEvents = new LinkedBlockingQueue<>();

    /**
     * At the BProgram's leisure, the external event are moved here, where they
     * can be managed.
     */
    private final List<BEvent> enqueuedExternalEvents = new LinkedList<>();

    /**
     * BThreads added between bsyncs are added here.
     */
    private final BlockingQueue<BThread> recentlyRegisteredBthreads = new LinkedBlockingDeque<>();

    private final List<BProgramListener> listeners = new ArrayList<>();

    private volatile boolean started = false;

    protected Scriptable globalScope;

    public BProgram() {
        this(BProgram.class.getSimpleName());
    }

    public BProgram(String aName) {
        this(aName, new SimpleEventSelectionStrategy());
    }

    public BProgram(String aName, EventSelectionStrategy anEventSelectionStrategy) {
        name = aName;
        bthreads = new HashSet<>();
        eventSelectionStrategy = anEventSelectionStrategy;
    }

    /**
     * Removes all BThreads that have finished executing.
     */
    public void bthreadCleanup() {
        for (Iterator<BThread> it = bthreads.iterator(); it.hasNext();) {
            BThread bt = it.next();
            if (!bt.isAlive()) {
                it.remove();
                listeners.forEach(l -> l.bthreadRemoved(this, bt));
            }
        }
    }

    public void start() throws InterruptedException {
        try {
            setup();
            listeners.forEach(l -> l.started(this));
            started = true;

            executor.invokeAll(bthreads.stream()
                    .map(bt -> new StartBThread(bt))
                    .collect(toList()));
            startRecentlyRegisteredBThreads();
            bthreadCleanup();
            if (bthreads.isEmpty()) {
                // super corner case, where no bsyncs were called.
                listeners.forEach(l -> l.ended(this));
            } else {
                do {
                    mainEventLoop();
                } while (isDaemonMode() && waitForExternalEvent());
            }
        } catch ( WrappedException we ) {
            throw new BProgramException(we.getCause());
        }
    }

    final ResultHandler handler = new ResultHandler();

    /**
     * Advances the BProgram a single super-step, that is until there are no
     * more internal events that can be selected.
     *
     * @throws InterruptedException
     * @return The reason the super-step terminated.
     */
    public EventSelectionResult.EmptyResult mainEventLoop() throws InterruptedException {

        handler.endResult = null;
        while (eventSelectionStrategy.select(createBSyncStatement()).accept(handler)) {
        }
        listeners.forEach(l -> l.superstepDone(this, handler.endResult));
        return handler.endResult;

    }

    public Object evaluateInGlobalScope(InputStream ios, String scriptname) {
        return evaluateInGlobalContext(globalScope, ios, scriptname);
    }

    public Object evaluateInGlobalScope(URI path) {
        return evaluateInGlobalContext(globalScope, path);
    }

    public Object evaluateInGlobalScope(String path, String scriptname) {
        try (final InputStream ios = getClass().getResourceAsStream(path)) {
            return evaluateInGlobalScope(ios, scriptname);
        } catch (IOException iox) {
            throw new RuntimeException("Error reading javascript file '" + path + "'", iox);
        }
    }

    /**
     * Loads a Javascript resource.
     *
     * @param path path to the resource, relative to the class of {@code this}.
     *
     * @see #loadJavascriptResource(java.lang.String, boolean)
     */
    public void loadJavascriptResource(String path) {
        loadJavascriptResource(path, false);
    }

    /**
     * Loads a Javascript resource (a file that's included in the .jar).
     *
     * @param path path of the resource, relative to the class.
     * @param absolute is the path global, or relative to the current class.
     */
    public void loadJavascriptResource(String path, boolean absolute) {
        try {
            final URL resource = (absolute ? getClass().getClassLoader().getResource(path) : getClass().getResource(path));
            if (resource == null) {
                throw new RuntimeException("Resource '" + path + "' not found.");
            }
            evaluateInGlobalScope(resource.toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(BProgram.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * makes the obj available in the java script code , under the given name
     * "objName"
     *
     * @param objName
     * @param obj
     */
    protected void putInGlobalScope(String objName, Object obj) {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        try {
            globalScope.put(objName, globalScope, Context.javaToJS(obj, globalScope));
        } finally {
            Context.exit();
        }
    }

    /**
     * Registers a BThread into the program. If the program started, the BThread
     * will take part in the current bstep.
     *
     * @param bt
     */
    public void registerBThread(BThread bt) {
        if (started) {
            bplog("Registered " + bt.getName());
            recentlyRegisteredBthreads.add(bt);
        } else {
            add(bt);
        }
    }

    /**
     * Quick an dirty b-thread removal.
     *
     * @return The b-thread object we deleted.
     * @todo Notify listeners, etc....
     * @deprecated
     *
     * @param name
     */
    public BThread unRegisterBThread(String name) {
        BThread theBt = null;

        loop:
        for (BThread bt : bthreads) {
            if (bt.getName().equals(name)) {
                theBt = bt;
                break loop;
            }
        }

        if (theBt != null) {
            bthreads.remove(theBt);
            bplog("Unregistered " + theBt.getName());
        }

        return theBt;
    }

    /**
     * Awakens BThreads that waited for/requested this event in their last
     * bsync, and waits for them to terminate.
     *
     * @param selectedEvent The event to trigger. Cannot be {@code null}.
     * @throws java.lang.InterruptedException
     */
    public void triggerEvent(BEvent selectedEvent) throws InterruptedException {
        if (selectedEvent == null) {
            throw new IllegalArgumentException("Cannot trigger a null event.");
        }
        listeners.forEach(l -> l.eventSelected(this, selectedEvent));

        bthreads.forEach(bt -> {
            if (bt.getCurrentRwbStatement() == null) {
                System.err.println("SEVERE: " + bt.getName() + " Has null stmt");
            }
        });

        Set<BThread> brokenUpon = bthreads.stream()
                .filter(bt -> bt.getCurrentRwbStatement().getBreakUpon().contains(selectedEvent))
                .collect(toSet());

        // Handle breakUpons
        if (!brokenUpon.isEmpty()) {
            bthreads.removeAll(brokenUpon);
            // FIXME allow these BTHreads last-chance event pumping.
            brokenUpon.forEach(bt -> {
                bt.setAlive(false);
                listeners.forEach(l -> l.bthreadRemoved(this, bt));
                bt.getBreakUponHandler()
                      .ifPresent( func -> executeInScope(func, bt.getScope(), new Object[]{selectedEvent}));
            });
        }

        // See who resumes for the next thread.
        Collection<ResumeBThread> resumes = bthreads.stream()
                .filter(bt -> bt.getCurrentRwbStatement().shouldWakeFor(selectedEvent))
                .map(bt -> new ResumeBThread(bt, selectedEvent))
                .collect(toList());
        executor.invokeAll(resumes);
        startRecentlyRegisteredBThreads();
    }

    public void add(Collection<BThread> bts) {
        bts.forEach(bt -> add(bt));
    }

    public void add(BThread bt) {
        bthreads.add(bt);
        listeners.forEach(l -> l.bthreadAdded(this, bt));
    }

    /**
     * Creates a list of the current {@link RWBStatements} of the current
     * BThreads. Note that the order in the list is arbitrary.
     *
     * @return list of statements in arbitrary order.
     */
    public List<RWBStatement> currentStatements() {
        return bthreads.stream()
                .map(BThread::getCurrentRwbStatement)
                .collect(toList());
    }

    /**
     * a method that sends events as input for the application
     *
     * @param e
     */
    public void enqueueExternalEvent(BEvent e) {
        recentlyEnquqedExternalEvents.add(e);
    }

    /**
     * Convenience method to register event in the program's context. Event
     * names are used as their Javascript name.
     *
     * @param events The events to register.
     * // TODO do we need this, now that we're pure-js?
     */
    protected void registerEvents(BEvent... events) {
        Arrays.asList(events).forEach((BEvent e) -> globalScope.put(e.getName(), globalScope, Context.javaToJS(e, globalScope)));
    }

    class ResultHandler implements EventSelectionResult.Visitor<Boolean> {

        EmptyResult endResult;

        @Override
        public Boolean visit(EventSelectionResult.SelectedExternal se) {
            try {
                enqueuedExternalEvents.remove(se.getIndex());
                triggerEvent(se.getEvent());
                bthreadCleanup();
                return true;
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public Boolean visit(Selected se) {
            try {
                triggerEvent(se.getEvent());
                bthreadCleanup();
                return true;
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
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
        if (enqueuedExternalEvents.remove(NO_MORE_DAEMON)) {
            daemonMode = false;
        }
        return new BSyncState(currentStatements(), Collections.unmodifiableList(enqueuedExternalEvents));
    }

    private void startRecentlyRegisteredBThreads() throws InterruptedException {

        add(recentlyRegisteredBthreads);
        recentlyRegisteredBthreads.forEach(bt -> setupAddedBThread(bt));

        executor.invokeAll(recentlyRegisteredBthreads.stream()
                .map(bt -> new StartBThread(bt))
                .collect(toList()));
        recentlyRegisteredBthreads.clear();
    }

    /**
     * Sets up internal data structures for running.
     */
    protected void setup() {
        setupGlobalScope();
        setupBThreadScopes();
    }

    protected void setupAddedBThread(BThread bt) {
        try {
            Context cx = ContextFactory.getGlobal().enterContext();
            cx.setOptimizationLevel(-1); // must use interpreter mode
            bt.setupScope(globalScope);
        } finally {
            Context.exit();
        }
    }

    protected void setupBThreadScopes() {
        try {
            Context cx = ContextFactory.getGlobal().enterContext();
            cx.setOptimizationLevel(-1); // must use interpreter mode
            bthreads.forEach(bt -> bt.setupScope(globalScope));
        } finally {
            Context.exit();
        }
    }

    protected void setupGlobalScope() {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        try (InputStream script
                = BProgram.class.getResourceAsStream("globalScopeInit.js");) {
            ImporterTopLevel importer = new ImporterTopLevel(cx);
            globalScope = cx.initStandardObjects(importer);
            
            BProgramJsProxy proxy = new BProgramJsProxy(this);
            globalScope.put("bpjs", globalScope,
                    Context.javaToJS(proxy, globalScope)); // for legacy code.
            globalScope.put("bp", globalScope,
                    Context.javaToJS(proxy, globalScope)); // recommended use
            
            // TODO these should be defined in a Javascript preamble, once we can define event sets in JS.
            globalScope.put("emptySet", globalScope,
                    Context.javaToJS(emptySet, globalScope));
            globalScope.put("all", globalScope,
                    Context.javaToJS(all, globalScope));

            // TODO this should go, now that we have named params for bSync
            globalScope.put("noEvents", globalScope,
                    Context.javaToJS(Events.noEvents, globalScope));

            evaluateInGlobalScope(script, GLOBAL_SCOPE_INIT);

            setupProgramScope(globalScope);

        } catch (IOException ex) {
            throw new RuntimeException("Error while setting up global scope", ex);
        } finally {
            Context.exit();
        }
    }

    protected void executeInGlobalScope( Function aFunction, Object[] args  ) {
        executeInScope(aFunction, globalScope, args );
    }
    
    protected void executeInScope( Function aFunction, Scriptable scope, Object[] args  ) {
        Context globalContext = ContextFactory.getGlobal().enterContext();
        globalContext.setOptimizationLevel(-1); // must use interpreter mode
        globalContext.callFunctionWithContinuations(aFunction, scope, args);
        Context.exit();
    }
    
    /**
     * The BProgram should set up its scope here (e.g. load the script for
     * BThreads). This method is called after {@link #setupGlobalScope()}.
     *
     * @param scope the scope to set up.
     */
    protected abstract void setupProgramScope(Scriptable scope);

    private boolean waitForExternalEvent() throws InterruptedException {
        final BEvent newEvent = recentlyEnquqedExternalEvents.take();
        if (newEvent == NO_MORE_DAEMON) {
            daemonMode = false;
            return false;
        } else {
            enqueuedExternalEvents.add(newEvent);
            return true;
        }
    }

    public void bplog(String string) {
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

    public <R extends BProgramListener> R addListener(R aListener) {
        listeners.add(aListener);
        return aListener;
    }

    public void removeListener(BProgramListener aListener) {
        listeners.remove(aListener);
    }

    public void setDaemonMode(boolean newDaemonMode) {
        if (daemonMode && !newDaemonMode) {
            daemonMode = false;
            recentlyEnquqedExternalEvents.add(NO_MORE_DAEMON);
        } else {
            daemonMode = newDaemonMode;
        }
    }

    public boolean isDaemonMode() {
        return daemonMode;
    }

    public Scriptable getGlobalScope() {
        return globalScope;
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
