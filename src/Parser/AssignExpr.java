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
            throw new ParserException("Error in ParseExpression(AssignExpr):"
                    + " unexpected token: " + t.getType().toString());
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
        
        Operation op;
        Operand toReturn;
        Operand src1 = rhs.genLLCode(f);
        //Don't gencode the left-hand side
//        Operand dest = lhs.genLLCode(f);
        Operand dest;
        String name = ((Factor)lhs).data;
        if(f.getTable().containsKey(name)){//local variable
            op = new Operation(Operation.OperationType.ASSIGN, b);
            dest = new Operand(Operand.OperandType.REGISTER,
                f.getTable().get(name));
            toReturn = dest;
            op.setSrcOperand(0, src1);
            op.setDestOperand(0, dest);
            
        }
        else{//global
            op = new Operation(Operation.OperationType.STORE_I, b);
            op.setSrcOperand(0, src1);
            op.setSrcOperand(1, new Operand(Operand.OperandType.STRING, name));
            //Arrays not supported
            op.setSrcOperand(2, new Operand(Operand.OperandType.INTEGER, 0));
            toReturn = op.getSrcOperand(1);//probably not used
        }       
        
        //Set pointers
        b.appendOper(op);
        return toReturn;
    }
    
}