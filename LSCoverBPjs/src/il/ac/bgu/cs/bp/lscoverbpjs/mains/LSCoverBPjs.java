package il.ac.bgu.cs.bp.lscoverbpjs.mains;

import bp.bprogram.listeners.StreamLoggerListener;
import il.ac.bgu.cs.bp.lscoverbpjs.LscBProgram;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author michael
 */
public class LSCoverBPjs {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException {
        LscBProgram lscbp = new LscBProgram() {
            @Override
            protected String getLscBpjCode() {
                try {
                    return new String( Files.readAllBytes(Paths.get("BasicLSC.xml.js")));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
        lscbp.addListener( new StreamLoggerListener() );
        lscbp.start();
    }
    
}
