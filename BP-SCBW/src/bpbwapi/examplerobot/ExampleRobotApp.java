package bpbwapi.examplerobot;

import bpbwapi.BWJavascriptApplication;

/**
 * Created by moshewe on 01/07/2015.
 */
public class ExampleRobotApp extends BWJavascriptApplication {

    public ExampleRobotApp() {
        super();
        _name = "ExampleRobotApp";
        evaluateInGlobalScope("example-robots.js", "examplebot");
        setupBThreadScopes();
    }

}
