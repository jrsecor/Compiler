package Parser;
import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import lowlevel.*;
/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class Param {
    String id;    
    
    public Param parseParam() throws ParserException{
        //munch int
        Token t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() == Token.TokenType.VOID_TOKEN){
            return null;
        }
        if(t.getType() != Token.TokenType.INT_TOKEN){
            throw new ParserException("Error in parseParam: "
                    + t.getType().toString());
        }
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() != Token.TokenType.ID_TOKEN){
            throw new ParserException("Error in parseParam: "
                    + t.getType().toString());
        }
        id = t.getData().toString();
        t = compiler.Compiler.scanner.viewNextToken();
        if(t.getType() == Token.TokenType.LBRAK_TOKEN){
            compiler.Compiler.scanner.getNextToken();
            t = compiler.Compiler.scanner.getNextToken();
            if(t.getType() != Token.TokenType.RBRAK_TOKEN){
                throw new ParserException("Error in parseParam: "
                    + t.getType().toString());
            }
        }
        return this;
    }
    
    public void print(int indent, BufferedWriter write) throws IOException{
        String s = "";
        for(int i = 0; i < indent; i++){
            s += "\t";
        }
        write.append(s + "Param\r\n");
        s += "\t";
        write.append(s + id + "\r\n");
    }
    
    public FuncParam genLLCode(Function f){
        f.getTable().put(id, f.getNewRegNum());
        return new FuncParam(Data.TYPE_INT, id);
    }
}