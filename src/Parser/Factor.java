package Parser;
import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import lowlevel.*;
/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class Factor extends Expression{

    public String data;
    boolean isCall;
    ArrayList<Expression> args;
    
    Factor(Integer integer) {
        data = Integer.toString(integer);
        isCall = false;
        args = new ArrayList<>();
    }
    Factor(String s) {
        data = s;
        isCall = false;
        args = new ArrayList<>();
    }

    @Override
    public Expression parseExpression(Token t) throws ParserException {
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() != Token.TokenType.LPAREN_TOKEN){
            throw new ParserException("Error in parseExpression (Factor):"
                    + " unexpected token: "+ t.getType().toString());
        }
        isCall = true;
        while(compiler.Compiler.scanner.viewNextToken().getType() != 
                Token.TokenType.RPAREN_TOKEN){
            if(compiler.Compiler.scanner.viewNextToken().getType() == 
                    Token.TokenType.COMMA_TOKEN){
                compiler.Compiler.scanner.getNextToken();
            }
            t = compiler.Compiler.scanner.getNextToken();            
            Expression expr = ((new ArithmeticExpression()).getNextExpression(t));
            args.add(expr);
        }
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() != Token.TokenType.RPAREN_TOKEN){
            throw new ParserException("Error in parseExpression (Factor):"
                    + " unexpected token: "+ t.getType().toString());
        }
        
        return this;
    }

    @Override
    public void print(int indent, BufferedWriter write) throws IOException {
        String s = "";
        for(int i = 0; i < indent; i++){
            s += "\t";
        }
        write.append(s + data);
        if(isCall){
            write.append("()\r\n");
            for(int i = 0; i < args.size(); i++){
                args.get(i).print(indent + 1, write);
            }
        }
        else{
            write.append("\r\n");
        }
    }

    @Override
    public Operand genLLCode(Function f) throws CodeGenerationException {
        BasicBlock b = f.getCurrBlock();
        Operand toReturn;
        if(isCall){//Call
            //Args
            for(int i = 0; i < args.size(); i++){
                Operation op = new Operation(Operation.OperationType.PASS, b);
                Operand src = args.get(i).genLLCode(f);
                op.setSrcOperand(0, src);
                Attribute att = new Attribute("PARAM_NUM",
                        Integer.toString(i));
                op.addAttribute(att);
                b.appendOper(op);
            }
            
            Operand junk = new Operand(Operand.OperandType.STRING, data);
            Operation op = new Operation(Operation.OperationType.CALL, b);
            op.setSrcOperand(0, junk);
            Attribute att1 = new Attribute("numParams",
                    Integer.toString(args.size()));
            op.addAttribute(att1);
            b.appendOper(op);
            
            toReturn = new Operand(Operand.OperandType.MACRO, "RetReg");
        }
        else if(data.matches("(0|1|2|3|4|5|6|7|8|9)+")){//NUM
            toReturn = new Operand(Operand.OperandType.INTEGER, 
                    Integer.parseInt(data));
        }
        else{//local or global ID
           if(f.getTable().containsKey(data)){//local
                toReturn = new Operand(Operand.OperandType.REGISTER,
                f.getTable().get(data));
            }
            else{//global
               //1 Load
               Operation ld = new Operation(Operation.OperationType.LOAD_I, b);
               Operand src1 = new Operand(Operand.OperandType.STRING, data);
               //Not handling arrays
               Operand src2 = new Operand(Operand.OperandType.INTEGER, 0);
               Operand dest = new Operand(Operand.OperandType.REGISTER,
                       f.getNewRegNum());
               ld.setSrcOperand(0, src1);
               ld.setSrcOperand(1, src2);
               ld.setDestOperand(0, dest);
               b.appendOper(ld);
               
               toReturn = dest;
            }
        }
        return toReturn;
    }    
}