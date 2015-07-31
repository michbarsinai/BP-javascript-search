package bp.tasks;

import bp.BEvent;
import bp.BPApplication;
import bp.actuation.IActuatorService;

import java.util.concurrent.Callable;

/**
 * Created by moshewe on 27/07/2015.
 */
public class ActuationTask implements Callable<Void>, Runnable {

    protected BPApplication _app;
    protected IActuatorService _actService;

    public ActuationTask(BPApplication _app,
                         IActuatorService actuationService) {
        this._app = _app;
        _actService = actuationService;
    }

    @Override
    public Void call() throws Exception {
        System.out.println("ActuationTask started!");
        while (true) {
            BEvent event = _app.readOutputEvent();
            _actService.actuate(event);
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
