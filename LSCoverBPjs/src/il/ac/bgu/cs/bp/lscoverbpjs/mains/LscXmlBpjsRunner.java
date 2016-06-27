package il.ac.bgu.cs.bp.lscoverbpjs.mains;

import bp.bprogram.listeners.StreamLoggerListener;
import il.ac.bgu.cs.bp.lscoverbpjs.LscBProgram;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
        final String lscFileName = "SyncLSC.xml";
        
        System.out.println("lscFileName = " + lscFileName);
        
        XQueryRunner transpiler = new XQueryRunner("lsc-main.xqy");
        final String source = transpiler.run(lscFileName);
        DateFormat dfmt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Files.write(Paths.get(lscFileName+".js"), Arrays.asList("// Transpiled "+dfmt.format(new Date()), "\n", source));
        
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
