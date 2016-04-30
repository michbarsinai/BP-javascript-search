package bp.tasks;

import bp.events.BEvent;
import bp.bprogram.BProgram;
import bp.actuation.IActuatorService;

import java.util.concurrent.Callable;

/**
 * Created by moshewe on 27/07/2015.
 */
public class ActuationTask implements Callable<Void>, Runnable {

    protected BProgram _app;
    protected IActuatorService _actService;

    public ActuationTask(BProgram _app,
                         IActuatorService actuationService) {
        this._app = _app;
        _actService = actuationService;
    }

    @Override
    public Void call() throws Exception {
        System.out.println("ActuationTask started!");
        while (true) {
//            BEvent event = _app.readOutputEvent();
//            _actService.actuate(event);
// Now should add a listener to the BProgram, and make a callback when the requested event is selected,
        }
    }

    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
