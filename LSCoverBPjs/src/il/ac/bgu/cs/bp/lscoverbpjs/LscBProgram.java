/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs;

import bp.bprogram.BProgram;
import bp.eventsets.Events;
import il.ac.bgu.cs.bp.lscoverbpjs.events.VisibleEvent;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * A BProgram that's adapted to running LSC bpjs.
 * @author michael
 */
public abstract class LscBProgram extends BProgram {

    @Override
    protected void setupProgramScope( Scriptable aScope ) {
        aScope.put("lsc", aScope, Context.javaToJS(new LscBpjsAdapter(), aScope));
        
        evaluateInGlobalContext(aScope, getLscBpjCode(), "Transpiled LSC");
    }
    
    protected abstract String getLscBpjCode();
    
}
