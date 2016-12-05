/*
 *  (C) Michael Bar-Sinai
 */
package bp.eventselection;

import bp.bprogram.runtimeengine.BProgramSyncSnapshot;
import bp.events.BEvent;
import bp.bprogram.runtimeengine.BSyncStatement;
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
        assertEquals(EventSelectionResult.NONE_REQUESTED, sut.select(new BProgramSyncSnapshot(Collections.emptyList(), Collections.emptyList())) );
    }
    
    @Test
    public void testUnanimousCase() {
        BEvent expected = eventOne;
        
        List<BSyncStatement> sets = Arrays.asList(BSyncStatement.make(null).request(eventOne),
                BSyncStatement.make(null).request(eventOne).waitFor(eventTwo),
                BSyncStatement.make(null).request(eventOne)
        );
        assertEquals(EventSelectionResult.selected(expected), sut.select(new BProgramSyncSnapshot(sets, Collections.emptyList())));
    }
    
    @Test
    public void testWithBlockingCase() {
        BEvent expected = eventOne;
        
        List<BSyncStatement> sets = Arrays.asList(BSyncStatement.make(null).request(eventOne),
                BSyncStatement.make(null).request(eventTwo),
                BSyncStatement.make(null).block(eventTwo)
        );
        assertEquals(EventSelectionResult.selected(expected), sut.select(new BProgramSyncSnapshot(sets, Collections.emptyList())));
    }
    
    @Test
    public void testDeadlock() {
        List<BSyncStatement> sets = Arrays.asList(BSyncStatement.make(null).request(eventOne),
                BSyncStatement.make(null).request(eventTwo),
                BSyncStatement.make(null).block(eventTwo),
                BSyncStatement.make(null).block(eventOne)
        );
        assertEquals(EventSelectionResult.DEADLOCK, sut.select(new BProgramSyncSnapshot(sets, Collections.emptyList())));
    }
    
    @Test
    public void testNoRequests() {
        List<BSyncStatement> sets = Arrays.asList(BSyncStatement.make(null).waitFor(eventOne),
                BSyncStatement.make(null).waitFor(eventTwo),
                BSyncStatement.make(null).block(eventTwo),
                BSyncStatement.make(null).block(eventOne)
        );
        assertEquals(EventSelectionResult.NONE_REQUESTED, sut.select(new BProgramSyncSnapshot(sets, Collections.emptyList())));
    }
    
    @Test
    public void testDeadlockWithExternals() {
        List<BSyncStatement> sets = Arrays.asList(BSyncStatement.make(null).request(eventOne),
                BSyncStatement.make(null).request(eventTwo),
                BSyncStatement.make(null).block(eventTwo),
                BSyncStatement.make(null).block(eventOne)
        );
        List<BEvent> externals = Arrays.asList(eventOne, eventTri, eventTri, eventTwo);
        assertEquals(EventSelectionResult.selectedExternal(eventTri, 1), 
                      sut.select(new BProgramSyncSnapshot(sets, externals)));
    }
    
     @Test
    public void testNoInternalRequests() {
        List<BSyncStatement> sets = Arrays.asList(BSyncStatement.make(null).waitFor(eventOne),
                BSyncStatement.make(null).waitFor(eventTri),
                BSyncStatement.make(null).waitFor(eventTwo)
        );
        
        List<BEvent> externals = Arrays.asList(eventOne, eventTwo, eventTri, eventTwo);
        assertEquals(EventSelectionResult.selectedExternal(eventOne, 0),
                      sut.select(new BProgramSyncSnapshot(sets, externals)));
    }
    
    
}
