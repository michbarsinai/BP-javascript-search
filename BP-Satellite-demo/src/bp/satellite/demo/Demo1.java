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
import bp.satellite.demo.events.ObsAlert;
import bp.satellite.demo.events.PosUpdate;
import bp.satellite.demo.events.StaticEvents;
import org.mozilla.javascript.Scriptable;

/**
 * The main entry point to the demo.
 */
@SuppressWarnings({"serial", "unused"})
public class Demo1 extends BProgram {

    private static GUI gui;

    public Demo1() {
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
        scope.put("LThrust", scope, Context.javaToJS(StaticEvents.LThrust, scope));
        scope.put("RThrust", scope, Context.javaToJS(StaticEvents.RThrust, scope));
        scope.put("TakePicture", scope, Context.javaToJS(StaticEvents.TakePicture, scope));
        scope.put("ObsAvoided", scope, Context.javaToJS(StaticEvents.ObsAvoided, scope));
        scope.put("SoftwareUpdate", scope, Context.javaToJS(StaticEvents.SoftwareUpdate, scope));
        scope.put("VelRecovery", scope, Context.javaToJS(StaticEvents.VelRecovery, scope));
        scope.put("RollBack", scope, Context.javaToJS(StaticEvents.RollBack, scope));
        scope.put("RollBackDisable", scope, Context.javaToJS(StaticEvents.RollBackDisable, scope));
        scope.put("RollBackEnable", scope, Context.javaToJS(StaticEvents.RollBackEnable, scope));

        scope.put("AnyObsAlertEvent", scope, Context.javaToJS(StaticEvents.AnyObsAlertEvent, scope));
        scope.put("AnyPosUpdateEvent", scope, Context.javaToJS(StaticEvents.AnyPosUpdateEvent, scope));

        loadJavascriptResource("globalScopeInit.js");
        loadJavascriptResource("bthreads/baseLogic.js");

    }

    public static void main(String[] args) throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {

        BProgramControls.debugMode = true;

        final Demo1 demo = new Demo1();
        demo.addListener(new StreamLoggerListener());

        demo.addListener(new BPEventListener() {
            @Override
            public void eventSelected(BProgram bp, BEvent e) {

                if (e.equals(StaticEvents.SoftwareUpdate)) {
                    demo.loadJavascriptResource("bthreads/newLogic.js");

                    gui.GuiUpdate();
                }

                if (e.equals(StaticEvents.RollBack)) {
                    demo.loadJavascriptResource("bthreads/baseLogic_rollback.js");
                    gui.GuiRollBack();
                }

                if (e.equals(StaticEvents.TakePicture)) {
                    gui.takePicture();
                }
                if (StaticEvents.AnyObsAlertEvent.contains(e)) {
                    gui.obsdetected();
                    gui.RBackButton.setEnabled(false);
                }
                if (StaticEvents.RollBackDisable.contains(e)) {
                    gui.RBackButton.setEnabled(false);
                }
                if (StaticEvents.RollBackEnable.contains(e)) {
                    gui.RBackButton.setEnabled(true);
                }
                if (StaticEvents.AnyPosUpdateEvent.contains(e)) {
                    gui.time = ((PosUpdate) e).SimTime;
                    gui.pos = ((PosUpdate) e).SatPos;
                    gui.vel = ((PosUpdate) e).SatVel;
                    gui.updateguitele();
                }
                if (e.equals(StaticEvents.ObsAvoided)) {
                    gui.obsavoided();
                }
                if (e.equals(StaticEvents.RThrust)) {
                    gui.RTfire();
                }
                if (e.equals(StaticEvents.LThrust)) {
                    gui.LTfire();
                }
                if (e.equals(StaticEvents.VelRecovery)) {
                    gui.VelRec();
                }

                if (e.equals(StaticEvents.StartSimulation)) {
                    Thread tmrThread = new Thread(() -> {
                        while (true) {
                            try {
                                Thread.sleep(50);
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
