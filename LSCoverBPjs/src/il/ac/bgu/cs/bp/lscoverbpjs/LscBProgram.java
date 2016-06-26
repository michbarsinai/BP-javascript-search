/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs;

import bp.bprogram.BProgram;
import org.mozilla.javascript.Scriptable;

/**
 * A BProgram that's adapted to running LSC bpjs.
 * @author michael
 */
public abstract class LscBProgram extends BProgram {

    @Override
    protected void setupProgramScope( Scriptable aScope ) {
        loadJavascriptResource("il/ac/bgu/cs/bp/lscoverbpjs/LSC.js",true);
        evaluateInGlobalContext(aScope, getLscBpjCode(), "Transpiled LSC");
    }
    
    protected abstract String getLscBpjCode();
    
}
