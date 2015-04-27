package Parser;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class FunctionDeclaration extends Declaration{

    CompoundStatement cmpdStmt;
    ArrayList<Param> params;
    String returnType;
    String name;
    
    public FunctionDeclaration(){
        cmpdStmt = new CompoundStatement();
        params = new ArrayList<>();
    }
    
    @Override
    public Declaration parseDeclaration(Token t) throws ParserException{
        if(t == null){
            returnType = "void";
            t = compiler.Compiler.scanner.getNextToken();
        }
        else{
            returnType = "int";
        }
        name = t.getData().toString();
        //munch (
        t = compiler.Compiler.scanner.getNextToken();
        if(t.getType() != Token.TokenType.LPAREN_TOKEN){
            throw new ParserException("Error in parseFunctionDeclaration: "
                                        + t.getType().toString());
        }
        while(t.getType() != Token.TokenType.RPAREN_TOKEN){
            Param p = new Param();
            p = p.parseParam();
            if(p != null){
                params.add(p);                
            }
            t = compiler.Compiler.scanner.getNextToken();
            if(t.getType() != Token.TokenType.RPAREN_TOKEN &&
                        t.getType() != Token.TokenType.COMMA_TOKEN){
                throw new ParserException("Error in parseFunctionDeclaration: "
                            + t.getType().toString());
            }
        }
        //parse compound-stmt
        cmpdStmt = (CompoundStatement)cmpdStmt.parseStatement(null);
        return this;
    }

    @Override
    public void print(int indent, BufferedWriter write) throws IOException {
        String s = "";
        for(int i = 0; i < indent; i++){
            s += "\t";
        }
        write.append(s + "FunctionDeclaration\r\n");
        s += "\t";
        write.append(s + returnType + "\r\n");
        write.append(s + name + "\r\n");
        for(int i = 0; i < params.size(); i++){
            params.get(i).print(indent + 1, write);
        }
        cmpdStmt.print(indent + 1, write);
    }
    
}
