/*
 *  Author: Michael Bar-Sinai
 */
package il.ac.bgu.cs.bp.lscoverbpjs;

/**
 *
 * @author michael
 */
public class XQueryRunnerException extends RuntimeException {
    
    private final String source;

    public XQueryRunnerException(String source, String message, Throwable cause) {
        super(message, cause);
        this.source = source;
    }

    public String getSource() {
        return source;
    }
    
}
