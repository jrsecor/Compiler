/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CMinusScanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class CMinusScanner implements Scanner{
    private BufferedReader inFile;
    private Token nextToken;
    private char c;
    //Used to represent DFA bubbles
    private enum state{
        START,
        STAR,
        SLASH,
        COMMENT,
        LT,
        GT,
        EQ,
        NOT,
        NUM,
        ELSE1,
        ELSE2,
        ELSE3,
        ELSE4,
        IF_INT,
        IF2,
        INT2,
        INT3,
        RETURN1,
        RETURN2,
        RETURN3,
        RETURN4,
        RETURN5,
        RETURN6,
        VOID1,
        VOID2,
        VOID3,
        VOID4,
        WHILE1,
        WHILE2,
        WHILE3,
        WHILE4,
        WHILE5,
        ID,
        ERROR
    }
    
    public CMinusScanner(BufferedReader file) throws IOException{
        inFile = file;
        c = (char)inFile.read();
        nextToken = scanToken();
    }
    
    public Token getNextToken() {
        Token returnToken = nextToken;
        if(nextToken.getType() != Token.TokenType.EOF_TOKEN){
            try {
                nextToken = scanToken();
            } catch (IOException ex) {
                System.err.print(ex.getMessage());
            }
        }
        return returnToken;
    }
    
    public Token viewNextToken(){
        return nextToken;
    }
    
    public Token scanToken() throws IOException{        
        String string = "";
        Token returnToken = null;
        if(!inFile.ready()){
            returnToken = new Token(Token.TokenType.EOF_TOKEN);
        }
        boolean getNextChar = true;
        state s = state.START;
        while(returnToken == null){
            switch(s){
                case START:
                    switch(c){
                        case 'e':
                            s = state.ELSE1;
                            break;
                        case 'i':
                            s = state.IF_INT;
                            break;
                        case 'w':
                            s = state.WHILE1;
                            break;
                        case 'v':
                            s = state.VOID1;
                            break;
                        case 'r':
                            s = state.RETURN1;
                            break;
                        case '+':
                            returnToken = new Token(Token.TokenType.PLUS_TOKEN);
                            
                            break;
                        case '-':
                            returnToken = new Token(Token.TokenType.MINUS_TOKEN);
                            
                            break;
                        case '*':
                            returnToken = new Token(Token.TokenType.MULT_TOKEN);
                            
                            break;
                        case ';':
                            returnToken = new Token(Token.TokenType.SEMI_TOKEN);
                            
                            break;
                        case ',':
                            returnToken = new Token(Token.TokenType.COMMA_TOKEN);
                            
                            break;
                        case '(':
                            returnToken = new Token(Token.TokenType.LPAREN_TOKEN);
                            
                            break;
                        case ')':
                            returnToken = new Token(Token.TokenType.RPAREN_TOKEN);
                            
                            break;
                        case '[':
                            returnToken = new Token(Token.TokenType.LBRAK_TOKEN);
                            
                            break;
                        case ']':
                            returnToken = new Token(Token.TokenType.RBRAK_TOKEN);
                            
                            break;
                        case '{':
                            returnToken = new Token(Token.TokenType.LBRACE_TOKEN);
                            
                            break;
                        case '}':
                            returnToken = new Token(Token.TokenType.RBRACE_TOKEN);
                            
                            break;
                        case '<':
                            s = state.LT;
                            break;
                        case '>':
                            s = state.GT;
                            break;
                        case '=':
                            s = state.EQ;
                            break;
                        case '!':
                            s = state.NOT;
                            break;
                        case '/':
                            s = state.SLASH;
                            break;
                        default:
                            if(isNum(c)){
                                s = state.NUM;
                                string += c;
                            }
                            else if(isLetter(c)){
                                s = state.ID;
                                string += c;
                            }
                            else if(isWhitespace(c)){
                                s = state.START;
                            }
                            else{
                                s = state.ERROR;
                            }
                            break;
                    }
                    break;
                case ELSE1:
                    switch(c){
                        case 'l':
                            s = state.ELSE2;
                            break;
                        default:
                            if (isLetter(c)){
                                s = state.ID;
                                string += "e" + c;
                            }
                            else if (isWhitespace(c) || isSymbol(c)){
                                returnToken = new Token(Token.TokenType.ID_TOKEN, "e");
                                getNextChar = false;
                            }
                            else{
                                s = state.ERROR;                                
                            }
                            break;
                    }
                    break;
                case ELSE2:
                    switch(c){
                        case 's':
                            s = state.ELSE3;
                            break;
                        default:
                            if (isLetter(c)){
                                s = state.ID;
                                string += "el" + c;
                            }
                            else if (isWhitespace(c) || isSymbol(c)){
                                returnToken = new Token(Token.TokenType.ID_TOKEN, "el");
                                getNextChar = false;
                            }
                            else{
                                s = state.ERROR;                                
                            }
                            break;
                    }
                    break;
                case ELSE3:
                    switch(c){
                        case 'e':
                            s = state.ELSE4;
                            break;
                        default:
                            if (isLetter(c)){
                                s = state.ID;
                                string += "els" + c;
                            }
                            else if (isWhitespace(c) || isSymbol(c)){
                                returnToken = new Token(Token.TokenType.ID_TOKEN, "els");
                                getNextChar = false;
                            }
                            else{
                                s = state.ERROR;                                
                            }
                            break;
                    }
                    break;
                case ELSE4:
                    if(isLetter(c)){
                        s = state.ID;
                                string += "else" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ELSE_TOKEN);
                    }
                    else if (isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ELSE_TOKEN);
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case IF_INT:
                    switch(c){
                        case 'f':
                            s = state.IF2;
                            break;
                        case 'n':
                            s = state.INT2;
                            break;
                        default:
                            if(isLetter(c)){
                                s = state.ID;
                                string += "i" + c;
                            }
                            else if(isWhitespace(c)||isSymbol(c)){
                                returnToken = new Token(Token.TokenType.ID_TOKEN, "i");
                                getNextChar = false;
                            }
                            else{
                                s = state.ERROR;
                            }
                            break;
                    }
                    break;
                case IF2:
                    if(isLetter(c)){
                        s = state.ID;
                        string += "if" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.IF_TOKEN);
                    }
                    else if (isSymbol(c)){
                        returnToken = new Token(Token.TokenType.IF_TOKEN);
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case INT2:
                    if(c == 't'){
                        s = state.INT3;
                    }
                    else if (isLetter(c)){
                        s = state.ID;
                        string += "in" + c;
                    }
                    else if (isWhitespace(c) || isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "in");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case INT3:
                    if(isLetter(c)){
                        s = state.ID;
                        string += "int" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.INT_TOKEN);
                    }
                    else if (isSymbol(c)){
                        returnToken = new Token(Token.TokenType.INT_TOKEN);
                        getNextChar = false;
                    }
                    break;
                case WHILE1:
                    if(c == 'h'){
                        s = state.WHILE2;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "w" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "w");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "w");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case WHILE2:
                    if(c == 'i'){
                        s = state.WHILE3;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "wh" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "wh");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "wh");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case WHILE3:
                    if(c == 'l'){
                        s = state.WHILE4;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "whi";
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "whi");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "whi");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case WHILE4:
                    if(c == 'e'){
                        s = state.WHILE5;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "whil" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "whil");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "whil");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case WHILE5:
                    if(isLetter(c)){
                        s = state.ID;
                        string = "while" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.WHILE_TOKEN);
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.WHILE_TOKEN);
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case RETURN1:
                    if(c == 'e'){
                        s = state.RETURN2;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "r" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "r");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "r");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case RETURN2:
                    if(c == 't'){
                        s = state.RETURN3;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "re" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "re");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "re");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case RETURN3:
                    if(c == 'u'){
                        s = state.RETURN4;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "ret" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "ret");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "ret");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case RETURN4:
                    if(c == 'r'){
                        s = state.RETURN5;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "retu" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "retu");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "retu");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case RETURN5:
                    if(c == 'n'){
                        s = state.RETURN6;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "retur" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "retur");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "retur");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case RETURN6:
                    if(isLetter(c)){
                        s = state.ID;
                        string = "return" + c;
                    }
                    else if(isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.RETURN_TOKEN);
                    }
                    else if(isSymbol(c)){
                        getNextChar = false;
                        returnToken = new Token(Token.TokenType.RETURN_TOKEN);
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case VOID1:
                    if(c == 'o'){
                        s = state.VOID2;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "v" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "v");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "v");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case VOID2:
                    if(c == 'i'){
                        s = state.VOID3;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "vo" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "vo");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "vo:");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case VOID3:
                    if(c == 'd'){
                        s = state.VOID4;
                    }
                    else if(isLetter(c)){
                        s = state.ID;
                        string = "voi" + c;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "voi");
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, "voi");
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case VOID4:
                    if(isLetter(c)){
                        s = state.ID;
                        string = "void" + c;
                    }
                    else if(isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.VOID_TOKEN);
                    }
                    else if(isSymbol(c)){
                        getNextChar = false;
                        returnToken = new Token(Token.TokenType.VOID_TOKEN);
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case ID:
                    if(isLetter(c)){
                        s = state.ID;
                        string += c;
                    }
                    else if(isWhitespace(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, string);
                    }
                    else if(isSymbol(c)){
                        returnToken = new Token(Token.TokenType.ID_TOKEN, string);
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case NUM:
                    if(isNum(c)){
                        s = state.NUM;
                        string += c;
                    }
                    else if(isWhitespace(c)){
                        Integer i = Integer.parseInt(string);
                        returnToken = new Token(Token.TokenType.NUM_TOKEN, i);
                    }
                    else if(isSymbol(c)){
                        Integer i = Integer.parseInt(string);
                        returnToken = new Token(Token.TokenType.NUM_TOKEN, i);
                        getNextChar = false;
                    }
                    else{
                        s = state.ERROR;
                    }
                    break;
                case ERROR:
                    if(isWhitespace(c)){
                        returnToken = new Token (Token.TokenType.ERROR_TOKEN);
                    }
                    if(isSymbol(c)){
                        returnToken = new Token (Token.TokenType.ERROR_TOKEN);
                        getNextChar = false;
                    }
                    break;
                case STAR:
                    if(c == '/'){
                        s = state.START;
                    }
                    else if(c == '*'){
                        s = state.STAR;
                    }
                    else{
                        s = state.COMMENT;
                    }
                    break;
                case SLASH:
                    if(c == '*'){
                        s = state.COMMENT;
                    }
                    else if (isWhitespace(c)){
                        returnToken = new Token (Token.TokenType.DIV_TOKEN);                        
                    }
                    else{
                        returnToken = new Token (Token.TokenType.DIV_TOKEN);                        
                        getNextChar = false;
                    }
                    break;
                case COMMENT:
                    if(c == '*'){
                        s = state.STAR;
                    }
                    break;
                case LT:
                    if(isWhitespace(c)){
                        returnToken = new Token (Token.TokenType.LT_TOKEN);
                    }
                    else if(c == '='){
                        returnToken = new Token (Token.TokenType.LTE_TOKEN);
                    }
                    else{
                        returnToken = new Token (Token.TokenType.LT_TOKEN);
                        getNextChar = false;
                    }
                    break;
                case GT:
                    if(isWhitespace(c)){
                        returnToken = new Token (Token.TokenType.GT_TOKEN);
                    }
                    else if(c == '='){
                        returnToken = new Token (Token.TokenType.GTE_TOKEN);
                    }
                    else{
                        returnToken = new Token (Token.TokenType.GT_TOKEN);
                        getNextChar = false;
                    }
                    break;
                case EQ:
                    if(isWhitespace(c)){
                        returnToken = new Token (Token.TokenType.ASSIGN_TOKEN);
                    }
                    else if(c == '='){
                        returnToken = new Token (Token.TokenType.EQ_TOKEN);
                    }
                    else{
                        returnToken = new Token (Token.TokenType.ASSIGN_TOKEN);
                        getNextChar = false;
                    }
                    break;
                case NOT:
                    if(c == '='){
                        returnToken = new Token (Token.TokenType.NEQ_TOKEN);
                    }
                    else if(!isWhitespace(c)){
                        s = state.ERROR;
                    }
                    else{
                        returnToken = new Token (Token.TokenType.ERROR_TOKEN);
                    }
                    break;                
                default:
                    returnToken = new Token (Token.TokenType.ERROR_TOKEN);
                    break;
            }
            if(getNextChar){
                c = (char)inFile.read();
            }
        }
        return returnToken;
    }
    
    private boolean isLetter(char c){
        if((c <= 'Z' && c >= 'A') || (c <= 'z' && c >= 'a')){
            return true;
        }
        return false;
    }
    private boolean isNum(char c){
        if(c <= '9' && c >= '0'){
            return true;
        }
        return false;
    }
    private boolean isWhitespace(char c){
        if(c == ' ' || c == '\n' || c == '\r' || c == '\t'){
            return true;
        }
        return false;
    }
    private boolean isSymbol(char c){
        String s = "" + c;
        return s.matches("\\+|-|\\*|/|<|>|=|!|\\(|\\)|\\{|\\}|\\[|\\]|;|\\,");
        //you're welcome Dr. G
    }
}
