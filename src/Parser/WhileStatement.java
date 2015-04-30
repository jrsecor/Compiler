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
            throw new ParserException("Error in parseStatement(WHILE) : Unexpected Token: " + t.getType().toString());
        }
        t = compiler.Compiler.scanner.getNextToken();
        expr = new ArithmeticExpression();//the next line should change what type of expression it is if neccessary
        expr = expr.getNextExpression(t);
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() != Token.TokenType.RPAREN_TOKEN){
            throw new ParserException("Error in parseStatement(WHILE) : Unexpected Token: " + t.getType().toString());
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
        
    }
    
}
