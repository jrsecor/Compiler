package Parser;

import java.util.ArrayList;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import lowlevel.*;

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

    @Override
    public Operand genLLCode(Function f) throws CodeGenerationException {
        BasicBlock b = f.getCurrBlock();
        Operation op;
        //Source 1
        Operand src1 = lhs.genLLCode(f);
        Operand src2 = rhs.genLLCode(f);
        switch(opType){
            case "<":
                op = new Operation(Operation.OperationType.LT, b);
                break;
            case "<=":
                op = new Operation(Operation.OperationType.LTE, b);
                break;
            case ">":
                op = new Operation(Operation.OperationType.GT, b);
                break;
            case ">=":
                op = new Operation(Operation.OperationType.GTE, b);
                break;
            case "==":
                op = new Operation(Operation.OperationType.EQUAL, b);
                break;
            case "!=":
                op = new Operation(Operation.OperationType.NOT_EQUAL, b);
                break;
            default:
                throw new CodeGenerationException("Error in Arithmetic Gen Code");
        }
        op.setSrcOperand(0, src1);
        op.setSrcOperand(1, src2);
        Operand dest = new Operand(Operand.OperandType.REGISTER,
                b.getFunc().getNewRegNum());
        op.setDestOperand(0, src2);
        
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
