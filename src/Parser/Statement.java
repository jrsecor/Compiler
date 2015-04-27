package Parser;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public abstract class Statement {
    
    public abstract Statement parseStatement(Token t) throws ParserException;
    public abstract void print(int indent, BufferedWriter write) throws IOException;
}
