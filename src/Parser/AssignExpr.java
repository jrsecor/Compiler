package Parser;
import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class AssignExpr extends Expression{

    Expression lhs;
    Expression rhs;

    AssignExpr(Expression exp) {
        lhs = exp;
    }
    
    @Override
    public Expression parseExpression(Token t) throws ParserException {
        t = compiler.Compiler.scanner.getNextToken();
        if (t.getType() != Token.TokenType.ASSIGN_TOKEN){
            throw new ParserException("Error in ParseExpression(AssignExpr): unexpected token: " + t.getType().toString());
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
        write.append(s + "AssignExpression\r\n");
        lhs.print(indent + 1, write);
        rhs.print(indent +1, write);
    }
    
}
