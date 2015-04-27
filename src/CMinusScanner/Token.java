/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CMinusScanner;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class Token {
    public enum TokenType {
        ELSE_TOKEN,
        IF_TOKEN,
        INT_TOKEN,
        RETURN_TOKEN,
        VOID_TOKEN,
        WHILE_TOKEN,
        PLUS_TOKEN,
        MINUS_TOKEN,
        MULT_TOKEN,
        DIV_TOKEN,
        LT_TOKEN,
        LTE_TOKEN,
        GT_TOKEN,
        GTE_TOKEN,
        EQ_TOKEN,
        NEQ_TOKEN,
        ASSIGN_TOKEN,
        SEMI_TOKEN,
        COMMA_TOKEN,
        LPAREN_TOKEN,
        RPAREN_TOKEN,
        LBRAK_TOKEN,
        RBRAK_TOKEN,
        LBRACE_TOKEN,
        RBRACE_TOKEN,
        ID_TOKEN,
        NUM_TOKEN,
        EOF_TOKEN,
        ERROR_TOKEN
    }
    private TokenType tokenType;
    private Object tokenData;
    
    public Token (TokenType type){
        this(type, null);
    }
    public Token (TokenType type, Object data){
        tokenType = type;
        tokenData = data;
    }
    public TokenType getType(){
        return this.tokenType;
    }
    public Object getData(){
        return this.tokenData;
    }
}
