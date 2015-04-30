package compiler;

import CMinusScanner.*;
import Parser.*;
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
    public static void main(String[] args) throws FileNotFoundException, 
            IOException, CodeGenerationException, ParserException {
        globalHash = new HashMap();
        String url = args[0];
        String filePrefix = args[0].substring(0, args[0].indexOf("."));
        String output = filePrefix + "Output.ast";
        
        BufferedReader rdr = new BufferedReader(new FileReader(url));
        BufferedWriter write = new BufferedWriter(new FileWriter(output));
        
        scanner = new CMinusScanner(rdr);
        
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


        ControlFlowAnalysis cf = new ControlFlowAnalysis(lowLevelCode);
        cf.performAnalysis();

        // performs DU analysis, annotating the function with the live range of
        // the value defined by each oper (some merging of opers which define
        // same virtual register is done)

        LivenessAnalysis liveness = new LivenessAnalysis(lowLevelCode);
        liveness.performAnalysis();
        liveness.printAnalysis();

        if (genX64Code) {
            int numRegs = 15;
            X64RegisterAllocator regAlloc=new X64RegisterAllocator(lowLevelCode,
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
            X86RegisterAllocator regAlloc = new 
                                X86RegisterAllocator(lowLevelCode, numRegs);
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