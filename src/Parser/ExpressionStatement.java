package Parser;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lowlevel.Function;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class ExpressionStatement extends Statement{

    Expression expr;
    
    @Override
    public Statement parseStatement(Token t) throws ParserException {
        if(t.getType() == Token.TokenType.SEMI_TOKEN || 
                t.getType() == Token.TokenType.EOF_TOKEN){
            expr = null;
        }
        else if(t.getType() == Token.TokenType.LPAREN_TOKEN ||
                    t.getType() == Token.TokenType.NUM_TOKEN ||
                    t.getType() == Token.TokenType.ID_TOKEN){
            
            expr = new ArithmeticExpression();
            expr = expr.getNextExpression(t);
            t = compiler.Compiler.scanner.getNextToken();
        }
        else{
            throw new ParserException("Error in parseStatement(ExpressionStmt)"
                    + " : Unexpected Token: " + t.getType().toString());
        }
        return this;
    }

    @Override
    public void print(int indent, BufferedWriter write) throws IOException {
        String s = "";
        for(int i = 0; i < indent; i++){
            s += "\t";
        }
        write.append(s + "ExpressionStatement\r\n");
        if(expr != null){
            expr.print(indent + 1, write);
        }
    }

    @Override
    public void genLLCode(Function f) {
        try {
            expr.genLLCode(f);
        } catch (CodeGenerationException ex) {
            System.err.println("Error in ExpressionStatement::genLLCode()");
        }
    }
    
}
