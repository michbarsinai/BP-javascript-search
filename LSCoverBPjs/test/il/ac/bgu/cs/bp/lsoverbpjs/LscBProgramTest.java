/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lsoverbpjs;

import org.junit.Test;
import il.ac.bgu.cs.bp.lscoverbpjs.LscBProgram;

/**
 *
 * @author michael
 */
public class LscBProgramTest {
    
    @Test
    public void sanityTest() throws InterruptedException {
        LscBProgram sut = new LscBProgram() {
            @Override
            protected String getLscBpjCode() {
                return "lsc.startChart('xys');\n"
                     + "lsc.endChart();";
            }
        };
        
        sut.start();
    }
}
