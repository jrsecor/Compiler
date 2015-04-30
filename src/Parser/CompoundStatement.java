package Parser;

import CMinusScanner.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import lowlevel.*;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class CompoundStatement extends Statement {
    
    ArrayList<Statement> stmts;
    ArrayList<VariableDeclaration> varDecls;

    public CompoundStatement(){
        stmts = new ArrayList<>();
        varDecls = new ArrayList<>();
    }
    
    @Override
    public Statement parseStatement(Token munched) throws ParserException{
        Token t;
        if(munched == null){
            t = compiler.Compiler.scanner.getNextToken();
        }
        else{
            t = munched;
        }
        if(t.getType() != Token.TokenType.LBRACE_TOKEN){
            throw new ParserException("Error in parseCompoundStatement: "
                    + t.getType().toString());
        }
        t = compiler.Compiler.scanner.getNextToken();
        while(t.getType() == Token.TokenType.INT_TOKEN){
            VariableDeclaration vdecl = new VariableDeclaration();
            vdecl.parseDeclaration(compiler.Compiler.scanner.getNextToken());
            varDecls.add(vdecl);
            t = compiler.Compiler.scanner.getNextToken();
        }
        while(t.getType() != Token.TokenType.RBRACE_TOKEN){
            Statement s;
            if(t.getType() == Token.TokenType.RETURN_TOKEN){
                s = new ReturnStatement();
            }
            else if(t.getType() == Token.TokenType.IF_TOKEN){
                s = new IfStatement();
            }
            else if(t.getType() == Token.TokenType.WHILE_TOKEN){
                s = new WhileStatement();
            }
            else if(t.getType() == Token.TokenType.LBRACE_TOKEN){
                s = new CompoundStatement();
            }
            else{
                s = new ExpressionStatement();
            }
            stmts.add(s.parseStatement(t));
            t = compiler.Compiler.scanner.getNextToken();
        }
        
        return this;
    }

    @Override
    public void print(int indent, BufferedWriter write) throws IOException {
        String s = "";
        for(int i = 0; i < indent; i++){
            s += "\t";
        }
        write.append(s + "CompoundStatement\r\n");
        for(int i = 0; i < varDecls.size(); i++){
            varDecls.get(i).print(indent + 1, write);
        }
        for(int i = 0; i < stmts.size(); i++){
            stmts.get(i).print(indent + 1, write);
        }
    }
    public void genLLCode(Function f){
        for(int i = 0; i < varDecls.size(); i++){
            varDecls.get(i).genLLCode(f);
        }
        for(int i = 0; i < stmts.size(); i++){
            stmts.get(i).genLLCode(f);
        }
        
    }
}