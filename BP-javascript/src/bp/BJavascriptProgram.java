package bp;

import org.mozilla.javascript.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import static bp.eventsets.EventSets.all;
import static bp.eventsets.EventSets.none;
import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import static java.nio.file.Paths.get;

/**
 * @author orelmosheweinstock 
 * @author Michael
 */
public abstract class BJavascriptProgram extends BPApplication {

    public static final String GLOBAL_SCOPE_INIT = "BPJavascriptGlobalScopeInit";
    protected Arbiter _arbiter;

    private Scriptable _globalScope;
    
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
        return evaluateInGlobalContext(_globalScope, path);
    }

    public Object evaluateInGlobalScope(InputStream ios,
                                        String scriptname) {
        return evaluateInGlobalContext(_globalScope, ios, scriptname);
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
     * Called from JS to add BThreads running func as their
     * runnable code.
     *
     * @param name
     * @param func
     * @return
     */
    public BThread registerBThread(String name, Function func) {
        Context cx = ContextFactory.getGlobal().enterContext();
        cx.setOptimizationLevel(-1); // must use interpreter mode
        try {
            BThread bt = new BThread(name, func);
            super.registerBThread(bt);
            return bt;
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
        setupGlobalScope();
        setupBThreadScopes();
        super.start();
    }
    
    protected void setupBThreadScopes() {
        try {
            Context cx = ContextFactory.getGlobal().enterContext();
            cx.setOptimizationLevel(-1); // must use interpreter mode
            bthreads.forEach( bt -> bt.setupScope(_globalScope) );
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
            _globalScope = cx.initStandardObjects(importer);
            _globalScope.put("bpjs", _globalScope,
                    Context.javaToJS(this, _globalScope));
            _globalScope.put("none", _globalScope,
                    Context.javaToJS(none, _globalScope));
            _globalScope.put("all", _globalScope,
                    Context.javaToJS(all, _globalScope));
            
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
            _globalScope.put(objName, _globalScope,
                    Context.javaToJS(obj, _globalScope));
        } finally {
            Context.exit();
        }
    }
    
    public Scriptable getGlobalScope() {
        return _globalScope;
    }
}


