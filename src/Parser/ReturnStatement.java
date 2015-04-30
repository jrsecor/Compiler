package Parser;
import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lowlevel.*;
/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class ReturnStatement extends Statement{
    
    Expression expr;

    @Override
    public Statement parseStatement(Token t) throws ParserException {
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() == Token.TokenType.SEMI_TOKEN){
            expr = null;
        }
        else if(t.getType() == Token.TokenType.LPAREN_TOKEN ||
                    t.getType() == Token.TokenType.NUM_TOKEN ||
                    t.getType() == Token.TokenType.ID_TOKEN){
            expr = new ArithmeticExpression();//the next line should change what type of expression it is if neccessary
            expr = expr.getNextExpression(t);
            t = compiler.Compiler.scanner.getNextToken();
            if(t.getType() != Token.TokenType.SEMI_TOKEN){
                throw new ParserException("Error in parseStatement(Return) : Unexpected Token: " + t.getType().toString());
            }
        }
        else{
            throw new ParserException("Error in parseStatement(Return) : Unexpected Token: " + t.getType().toString());
        }
        return this;
                     
    }

    @Override
    public void print(int indent, BufferedWriter write) throws IOException {
        String s = "";
        for(int i = 0; i < indent; i++){
            s += "\t";
        }
        write.append(s + "ReturnStatement\r\n");
        if(expr != null){
            expr.print(indent + 1, write);
        }
    }

    @Override
    public void genLLCode(Function f) {
        BasicBlock b = f.getCurrBlock();        
        try {
            if(expr != null){
                Operand src = expr.genLLCode(f);
                Operand dest = new Operand(Operand.OperandType.MACRO, "RetReg");
                Operation op = new Operation(Operation.OperationType.ASSIGN, b);
                op.setDestOperand(0, dest);
                op.setSrcOperand(0, src);
                b.appendOper(op);
            }
            Operation op = new Operation(Operation.OperationType.JMP, b);
            Operand src = new Operand(Operand.OperandType.BLOCK, 
                        f.getReturnBlock().getBlockNum());
            op.setSrcOperand(0, src);
            b.appendOper(op);
        } catch (Exception ex) {
            System.err.println("error in ReturnStatement::genLLCode()");
        }
        
    }
    
}
