/*
 *  Author: Michael Bar-Sinai
 */
package bp;

import bp.bprogram.runtimeengine.BProgram;
import bp.bprogram.runtimeengine.BThreadSyncSnapshot;
import bp.eventselection.EventSelectionResult;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ContinuationPending;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.serialize.ScriptableOutputStream;

/**
 * Playing around with continuations.
 *
 * @author michael
 */
public class ContinuationGames {

    static class BPP extends BProgram implements java.io.Serializable {

        @Override
        protected void setupProgramScope(Scriptable scope) {
            evaluateBpCode(scope,
                    "bp.registerBThread( \"bt\", function(){\n"
                    + "   bp.log.info(\"started\");"
                    + "   var i=1;"
                    + "   bsync({waitFor: bp.Event(\"e\")});\n"
                    + "   i = i+1;"
                    + "   bp.log.info(i);"
                    + "});", "");
        }

        @Override
        public EventSelectionResult.EmptyResult mainEventLoop() throws InterruptedException {
            try {
                BThreadSyncSnapshot bt = bthreads.iterator().next();
                Object cnt = bt.getContinuation();
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                Context.enter();
//                outs = new ScriptableOutputStream(bytes, bt.getScope());
//                outs.writeObject(cnt);
//                outs.close();
//                bytes.close();
                Context.exit();
//                byte[] contBytes = bytes.toByteArray();
//                System.out.println(contBytes);

                Context globalContext = ContextFactory.getGlobal().enterContext();
                globalContext.setOptimizationLevel(-1); // must use interpreter mode
                for (int i = 0; i < 10; i++) {
                    globalContext.resumeContinuation(cnt, globalScope, "");
                }
                Context.exit();

            } catch (Exception ex) {
                Logger.getLogger(ContinuationGames.class.getName()).log(Level.SEVERE, null, ex);
            }
            return EventSelectionResult.NONE_REQUESTED;
        }

    }

    public static void main(String[] args) throws InterruptedException {
        BProgram bpp = new BPP();

        bpp.start();
    }
}
