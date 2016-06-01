/*
 *  (C) Michael Bar-Sinai
 */
package bp.eventselection;

import bp.bprogram.BSyncState;
import bp.events.BEvent;
import bp.bprogram.RWBStatement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class SimpleEventSelectionStrategyTest {
    
    private final BEvent eventOne = new BEvent("one");
    private final BEvent eventTwo = new BEvent("two");
    private final BEvent eventTri = new BEvent("tri");
    
    private SimpleEventSelectionStrategy sut;
    
    @Before
    public void setUp() {
        sut = new SimpleEventSelectionStrategy();
    }

    @Test
    public void testEmptySet() {
        assertEquals( EventSelectionResult.NONE_REQUESTED, sut.select(new BSyncState(Collections.emptyList(), Collections.emptyList())) );
    }
    
    @Test
    public void testUnanimousCase() {
        BEvent expected = eventOne;
        
        List<RWBStatement> sets = Arrays.asList(
                RWBStatement.make(null).request(eventOne),
                RWBStatement.make(null).request(eventOne).waitFor(eventTwo),
                RWBStatement.make(null).request(eventOne)
        );
        assertEquals( EventSelectionResult.selected(expected), sut.select(new BSyncState(sets, Collections.emptyList())));
    }
    
    @Test
    public void testWithBlockingCase() {
        BEvent expected = eventOne;
        
        List<RWBStatement> sets = Arrays.asList(
                RWBStatement.make(null).request(eventOne),
                RWBStatement.make(null).request(eventTwo),
                RWBStatement.make(null).block(eventTwo)
        );
        assertEquals( EventSelectionResult.selected(expected), sut.select(new BSyncState(sets, Collections.emptyList())));
    }
    
    @Test
    public void testDeadlock() {
        List<RWBStatement> sets = Arrays.asList(
                RWBStatement.make(null).request(eventOne),
                RWBStatement.make(null).request(eventTwo),
                RWBStatement.make(null).block(eventTwo),
                RWBStatement.make(null).block(eventOne)
        );
        assertEquals( EventSelectionResult.DEADLOCK, sut.select(new BSyncState(sets, Collections.emptyList())));
    }
    
    @Test
    public void testNoRequests() {
        List<RWBStatement> sets = Arrays.asList(
                RWBStatement.make(null).waitFor(eventOne),
                RWBStatement.make(null).waitFor(eventTwo),
                RWBStatement.make(null).block(eventTwo),
                RWBStatement.make(null).block(eventOne)
        );
        assertEquals( EventSelectionResult.NONE_REQUESTED, sut.select(new BSyncState(sets, Collections.emptyList())));
    }
    
    @Test
    public void testDeadlockWithExternals() {
        List<RWBStatement> sets = Arrays.asList(
                RWBStatement.make(null).request(eventOne),
                RWBStatement.make(null).request(eventTwo),
                RWBStatement.make(null).block(eventTwo),
                RWBStatement.make(null).block(eventOne)
        );
        List<BEvent> externals = Arrays.asList(eventOne, eventTri, eventTri, eventTwo);
        assertEquals( EventSelectionResult.selectedExternal(eventTri, 1), 
                      sut.select(new BSyncState(sets, externals)));
    }
    
     @Test
    public void testNoInternalRequests() {
        List<RWBStatement> sets = Arrays.asList(
                RWBStatement.make(null).waitFor(eventOne),
                RWBStatement.make(null).waitFor(eventTri),
                RWBStatement.make(null).waitFor(eventTwo)
        );
        
        List<BEvent> externals = Arrays.asList(eventOne, eventTwo, eventTri, eventTwo);
        assertEquals( EventSelectionResult.selectedExternal(eventOne, 0),
                      sut.select(new BSyncState(sets, externals)));
    }
    
    
}
