package il.ac.bgu.cs.bp.lscoverbpjs.mains;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.DropDB;
import org.basex.core.cmd.XQuery;

/**
 * Runs an XQuery on an XML file. Returns a String result.
 * @author michael
 */
public class XQueryRunner {

    private final String source;
    
    public XQueryRunner( String filename ) {
        source = loadFile( filename );
    }
    
    public String run( String subject ) {
        Context context = new Context();
        try {
            try {
                // Create a collection from all XML documents in the specified directory
                CreateDB cdb = new CreateDB("Collection");
                cdb.execute(context);

                // If the name of the database is omitted in the collection() function,
                // the currently opened database will be referenced
                return new XQuery( source.replace("__INPUT_FILE__", subject)).execute(context);


            } finally {
                // Drop the database
                new DropDB("Collection").execute(context);
                // Close the database context
                context.close();
            }
        } catch (BaseXException ex) {
            throw new RuntimeException( ex );
        }
        
    }

    /**
     * Loads a file, interprets macros (mostly imports). 
     * Note that macros have to be in their own line.
     * 
     * @param filename
     * @return The macro-processed source of the file.
     */
    public final String loadFile( String filename ) {
        try {
            return Files.readAllLines(Paths.get(filename)).stream()
                    .map( line ->
                            line.trim().startsWith("(:#") ? processLine(line) : line )
                    .collect( Collectors.joining("\n"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private String processLine( String line ) {
        // get the directive and parameter
        line = line.trim();
        line = line.split("#")[1].split(":")[0];
        line = line.trim();
        String[] comps = line.split(" ", 2);
        comps[1] = comps[1].trim();
        
        if ( comps[0].equals("import") ) {
            return loadFile( comps[1] );
        } else {
            throw new RuntimeException("Unknown macro directive: " + comps[0] );
        }
    }
    
    public static void main(String[] args) throws BaseXException, IOException {
        final String xquerySource = "query-ref.xq";
        final String xmlInput = "books.xml";
        System.out.println("Generating BPjs code from " + xquerySource );
        XQueryRunner rnr = new XQueryRunner( xquerySource );
        String result = rnr.run( xmlInput );
        
        System.out.println("DONE.");
        System.out.println("\n\nOutput");
        System.out.println("------");
        System.out.println( result );
    }
    
}
