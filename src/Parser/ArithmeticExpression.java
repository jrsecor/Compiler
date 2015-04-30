package Parser;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import lowlevel.*;

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
    public void print(int indent, BufferedWriter write) throws IOException{
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

    @Override
    public Operand genLLCode(Function f) throws CodeGenerationException{
        BasicBlock b = f.getCurrBlock();
        Operation op;
        //Source 1
        Operand src1 = lhs.genLLCode(f);
        Operand src2 = rhs.genLLCode(f);
        boolean src1Imm = src1.getType() == Operand.OperandType.INTEGER;
        boolean src2Imm = src2.getType() == Operand.OperandType.INTEGER;
        switch(opType){
            case "+":
                if(src1Imm || src2Imm){
                    op = new Operation(Operation.OperationType.ADD_I, b);
                }
                else{
                    op = new Operation(Operation.OperationType.X64_ADD_Q, b);
                }
                break;
                
            case "-":
                if(src1Imm || src2Imm){
                    op = new Operation(Operation.OperationType.SUB_I, b);
                }
                else{
                    op = new Operation(Operation.OperationType.X64_SUB_Q, b);
                }
                break;
            case "*":
                op = new Operation(Operation.OperationType.MUL_I, b);
                break;
                
            case "/":
                op = new Operation(Operation.OperationType.DIV_I, b);
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
