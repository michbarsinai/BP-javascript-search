package bp.satellite.demo;

import bp.BProgramControls;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.Context;

import bp.bprogram.BProgram;
import bp.bprogram.listeners.BPEventListener;
import bp.bprogram.listeners.StreamLoggerListener;
import bp.events.BEvent;
import bp.satellite.demo.events.PosUpdate;
import bp.satellite.demo.events.StaticEvents;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import bp.bprogram.BProgram;
import bp.events.BEvent;
import bp.bprogram.BProgram;
import bp.events.BEvent;
import bp.satellite.demo.events.PosUpdate;
import bp.satellite.demo.events.StaticEvents;
import org.mozilla.javascript.Scriptable;

/**
 * The main entry point to the demo.
 */
@SuppressWarnings({"serial", "unused"})
public class Demo extends BProgram {

    private static GUI gui;

    public Demo() {
        super("Demo Updated");
        setDaemonMode(true);
        SwingUtilities.invokeLater(() -> {
            gui = new GUI(this);
        });

    }

    @Override
    protected void setupProgramScope(Scriptable scope) {

        scope.put("StartSimulation", scope, Context.javaToJS(StaticEvents.StartSimulation, scope));
        scope.put("Tick", scope, Context.javaToJS(StaticEvents.Tick, scope));
        scope.put("TakePicture", scope, Context.javaToJS(StaticEvents.TakePicture, scope));
        scope.put("posupdate", scope, Context.javaToJS(StaticEvents.PosUpdateEvent, scope));

        loadJavascriptResource("globalScopeInit.js");
        loadJavascriptResource("bthreads/logic.js");

    }

    public static void main(String[] args) throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {

        BProgramControls.debugMode = false;

        final Demo demo = new Demo();
        demo.addListener(new StreamLoggerListener());

        demo.addListener(new BPEventListener() {
            @Override
            public void eventSelected(BProgram bp, BEvent e) {
                if (e.equals(StaticEvents.TakePicture)) {
                    gui.takePicture();
                }
                if (StaticEvents.PosUpdateEvent.contains(e)) {
                    gui.time = ((PosUpdate) e).SimTime;
                    gui.pos = ((PosUpdate) e).SatPos;
                    gui.updateguitele();
                }

                if (e.equals(StaticEvents.StartSimulation)) {
                    Thread tmrThread = new Thread(() -> {
                        while (true) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                            gui.fireTimeTick();
                        }
                    });
                    tmrThread.start();
                }
            }
        });
        demo.start();
    }
}
