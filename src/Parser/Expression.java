package Parser;

import CMinusScanner.*;
import com.sun.javafx.fxml.expression.BinaryExpression;
import java.io.BufferedWriter;
import java.io.IOException;
import lowlevel.*;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public abstract class Expression {
    Expression exp;
    public abstract Expression parseExpression(Token t) throws ParserException;
    public abstract void print(int indent, BufferedWriter write) throws IOException;
    public abstract Operand genLLCode(Function f) throws CodeGenerationException; 
    
    public Expression(){
        exp = null;
    }
    
    public Expression getNextExpression(Token t) throws ParserException{
        if(t.getType() == Token.TokenType.LPAREN_TOKEN){
            exp = getNextExpression(compiler.Compiler.scanner.getNextToken());
            t = compiler.Compiler.scanner.getNextToken();
            if(t.getType() != Token.TokenType.RPAREN_TOKEN){
                throw new ParserException("Error in in getNextExpression: unexpected token: " + t.getType().toString() + " " + t.getData().toString());
            }
            return parseSimpleExpression(t);
        }
        if(t.getType() == Token.TokenType.NUM_TOKEN){
            exp = new Factor((Integer) t.getData());
            return parseSimpleExpression(t);
        }
        if(t.getType() == Token.TokenType.ID_TOKEN){
            exp = new Factor(t.getData().toString());
            return parseExpressionPrime(t);
        }
        //error
        return null;
    }
    
    public Expression parseSimpleExpression(Token t) throws ParserException{
        t = compiler.Compiler.scanner.viewNextToken();
        Expression e;
        if(t.getType() == Token.TokenType.PLUS_TOKEN ||
                t.getType() == Token.TokenType.MINUS_TOKEN ||
                t.getType() == Token.TokenType.DIV_TOKEN ||
                t.getType() == Token.TokenType.MULT_TOKEN){
            e = new ArithmeticExpression(exp);
        }
        else if (t.getType() == Token.TokenType.GT_TOKEN ||
                t.getType() == Token.TokenType.GTE_TOKEN ||
                t.getType() == Token.TokenType.LT_TOKEN ||
                t.getType() == Token.TokenType.LTE_TOKEN ||
                t.getType() == Token.TokenType.EQ_TOKEN ||
                t.getType() == Token.TokenType.NEQ_TOKEN){           
            e = new BinaryExpr(exp);
        }
        else{
            return exp;
        }
        return e.parseExpression(t);
    }

    private Expression parseExpressionPrime(Token t) throws ParserException {
        Expression e = null;
        t = compiler.Compiler.scanner.viewNextToken();
        if(t.getType() == Token.TokenType.ASSIGN_TOKEN){
            e = new AssignExpr(exp);
        }
        else if (t.getType() == Token.TokenType.LPAREN_TOKEN){            
            e = new Factor(((Factor)exp).data);
        }
        else if (t.getType() == Token.TokenType.LBRAK_TOKEN){
            e = new ArrayExpression(((Factor)exp).data);            
        }
        if(e != null){
            e = e.parseExpression(t);
        }
        else{
            e = exp;
        }
        if(e instanceof ArrayExpression){
            //production 3 of rule 15
            exp = e;
            e = parseExpressionDoublePrime(t);
        }
        else if(e instanceof Factor){
            //productions 2 and 4 of rule 15            
            exp = e;
            e = parseSimpleExpression(t);
        }
        return e;
    }

    private Expression parseExpressionDoublePrime(Token t) throws ParserException {
        Expression e = null;
        t = compiler.Compiler.scanner.viewNextToken();
        if(t.getType() == Token.TokenType.ASSIGN_TOKEN){
            e = new AssignExpr(exp);
        }
        if(e == null){
            return parseSimpleExpression(t);
        }
        return e.parseExpression(t);
    }
}
