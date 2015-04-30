package Parser;
import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import lowlevel.*;
/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class WhileStatement extends Statement {

    Expression expr;
    Statement stmt;
    
    @Override
    public Statement parseStatement(Token t) throws ParserException {
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() != Token.TokenType.LPAREN_TOKEN){
            throw new ParserException("Error in parseStatement(WHILE) :"
                    + " Unexpected Token: " + t.getType().toString());
        }
        t = compiler.Compiler.scanner.getNextToken();
        expr = new ArithmeticExpression();
        expr = expr.getNextExpression(t);
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() != Token.TokenType.RPAREN_TOKEN){
            throw new ParserException("Error in parseStatement(WHILE) :"
                    + " Unexpected Token: " + t.getType().toString());
        }
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() == Token.TokenType.RETURN_TOKEN){
            stmt = new ReturnStatement();
        }
        else if(t.getType() == Token.TokenType.IF_TOKEN){
            stmt = new IfStatement();
        }
        else if(t.getType() == Token.TokenType.WHILE_TOKEN){
            stmt = new WhileStatement();
        }
        else if(t.getType() == Token.TokenType.LBRACE_TOKEN){
            stmt = new CompoundStatement();
        }
        else{
            stmt = new ExpressionStatement();
        }
        stmt = stmt.parseStatement(t);
        return this;
    }

    @Override
    public void print(int indent, BufferedWriter write) throws IOException {
        String s = "";
        for(int i = 0; i < indent; i++){
            s += "\t";
        }
        write.append(s + "WhileStatement\r\n");
        expr.print(indent + 1, write);
        stmt.print(indent + 1, write);
    }

    @Override
    public void genLLCode(Function f) {
        BasicBlock b = f.getCurrBlock();
        try {
            //1 Gencode
            Operand binExp = expr.genLLCode(f);
            
            //2 Make BasicBlock
            BasicBlock loopBlock = new BasicBlock(f);
            BasicBlock postBlock = new BasicBlock(f);
            //3 Make Branch
            Operation branch = new Operation(Operation.OperationType.BEQ, b);
            branch.setSrcOperand(0, binExp);
            Operand src1 = new Operand(Operand.OperandType.INTEGER, 0);
            branch.setSrcOperand(1, src1);
            Operand src2;
            src2 = new Operand(Operand.OperandType.BLOCK, 
                    postBlock.getBlockNum());            
            branch.setSrcOperand(2, src2);
            b.appendOper(branch);
            
            //4 Append Loop Block
            f.appendToCurrentBlock(loopBlock);
            
            //5 CurrentBlock is loop block
            f.setCurrBlock(loopBlock);
            
            //6 Gencode Loop
            stmt.genLLCode(f);
            
            //7 Recompute Loop Condition
            binExp = expr.genLLCode(f);
            
            //8 Make Branch
            branch = new Operation(Operation.OperationType.BNE, b);
            branch.setSrcOperand(0, binExp);
            src1 = new Operand(Operand.OperandType.INTEGER, 0);
            branch.setSrcOperand(1, src1);
            src2 = new Operand(Operand.OperandType.BLOCK, 
                    loopBlock.getBlockNum());            
            branch.setSrcOperand(2, src2);
            //Loop back goes into loop block
            loopBlock.appendOper(branch);
            
            //8 append post
            f.appendToCurrentBlock(postBlock);
            
            //9 CurrentBlock = post
            f.setCurrBlock(postBlock);
        } catch (CodeGenerationException ex) {
            System.err.println("Error in WhileStatement::genLLCode()");
        }        
    }
}