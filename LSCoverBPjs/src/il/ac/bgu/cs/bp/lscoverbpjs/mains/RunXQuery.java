/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs.mains;

import il.ac.bgu.cs.bp.lscoverbpjs.XQueryRunner;
import il.ac.bgu.cs.bp.lscoverbpjs.XQueryRunnerException;

/**
 *
 * @author michael
 */
public class RunXQuery {

    public static void main(String[] args) {
//        final String xquerySource = "query-ref.xq";
//        final String xmlInput = "books.xml";
        final String xquerySource = "lsc-main.xqy";
        final String xmlInput = "LoopLSC.xml";
        
        System.out.println("Generating BPjs code from " + xquerySource );
        try {
            XQueryRunner rnr = new XQueryRunner( xquerySource );
            String result = rnr.run( xmlInput );

            System.out.println("DONE.");
            System.out.println("\n\nOutput");
            System.out.println("------");
            System.out.println( result );
        } catch ( XQueryRunnerException xqre ) {
            System.out.println("Error running query:");
            System.out.println( xqre.getMessage() );
            System.out.println( xqre.getCause().getMessage() );
            System.out.println("----");
            System.out.println( xqre.getSource() );
        }
    }
    
}
