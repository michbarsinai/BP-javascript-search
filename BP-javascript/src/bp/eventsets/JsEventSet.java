/*
 *  Author: Michael Bar-Sinai
 */
package bp.eventsets;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;

/**
 * An event set whose predicate is a Javascript function.
 * @author michael
 */
public class JsEventSet implements EventSet {
    
    private final Function predicate;
    
    public JsEventSet( Function aPredicate ) {
        predicate = aPredicate;
    }

    public Function getPredicate() {
        return predicate;
    }

    @Override
    public boolean contains(Object o) {
        Context ctxt = Context.enter();
        try {
            Object result = predicate.call(ctxt, predicate, predicate.getParentScope(), new Object[]{o});
            ctxt.setOptimizationLevel(-1);
            try {
                Boolean res = (Boolean)Context.jsToJava(result, Boolean.class);
                if ( res == null ) {
                    throw new RuntimeException("JS Predicate returned null, not a boolean value.");
                }
                return res;
            } catch ( EvaluatorException ee ) {
                throw new RuntimeException("JS Predicate did not return a boolean value.", ee);
            }
            
        } finally {
            Context.exit();
        }
    }
    
}
