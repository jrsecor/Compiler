package Parser;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import lowlevel.*;

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
    
    @Override
    public CodeItem genLLCode(Function f){
        //f will always be null....
        int type = returnType.equals("int") ? Data.TYPE_INT : Data.TYPE_VOID;
        Function toReturn = new Function(type, name);
        FuncParam firstParam = null;
        FuncParam nextParam1 = null;
        FuncParam nextParam2;
        for(int i = 0; i < params.size(); i++){
            if(i == 0){
                firstParam = params.get(i).genLLCode(toReturn);
                nextParam1 = firstParam;
            }
            else{
                nextParam2 = params.get(i).genLLCode(toReturn);
                nextParam1.setNextParam(nextParam2);
            }
        }
        toReturn.setFirstParam(firstParam);        
        // 3) Make Basic Block 0
        toReturn.createBlock0();
        // 4) make basic block (block 0 is sentinal)
        BasicBlock b = new BasicBlock(toReturn);
        toReturn.appendBlock(b);
        toReturn.setCurrBlock(b);
        
        //GenCode CompoundStatement
        cmpdStmt.genLLCode(toReturn);
        
        //Append Return Block
        toReturn.appendBlock(toReturn.getReturnBlock());
        
        //Append unconnected chain
        if(toReturn.getFirstUnconnectedBlock() != null){
            toReturn.appendBlock(toReturn.getFirstUnconnectedBlock());
        }
        return toReturn;
    }
}
