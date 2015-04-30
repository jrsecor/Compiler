package Parser;

import CMinusScanner.Token;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import lowlevel.*;
/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class Program {
    
    ArrayList<Declaration> decls;
    
    public Program(){
        decls = new ArrayList<>();
    }
    
    public static Program ParseProgram() throws ParserException{
        Program toReturn = new Program();
        Declaration temp = null;
        Token t = compiler.Compiler.scanner.getNextToken();
        Token munched = null;
        while(t.getType() != Token.TokenType.EOF_TOKEN){
            if(t.getType() == Token.TokenType.VOID_TOKEN){
                temp = new FunctionDeclaration();
                munched = null;
            }
            else {
                munched = compiler.Compiler.scanner.getNextToken();
                t =  compiler.Compiler.scanner.viewNextToken();
                if(t.getType() == Token.TokenType.LPAREN_TOKEN){
                    temp = new FunctionDeclaration();
                }
                else if(t.getType() == Token.TokenType.SEMI_TOKEN ||
                                t.getType() == Token.TokenType.LBRAK_TOKEN ||
                                t.getType() == Token.TokenType.EOF_TOKEN){
                    temp = new VariableDeclaration();
                }
                else{
                    throw new ParserException("Error in Parse Program:"
                            + " unexpected token: " + t.getType().toString());
                }
            }
            Declaration d = temp.parseDeclaration(munched);
            toReturn.decls.add(d);
            t = compiler.Compiler.scanner.getNextToken();
        }
        return toReturn;
    }
    
    public void printProgram(int indent, BufferedWriter write) throws IOException{
        write.append("Program\r\n");
        for (int i = 0; i < decls.size(); i++){
            decls.get(i).print(indent + 1, write);
        }
    }
    
    public CodeItem genLLCode(){
        CodeItem first = decls.get(0).genLLCode(null);
        CodeItem current = first;
        for (int i = 1; i < decls.size(); i++){
            CodeItem temp = decls.get(i).genLLCode(null);
            current.setNextItem(temp);
            current = temp;
        }
        return first;
    }
}