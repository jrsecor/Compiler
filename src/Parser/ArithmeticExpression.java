package Parser;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class ArithmeticExpression extends Expression{

    Expression lhs;
    String opType;
    Expression rhs;

    ArithmeticExpression(Expression exp) {
        lhs = exp; 
    }
    ArithmeticExpression() {
        
    }
    
    @Override
    public Expression parseExpression(Token t) throws ParserException {
        t = compiler.Compiler.scanner.getNextToken();
        switch(t.getType()){
            case PLUS_TOKEN:
                opType = "+";
                break;
            case MINUS_TOKEN:
                opType = "-";                
                break;
            case MULT_TOKEN:
                opType = "*";
                break;
            case DIV_TOKEN:
                opType = "/";
                break;
            default:
                throw new ParserException("Error in parseExpression (ArithmeticExpression): unexpected token: "+ t.getType().toString());
        }
        t = compiler.Compiler.scanner.getNextToken();
        rhs = (new ArithmeticExpression()).getNextExpression(t);
        
        return this;
    }

    @Override
    public void print(int indent, BufferedWriter write) throws IOException {
        String s = "";
        for(int i = 0; i < indent; i++){
            s += "\t";
        }
        write.append(s + "ArithmeticExpression\r\n");
        s += "\t";
        write.append(s + opType + "\r\n");
        lhs.print(indent + 2, write);
        rhs.print(indent + 2, write);
    }
    
}
