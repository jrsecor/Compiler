package Parser;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import lowlevel.*;
/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class VariableDeclaration extends Declaration{
    
    String id;
    int size;
    
    public VariableDeclaration(){
        id = "";
        size = 0;
    }

    @Override
    public Declaration parseDeclaration(Token t) throws ParserException{
        id = t.getData().toString();
        if(compiler.Compiler.scanner.getNextToken().getType() == Token.TokenType.LBRAK_TOKEN){
            size = (Integer)compiler.Compiler.scanner.getNextToken().getData();
            //much the RBRACKET
            Token checker = compiler.Compiler.scanner.getNextToken();
            if(checker.getType() != Token.TokenType.RBRAK_TOKEN){
                throw new ParserException("Error in parseVariableDeclaration: " + checker.getType().toString());
            }
            //munch the SEMI-COLON
            checker = compiler.Compiler.scanner.getNextToken();            
            if(checker.getType() != Token.TokenType.SEMI_TOKEN){
                throw new ParserException("Error in parseVariableDeclaration: " + checker.getType().toString());
            }
        }        
        return this;
    }

    @Override
    public void print(int indent, BufferedWriter write) throws IOException {
        String s = "";
        for(int i = 0; i < indent; i++){
            s += "\t";
        }
        write.append(s + "VariableDeclaration\r\n");
        s += "\t";
        if(size != 0){
           write.append(s + id + "[" + size + "]\r\n"); 
        }
        else{
            write.append(s + id + "\r\n");
        }
    }
    
    @Override
    public CodeItem genLLCode(Function f){
        if(f == null){//global variable
            compiler.Compiler.globalHash.put(id, compiler.Compiler.globalHash.size() + 1);
            Data d = new Data(Data.TYPE_INT, id, size != 0, size);
            return d;
        }
        else{
            f.getTable().put(id, f.getNewRegNum());
            Data d = new Data(Data.TYPE_INT, id, size != 0, size);
            return d;
        }
    }
    
}
