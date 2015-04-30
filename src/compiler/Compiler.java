package compiler;

import CMinusScanner.*;
import Parser.*;
import codegen.GenCode;
import dataflow.*;
import java.io.*;
import java.util.HashMap;
import lowlevel.*;
import optimizer.*;
import x64codegen.*;
import x86codegen.*;

/**
 *
 * @author Abraham Church and Jacob Secor
 */
public class Compiler {
    static boolean genX64Code = true;
    public static CMinusScanner scanner;
    public static HashMap globalHash;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, CodeGenerationException, ParserException {
        globalHash = new HashMap();
        String url = args[0];
        String filePrefix = args[0].substring(0, args[0].indexOf("."));
        String output = filePrefix + "Output.ast";
        
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
        
        Program parseTree = Program.ParseProgram();        
        parseTree.printProgram(0, write);
        write.close();
        CodeItem lowLevelCode = parseTree.genLLCode();

        String fileName = filePrefix + ".ll";
        PrintWriter outFile =
                new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        lowLevelCode.printLLCode(outFile);
        outFile.close();

        int optiLevel = 2;
        LowLevelCodeOptimizer lowLevelOpti =
                new LowLevelCodeOptimizer(lowLevelCode, optiLevel);
        lowLevelOpti.optimize();

        fileName = filePrefix + ".opti";
        outFile =
                new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        lowLevelCode.printLLCode(outFile);
        outFile.close();

        if (genX64Code) {
            X64CodeGenerator x64gen = new X64CodeGenerator(lowLevelCode);
            x64gen.convertToX64();
        }
        else {
            X86CodeGenerator x86gen = new X86CodeGenerator(lowLevelCode);
            x86gen.convertToX86();
        }
        fileName = filePrefix + ".x86";
        outFile =
                new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
        lowLevelCode.printLLCode(outFile);
        outFile.close();

//    lowLevelCode.printLLCode(null);

            // simply walks functions and finds in and out edges for each BasicBlock
            ControlFlowAnalysis cf = new ControlFlowAnalysis(lowLevelCode);
            cf.performAnalysis();
//    cf.printAnalysis(null);

            // performs DU analysis, annotating the function with the live range of
            // the value defined by each oper (some merging of opers which define
            // same virtual register is done)
//    DefUseAnalysis du = new DefUseAnalysis(lowLevelCode);
//    du.performAnalysis();
//    du.printAnalysis();

            LivenessAnalysis liveness = new LivenessAnalysis(lowLevelCode);
            liveness.performAnalysis();
            liveness.printAnalysis();

            if (genX64Code) {
                int numRegs = 15;
                X64RegisterAllocator regAlloc = new X64RegisterAllocator(lowLevelCode,
                        numRegs);
                regAlloc.performAllocation();

                lowLevelCode.printLLCode(null);

                fileName = filePrefix + ".s";
                outFile =
                        new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
                X64AssemblyGenerator assembler =
                        new X64AssemblyGenerator(lowLevelCode, outFile);
                assembler.generateX64Assembly();
                outFile.close();
            }
            else {
                int numRegs = 7;
                X86RegisterAllocator regAlloc = new X86RegisterAllocator(lowLevelCode,
                        numRegs);
                regAlloc.performAllocation();

                lowLevelCode.printLLCode(null);

                fileName = filePrefix + ".s";
                outFile =
                        new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
                X86AssemblyGenerator assembler =
                        new X86AssemblyGenerator(lowLevelCode, outFile);
                assembler.generateAssembly();
                outFile.close();
            }
        } 
    }
        
        
        
        
        /*
        
        Program program = new Program();
        try {
            program = Program.ParseProgram();
            program.printProgram(0, write);
            write.close();
            rdr.close();
            //Gen Code
            rdr = new BufferedReader(new FileReader(output));
            //skip Program
            rdr.readLine().trim();
            
            String s = rdr.readLine().trim();
            CodeItem first;
            switch(s){
                case "FunctionDeclaration": 
                    Function f = GenCode.GenCodeFunDecl(rdr);
                    first = f;
                    break;

                case "VariableDeclaration":
                    Data d = GenCode.GenCodeGlobal(rdr);
                    first = d;
                    break;

                default:
                    throw new CodeGenerationException("error");
            }
            while(rdr.ready()){
                s = rdr.readLine().trim();
                switch(s){
                    case "FunctionDeclaration": 
                        Function f = GenCode.GenCodeFunDecl(rdr);
                        CodeItem tail = first.getNextItem();
                        CodeItem prev = first;
                        while(tail != null){
                            prev = tail;
                            tail.getNextItem();
                        }
                        prev.setNextItem(f);                        
                        Operation exit = new Operation(Operation.OperationType.FUNC_EXIT, f.getLastBlock());
                        f.getLastBlock().appendOper(exit);
                        break;

                    case "VariableDeclaration":
                        Data d = GenCode.GenCodeGlobal(rdr);
                        tail = first.getNextItem();
                        prev = first;
                        while(tail != null){
                            prev = tail;
                            tail.getNextItem();
                        }
                        prev.setNextItem(d);
                        break;
                }
            }
            PrintWriter pw = new PrintWriter(base + ".ll");
            
            X64CodeGenerator coder = new X64CodeGenerator(first);
            coder.convertToX64();
            
            X64AssemblyGenerator assembler = new X64AssemblyGenerator(first, pw);            
            assembler.generateX64Assembly();
            
            pw.close();
        } catch (ParserException ex) {
            System.out.print(ex.getMessage());
        }
    }    
}*/
