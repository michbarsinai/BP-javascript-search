package bp.bprogram;

import bp.BProgramControls;
import bp.events.BEvent;
import bp.eventsets.EventSets;
import org.mozilla.javascript.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import static bp.eventsets.EventSets.all;
import static java.nio.file.Files.readAllBytes;
import static bp.eventsets.EventSets.emptySet;
import static java.nio.file.Paths.get;
import java.util.Arrays;
import static java.nio.file.Paths.get;

/**
 * TODO (maybe) merge with BProgram - the BThreads assume Javascript anyway, so no point in splitting BProgram.
 * @author orelmosheweinstock 
 * @author Michael
 */
public abstract class BJavascriptProgram extends BProgram {

    public static final String GLOBAL_SCOPE_INIT = "BPJavascriptGlobalScopeInit";

    private Scriptable globalScope;
    
    public BJavascriptProgram() {
        super();
    }
    
    public BJavascriptProgram( String aName ) {
        super( aName );
    }

    public static Object evaluateInGlobalContext(Scriptable scope,
                                                 String script,
                                                 String scriptName) {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        return cx.evaluateString(scope,
                script,
                scriptName,
                1,
                null);
    }

    public static Object evaluateInGlobalContext(Scriptable scope,
                                                 String path) {
        Path pathObject = get(path);
        try {
            String script = new String(readAllBytes(pathObject));
            return evaluateInGlobalContext(scope, script,
                    pathObject.toString());
        } catch (IOException e) {
            throw new RuntimeException("Error while evaluating in global context: "+ e.getMessage(), e);
        }
    }

    public static Object evaluateInGlobalContext(Scriptable scope,
                                                 InputStream ios,
                                                 String scriptName) {
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

    public Object evaluateInGlobalScope(String path) {
        return evaluateInGlobalContext(globalScope, path);
    }

    public Object evaluateInGlobalScope(InputStream ios,
                                        String scriptname) {
        return evaluateInGlobalContext(globalScope, ios, scriptname);
    }

    public Object evaluateInGlobalScope(String path,
                                        String scriptname) {
        try (InputStream ios = getClass().getResourceAsStream(path) ) {
            return evaluateInGlobalScope(ios, scriptname);
        } catch ( IOException iox ) {
            throw new RuntimeException("Error reading javascript file '" + path + "'", iox);
        }
    }
    
    /**
     * Convenience method to register event in the program's context. Event names are used
     * as their Javascript name.
     * @param events The events to register.
     */
    protected void registerEvents( BEvent... events ) {
        Arrays.asList(events).forEach(
                        e -> globalScope.put( e.getName(), globalScope, Context.javaToJS(e, globalScope)));            
    }
    
    /**
     * Called from JS to add BThreads running func as their
     * runnable code.
     *
     * @param name
     * @param func
     * @return
     */
    public BThread registerBThread(String name, Function func) {
        BThread bt = new BThread(name, func);
        super.registerBThread(bt);
        return bt;
    }
    
    @Override
    protected void setupAddedBThread( BThread bt ) {
        try {
            Context cx = ContextFactory.getGlobal().enterContext();
            cx.setOptimizationLevel(-1); // must use interpreter mode
            bt.setupScope(globalScope);
        } finally {
            Context.exit();
        }
    }
    
    @Override
    public void bplog(String s) {
        if (BProgramControls.debugMode)
            System.out.println(getClass().getSimpleName() + ": " + s);
    }

    @Override
    public void start() throws InterruptedException {
        setup();
        super.start();
    }
    
    /**
     * Sets up internal data structures for running.
     */
    public void setup() {
        setupGlobalScope();
        setupBThreadScopes();
    }
    
    protected void setupBThreadScopes() {
        try {
            Context cx = ContextFactory.getGlobal().enterContext();
            cx.setOptimizationLevel(-1); // must use interpreter mode
            bthreads.forEach(bt -> bt.setupScope(globalScope) );
        } finally {
            Context.exit();
        }
    }

    protected void setupGlobalScope() {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        try (InputStream script =
                    BJavascriptProgram.class.getResourceAsStream("globalScopeInit.js");) {
            ImporterTopLevel importer = new ImporterTopLevel(cx);
            globalScope = cx.initStandardObjects(importer);
            globalScope.put("bpjs", globalScope,
                    Context.javaToJS(this, globalScope));
            globalScope.put("emptySet", globalScope,
                    Context.javaToJS(emptySet, globalScope));
            globalScope.put("noEvents", globalScope,
                    Context.javaToJS(EventSets.noEvents, globalScope));
            globalScope.put("all", globalScope,
                    Context.javaToJS(all, globalScope));
            
            evaluateInGlobalScope(script, GLOBAL_SCOPE_INIT);
            
            setupProgramScope();
            
        } catch (IOException ex) {
            throw new RuntimeException("Error while setting up global scope", ex );
        } finally {
            Context.exit();
        }

    }
    
    /**
     * The BProgram should set up its scope here (e.g. load the script for BThreads).
     * This method is called after {@link #setupGlobalScope()}.
     */
    protected abstract void setupProgramScope();
    
    protected void loadJavascriptFile( String path ) {
        evaluateInGlobalScope(getClass().getResource(path).getPath());
    }
    
    /**
     *  makes the obj available in the java script code , under the given name "objName"
     *
     * @param objName
     * @param obj
     */
    protected void putInGlobalScope(String objName, Object obj) {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        try {
            globalScope.put(objName, globalScope,
                    Context.javaToJS(obj, globalScope));
        } finally {
            Context.exit();
        }
    }
    
    public Scriptable getGlobalScope() {
        return globalScope;
    }
}


