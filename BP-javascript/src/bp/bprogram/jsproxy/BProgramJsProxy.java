/*
 *  Author: Michael Bar-Sinai
 */
package bp.bprogram.jsproxy;

import bp.bprogram.BProgram;
import bp.bprogram.BThread;
import bp.events.BEvent;
import bp.eventsets.JsEventSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.mozilla.javascript.Function;

/**
 * An object representing a {@link BProgram} at the Javascript code. Used in order to
 * prevent JS code from touching parts of the BProgram it shouldn't.
 * @author michael
 */
public class BProgramJsProxy {
    
    public enum LogLevel {Off, Warn, Info, Fine}
    
    public class BpLog {
        LogLevel level = LogLevel.Info;
        
        public void Warn( String msg ) { log( LogLevel.Warn, msg ); }
        public void info( String msg ) { log( LogLevel.Info, msg ); }
        public void fine( String msg ) { log( LogLevel.Fine, msg ); }
        
        public void log( LogLevel lvl, String msg ) {
            if ( level.compareTo(lvl) <= 0) {
                System.out.println("[JS][" + lvl.name() + "] " + msg );
            }
        }
    }
    
    private final BProgram program;
    
    private final AtomicInteger autoAddCounter = new AtomicInteger(0);
    
    public final BpLog log = new BpLog();
    
    public BProgramJsProxy(BProgram program) {
        this.program = program;
    }
    
    /**
     * Event constructor, called from Javascript, hence the funny
     * capitalization.
     *
     * @param name name of the event
     * @return an event with the passed name.
     */
    public BEvent Event(String name) {
        return new BEvent(name);
    }

    /**
     * Event constructor, called from Javascript, hence the funny
     * capitalization.
     *
     * @param name name of the event
     * @param jsData Additional data for the object.
     * @return an event with the passed name.
     */
    public BEvent Event(String name, Object jsData) {
        return new BEvent(name, jsData);
    }
    
    public JsEventSet EventSet(String name, Function predicate) {
        return new JsEventSet(name, predicate);
    }
    
    /**
     * Called from JS to add BThreads running func as their runnable code.
     *
     * @param name
     * @param func
     *
     * @see #registerBThread(org.mozilla.javascript.Function)
     */
    public void registerBThread(String name, Function func) {
        program.registerBThread(new BThread(name, func));
    }

    /**
     * Registers a BThread and gives it a unique name. Use when you don't care
     * about the added BThread's name.
     *
     * @param func the BThread to add.
     *
     * @see #registerBThread(java.lang.String, org.mozilla.javascript.Function)
     */
    public void registerBThread(Function func) {
        registerBThread("autoadded-" + autoAddCounter.incrementAndGet(), func);
        
    }
    
    /**
     * Sets whether the BProgram will wait for external events when there's
     * no internal event to choose.
     * 
     * @param newDaemonMode 
     */
    public void setDaemonMode( boolean newDaemonMode ) {
        program.setDaemonMode( newDaemonMode );
    }
    
    public boolean isDaemonMode() {
        return program.isDaemonMode();
    }
    
    public void loadJavascriptResource( String path ) {
        loadJavascriptResource(path, false);
    }
    
    /**
     * Loads a Javascript resource (a file that's included in the .jar).
     *
     * @param path path of the resource, relative to the class.
     * @param absolute is the path global, or relative to the current class.
     */
    public void loadJavascriptResource(String path, boolean absolute) {
        program.loadJavascriptResource(path, absolute);
    }
    
}
