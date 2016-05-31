package il.ac.bgu.cs.bp.lscoverbpjs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.XQuery;
import org.xml.sax.InputSource;

/**
 *
 * @author michael
 */
public class SimpleXQueryPoC {

    static String libCode;
    
    public static void main(String[] args) throws BaseXException, IOException {
        Context context = new Context();

        System.out.println("=== Reading the library ===");
        libCode = new String(Files.readAllBytes(Paths.get("lsc-bpj.xql")));
        
        System.out.println("=== Generating BPjs code from LSC xml ===");
        
        
        
        // Create a collection from all XML documents in the specified directory
        CreateDB cdb = new CreateDB("Collection");
        cdb.setInput(new InputSource(Files.newBufferedReader(Paths.get("SimpleLSC.xml"))));
        cdb.execute(context);
        
        
        
        // If the name of the database is omitted in the collection() function,
        // the currently opened database will be referenced
        StringBuilder output = new StringBuilder();
        Stream.of( "messages.xqy", "lifelines.xqy")
                .forEach( filename -> {
                    System.out.print("..processing " + filename);
                    try {
                        output.append( generateQuery(filename).execute(context) );
                        output.append("\n");
                    } catch (BaseXException ex) {
                        output.append("Error parsing file ").append(filename);
                        output.append("\n");
                        output.append("msg: ").append(ex.getMessage());
                        output.append("\n");
                        Logger.getLogger(SimpleXQueryPoC.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println(".. DONE");
                });

        // Drop the database
        new DropDB("Collection").execute(context);
        // Close the database context
        context.close();

        System.out.println("*** OUTPUT ****");
        System.out.println(output.toString());
        Files.write(Paths.get("generated.js"), Collections.singleton(output.toString()));
    }
    
    public static XQuery generateQuery( String filename ) {
        try {
            return new XQuery(
                    libCode + " \n" +
                    Files.readAllLines(Paths.get(filename)).stream()
                            .collect(Collectors.joining("\n")));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
