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
public class MessagePassedEventTest {
    
    public MessagePassedEventTest() {
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
        
        assertNotEquals( mpe1, null );
        assertEquals( mpe1, mpe1 );
        assertEquals( mpe1, mpe2 );
        assertNotEquals( mpe1, new MessagePassedEvent("X", "t", "content", "chart1") );
        assertNotEquals( mpe1, new MessagePassedEvent("f", "X", "content", "chart1") );
        assertNotEquals( mpe1, new MessagePassedEvent("f", "t", "XXXXXXX", "chart1") );
        assertNotEquals( mpe1, new MessagePassedEvent("f", "t", "content", "XXXXXX") );
    }
    
}
