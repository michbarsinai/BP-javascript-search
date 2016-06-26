package il.ac.bgu.cs.bp.lscoverbpjs;

import bp.bprogram.listeners.StreamLoggerListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.basex.core.BaseXException;
import org.mozilla.javascript.EvaluatorException;

/**
 *  
 * Read LSC XML, run.
 * 
 * @author michael
 */
public class LscXmlBpjsRunner {
    
    public static void main(String[] args) throws BaseXException, IOException, InterruptedException {
        final String lscFileName = "BasicLSC.xml";
        
        System.out.println("lscFileName = " + lscFileName);
        
        XQueryRunner transpiler = new XQueryRunner("lsc-main.xqy");
        final String source = transpiler.run(lscFileName);
        Files.write(Paths.get(lscFileName+".js"), Arrays.asList(source));
        
        try {
            new LscBProgram() {
                { addListener(new StreamLoggerListener()); }
                @Override
                protected String getLscBpjCode() {
                    return source;
                }
            }.start();
        } catch ( EvaluatorException ee ) {
            System.out.println("Evaluation Error: " + ee.getMessage());
            System.out.println("Source:");
            System.out.println( source );
        }
    }
}
