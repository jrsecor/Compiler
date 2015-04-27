package Parser;

import java.util.ArrayList;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class BinaryExpr extends Expression{

    String opType;
    Expression lhs;
    Expression rhs;

    BinaryExpr(Expression exp) {
        lhs = exp;
    }
    
    @Override
    public Expression parseExpression(Token t) throws ParserException {
        t = compiler.Compiler.scanner.getNextToken();
        switch(t.getType()){
            case LT_TOKEN:
                opType = "<";
                break;
            case LTE_TOKEN:
                opType = "<=";                
                break;
            case GT_TOKEN:
                opType = ">";
                break;
            case GTE_TOKEN:
                opType = ">=";
                break;
            case EQ_TOKEN:
                opType = "==";
                break;
            case NEQ_TOKEN:
                opType = "!=";
                break;
            default:
                throw new ParserException("Error in parseExpression (BinaryExpr): unexpected token: "+ t.getType().toString());
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
        write.append(s + "BinaryExpression\r\n");
        s += "\t";
        write.append(s + opType + "\r\n");
        lhs.print(indent + 2, write);
        rhs.print(indent + 2, write);        
    }    
}
