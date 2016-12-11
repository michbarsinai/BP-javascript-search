package bp.examples.interrupthandler;

import bp.events.BEvent;
import bp.bprogram.runtimeengine.BProgram;
import bp.bprogram.runtimeengine.exceptions.BProgramException;
import bp.bprogram.runtimeengine.listeners.InMemoryEventLoggingListener;
import bp.bprogram.runtimeengine.listeners.StreamLoggerListener;
import bp.validation.eventpattern.EventPattern;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.mozilla.javascript.Scriptable;

/**
 * @author @michbarsinai
 */
public class InterruptHandlerTest {

    BProgram buildProgram(String jsFilename) {
        return new BProgram(jsFilename) {
            @Override
            protected void setupProgramScope( Scriptable aScope ) {
                loadJavascriptResource(jsFilename + ".js");
            }
        };
    }
    
    @Test
    public void echoEventTest() throws InterruptedException {
        BProgram sut = buildProgram("InterruptHandler");
        sut.addListener( new StreamLoggerListener() );
        InMemoryEventLoggingListener eventLogger = sut.addListener( new InMemoryEventLoggingListener() );
        
        sut.start();
        
        eventLogger.getEvents().forEach(e->System.out.println(e) );
        EventPattern expected = new EventPattern()
                .append(new BEvent("boom"))
                .append(new BEvent("boom"))
                .append(new BEvent("internalValue"));
        
        assertTrue( expected.matches(eventLogger.getEvents()) );
    }
    
    @Test(expected=BProgramException.class)
    public void illegalBsyncTest() throws InterruptedException {
        BProgram sut = buildProgram("InterruptHandler_illegal");
        sut.addListener( new StreamLoggerListener() );
        
        sut.start();
        
        fail("Program should have terminated in error.");
    }

}