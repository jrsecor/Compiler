package Parser;
import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import lowlevel.*;

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

    @Override
    public Operand genLLCode(Function f) throws CodeGenerationException {
        BasicBlock b = f.getCurrBlock();
        Operand src = rhs.genLLCode(f);
        Operand dest = lhs.genLLCode(f);
        Operation op = new Operation(Operation.OperationType.ASSIGN, b);
        
        op.setSrcOperand(0, src);
        op.setDestOperand(0, dest);
        
        //Set pointers
        op.setPrevOper(b.getLastOper());
        if(b.getLastOper() == null){
            b.setFirstOper(op);
            b.setLastOper(op);
        }
        else{
            b.getLastOper().setNextOper(op);
            b.appendOper(op);
        }  
        return dest;
    }
    
}
