/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs.events;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class EnabledEventTest {
    
    public EnabledEventTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testEquals() {
        MessagePassedEvent mpe1 = new MessagePassedEvent("f", "t", "content", "chart1");
        MessagePassedEvent mpe2 = new MessagePassedEvent("f", "t", "content", "chart1");
        MessagePassedEvent mpe3 = new MessagePassedEvent("f", "t", "content", "chartX");
        assertEquals( new EnabledEvent(mpe1), new EnabledEvent(mpe1) );
        assertEquals( new EnabledEvent(mpe1), new EnabledEvent(mpe2) );
        assertNotEquals( new EnabledEvent(mpe1), new EnabledEvent(mpe3) );
    }
    
}
