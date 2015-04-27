package Parser;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;

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
    
}
