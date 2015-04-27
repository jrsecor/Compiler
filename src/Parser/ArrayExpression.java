package Parser;

import java.util.ArrayList;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class ArrayExpression extends Expression{

    Expression index;
    String id;

    ArrayExpression(String s) {
        id = s;
    }
    
    @Override
    public Expression parseExpression(Token t) throws ParserException{
        t = compiler.Compiler.scanner.getNextToken();
        if (t.getType() != Token.TokenType.LBRAK_TOKEN){
            throw new ParserException("Error in ParseExpression(array): unexpected token: " + t.getType().toString());
        }
        index = new ArithmeticExpression();//should be fixed by the next line
        t = compiler.Compiler.scanner.getNextToken();
        index = index.getNextExpression(t);
        t = compiler.Compiler.scanner.getNextToken();
        if (t.getType() != Token.TokenType.RBRAK_TOKEN){
            throw new ParserException("Error in ParseExpression(array): unexpected token: " + t.getType().toString());
        }
        return this;
    }

    @Override
    public void print(int indent, BufferedWriter write) throws IOException {
        String s = "";
        for(int i = 0; i < indent; i++){
            s += "\t";
        }
        write.append(s + "ArrayExpression\r\n");
        s += "\t";
        write.append(s + id + "\r\n");
        index.print(indent + 1, write);
    }
    
}
