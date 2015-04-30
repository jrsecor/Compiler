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
public class IfStatement extends Statement {

    Expression expr;
    Statement thenPt;
    Statement elsePt;
    
    @Override
    public Statement parseStatement(Token t) throws ParserException {
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() != Token.TokenType.LPAREN_TOKEN){
            throw new ParserException("Error in parseStatement(IF) : Unexpected Token: " + t.getType().toString());
        }
        t = compiler.Compiler.scanner.getNextToken();
        expr = new ArithmeticExpression();//the next line should change what type of expression it is if neccessary
        expr = expr.getNextExpression(t);
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() != Token.TokenType.RPAREN_TOKEN){
            throw new ParserException("Error in parseStatement(IF) : Unexpected Token: " + t.getType().toString());
        }
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() == Token.TokenType.RETURN_TOKEN){
            thenPt = new ReturnStatement();
        }
        else if(t.getType() == Token.TokenType.IF_TOKEN){
            thenPt = new IfStatement();
        }
        else if(t.getType() == Token.TokenType.WHILE_TOKEN){
            thenPt = new WhileStatement();
        }
        else if(t.getType() == Token.TokenType.LBRACE_TOKEN){
            thenPt = new CompoundStatement();
        }
        else{
            thenPt = new ExpressionStatement();
        }
        thenPt = thenPt.parseStatement(t);
        t = compiler.Compiler.scanner.viewNextToken();
        if(t.getType() == Token.TokenType.ELSE_TOKEN){
            t = compiler.Compiler.scanner.getNextToken();
            t = compiler.Compiler.scanner.getNextToken();
            if(t.getType() == Token.TokenType.RETURN_TOKEN){
                elsePt = new ReturnStatement();
            }
            else if(t.getType() == Token.TokenType.IF_TOKEN){
                elsePt = new IfStatement();
            }
            else if(t.getType() == Token.TokenType.WHILE_TOKEN){
                elsePt = new WhileStatement();
            }
            else if(t.getType() == Token.TokenType.LBRACE_TOKEN){
                elsePt = new CompoundStatement();
            }
            else{
                elsePt = new ExpressionStatement();
            }
            elsePt = elsePt.parseStatement(t);
        }
        
        return this;
    }

    @Override
    public void print(int indent, BufferedWriter write) throws IOException {
        String s = "";
        for(int i = 0; i < indent; i++){
            s += "\t";
        }
        write.append(s + "IfStatement\r\n");
        expr.print(indent + 1, write);
        thenPt.print(indent + 1, write);
        if(elsePt != null){
            elsePt.print(indent + 1, write);
        }
    }

    @Override
    public void genLLCode(Function f) {
        BasicBlock b = f.getCurrBlock();
        try {
            //1 Gencode
            Operand binExp = expr.genLLCode(f);
            
            //2 Make BasicBlock
            BasicBlock thenBlock = new BasicBlock(f);
            BasicBlock postBlock = new BasicBlock(f);
            BasicBlock elseBlock = null;
            //3 Make Branch
            Operation branch = new Operation(Operation.OperationType.BEQ, b);
            branch.setSrcOperand(0, binExp);
            Operand src1 = new Operand(Operand.OperandType.INTEGER, 0);
            branch.setSrcOperand(1, src1);
            Operand src2;
            if(elsePt == null){
                src2 = new Operand(Operand.OperandType.BLOCK, 
                    postBlock.getBlockNum());
            }
            else{
                elseBlock = new BasicBlock(f);
                src2 = new Operand(Operand.OperandType.BLOCK, 
                    elseBlock.getBlockNum());
            }
            branch.setSrcOperand(2, src2);
            b.appendOper(branch);
            
            //4 Append Then Block
            f.appendBlock(thenBlock);
            
            //5 CurrentBlock is then block
            f.setCurrBlock(thenBlock);
            
            //6 Gencode Then
            thenPt.genLLCode(f);
            
            //7 append post
            f.appendBlock(postBlock);
            if(elsePt != null){
                //8 CurrentBlock = else
                f.setCurrBlock(elseBlock);
                
                //9 Gencode else
                elsePt.genLLCode(f);
                
                //10 JMP Post
                Operation jump = new Operation(Operation.OperationType.JMP,
                    elseBlock);
                Operand src = new Operand(Operand.OperandType.BLOCK,
                        postBlock.getBlockNum());
                jump.setSrcOperand(0, src);
                elseBlock.appendOper(jump);
                
                //11 Append unconnected?
                f.appendUnconnectedBlock(elseBlock);
            }
            
            //12 CurrentBlock = post
            f.setCurrBlock(postBlock);
        } catch (CodeGenerationException ex) {
            System.err.println("Error in IfStatement::genLLCode()");
        }        
    }    
}
