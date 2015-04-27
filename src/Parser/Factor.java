package Parser;
import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
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
            throw new ParserException("Error in parseExpression (Factor): unexpected token: "+ t.getType().toString());
        }
        isCall = true;
        while(compiler.Compiler.scanner.viewNextToken().getType() != Token.TokenType.RPAREN_TOKEN){
            if(compiler.Compiler.scanner.viewNextToken().getType() == Token.TokenType.COMMA_TOKEN){
                compiler.Compiler.scanner.getNextToken();
            }
            t = compiler.Compiler.scanner.getNextToken();            
            Expression expr = ((new ArithmeticExpression()).getNextExpression(t));
            args.add(expr);
        }
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() != Token.TokenType.RPAREN_TOKEN){
            throw new ParserException("Error in parseExpression (Factor): unexpected token: "+ t.getType().toString());
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
    
}