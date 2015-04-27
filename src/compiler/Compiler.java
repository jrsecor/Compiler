package compiler;

import CMinusScanner.*;
import Parser.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class Compiler {
    
    public static CMinusScanner scanner;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String url = args[0];
        String output = args[0].substring(0, args[0].indexOf(".")) + "Output.ast";
        
        BufferedReader rdr = new BufferedReader(new FileReader(url));
        BufferedWriter write = new BufferedWriter(new FileWriter(output));
        
        scanner = new CMinusScanner(rdr);
        /*
        JFlex Scanner Stuff
        
        Lexer scanner = new Lexer(rdr);
        
        Token t = scanner.yylex();
        while(t != null){
            if(t.getType() == Token.TokenType.ID_TOKEN || t.getType() == Token.TokenType.NUM_TOKEN){
                write.append(t.getType().toString() + ", " + t.getData() + "\r\n");
            }
            else{
                write.append(t.getType().toString() + "\r\n");
            }
            t = scanner.yylex();
        }
        */
        
        /*
        Our Scanner - For Testing purpsoes now
        while(scanner.viewNextToken().getType() != Token.TokenType.EOF_TOKEN){
            Token t = scanner.getNextToken();
            if(t.getType() == Token.TokenType.ID_TOKEN || t.getType() == Token.TokenType.NUM_TOKEN){
                write.append(t.getType().toString() + ", " + t.getData() + "\r\n");
            }
            else{
                write.append(t.getType().toString() + "\r\n");
            }
        }
        */
        
        Program program = new Program();
        try {
            program = Program.ParseProgram();
            program.printProgram(0, write);
        } catch (ParserException ex) {
            System.out.print(ex.getMessage());
        }

        
        
        write.close();
        rdr.close();//Kuz
    }    
}
