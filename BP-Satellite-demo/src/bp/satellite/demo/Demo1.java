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
/**
 * The main entry point to the demo.
 */
@SuppressWarnings({"serial", "unused"})
public class Demo1 extends BProgram {

    private static GUI gui;

    public Demo1() {
        super("Demo Updated");
        setDaemonMode(true);
        SwingUtilities.invokeLater( () -> {
            gui = new GUI(this);
        } );
        
    }

    @Override
    protected void setupProgramScope() {

        globalScope.put("StartSimulation", globalScope, Context.javaToJS(StaticEvents.StartSimulation, globalScope));
        globalScope.put("Tick", globalScope, Context.javaToJS(StaticEvents.Tick, globalScope));
        globalScope.put("LThrust", globalScope, Context.javaToJS(StaticEvents.LThrust, globalScope));
        globalScope.put("RThrust", globalScope, Context.javaToJS(StaticEvents.RThrust, globalScope));
        globalScope.put("TakePicture", globalScope, Context.javaToJS(StaticEvents.TakePicture, globalScope));
        globalScope.put("ObsAvoided", globalScope, Context.javaToJS(StaticEvents.ObsAvoided, globalScope));
        globalScope.put("obsalert", globalScope, Context.javaToJS(StaticEvents.ObsAlertEvent, globalScope));
        globalScope.put("posupdate", globalScope, Context.javaToJS(StaticEvents.PosUpdateEvent, globalScope));

        loadJavascriptFile("globalScopeInit.js");
        loadJavascriptFile("bthreads/logic.js");

    }

    public static void main(String[] args) throws MalformedURLException, InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {
        
        BProgramControls.debugMode = false;
        
        final Demo1 demo = new Demo1();
        demo.addListener(new StreamLoggerListener());

        demo.addListener(new BPEventListener() {
            @Override
            public void eventSelected(BProgram bp, BEvent e) {
                if (e.equals(StaticEvents.TakePicture)) {
                    gui.takePicture();
                }
                if (StaticEvents.ObsAlertEvent.contains(e)) {
                    gui.obsdetected();
                }
                if (StaticEvents.PosUpdateEvent.contains(e)){
                    gui.time=((PosUpdate)e).SimTime;     
                    gui.pos=((PosUpdate)e).SatPos; 
                    gui.vel=((PosUpdate)e).SatVel;    
                    gui.updateguitele();
                }
                if (e.equals(StaticEvents.ObsAvoided)) {
                    gui.obsavoided();
                }
                 if (e.equals(StaticEvents.RThrust)) {
                    gui.RTfire();
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
