package Parser;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class ExpressionStatement extends Statement{

    Expression expr;
    
    @Override
    public Statement parseStatement(Token t) throws ParserException {
        if(t.getType() == Token.TokenType.SEMI_TOKEN || t.getType() == Token.TokenType.EOF_TOKEN){
            expr = null;
        }
        else if(t.getType() == Token.TokenType.LPAREN_TOKEN ||
                    t.getType() == Token.TokenType.NUM_TOKEN ||
                    t.getType() == Token.TokenType.ID_TOKEN){
            
            expr = new ArithmeticExpression();//the next line should change what type of expression it is if neccessary
            expr = expr.getNextExpression(t);
            t = compiler.Compiler.scanner.getNextToken();
        }
        else{
            throw new ParserException("Error in parseStatement(ExpressionStmt) : Unexpected Token: " + t.getType().toString());
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
    
}
