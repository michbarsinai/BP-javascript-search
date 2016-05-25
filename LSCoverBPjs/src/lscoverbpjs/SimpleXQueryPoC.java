package lscoverbpjs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
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

    public static void main(String[] args) throws BaseXException, IOException {
        Context context = new Context();

        System.out.println("=== QueryCollection ===");

        // Create a collection from all XML documents in the specified directory
        System.out.println("* Create a collection.");

        CreateDB cdb = new CreateDB("Collection");
        cdb.setInput(new InputSource(Files.newBufferedReader(Paths.get("books.xml"))));
        cdb.execute(context);
        cdb = new CreateDB("Collection");
        cdb.setInput(new InputSource(Files.newBufferedReader(Paths.get("SimpleLSC.xml"))));
        cdb.execute(context);

        // List all documents in the database
        System.out.println("\n* List all documents in the database:");

        // The XQuery base-uri() function returns a file path
        System.out.println(new XQuery(
                "for $doc in collection('Collection.xml')"
                + "return <doc path='{ base-uri($doc) }'/>"
        ).execute(context));

        // Evaluate a query on a single document
        System.out.println("\n* Evaluate a query on a single document:");

        // If the name of the database is omitted in the collection() function,
        // the currently opened database will be referenced
        System.out.println(new XQuery(
                Files.readAllLines(Paths.get("query.xq")).stream().collect(Collectors.joining("\n"))
        ).execute(context));

        // Drop the database
        System.out.println("\n* Drop the database.");

        new DropDB("Collection").execute(context);

        // Close the database context
        context.close();

    }

    private static void print(String s) {
        java.lang.System.out.println(s);
    }
}
