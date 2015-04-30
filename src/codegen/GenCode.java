package codegen;

import Parser.CodeGenerationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import lowlevel.*;

/**
 *
 * @author Jacob Secor
 */
public class GenCode {
    
    public static Function GenCodeFunDecl(BufferedReader rdr) throws 
                                        IOException, CodeGenerationException{
        String returnType = rdr.readLine().trim();
        int rtrn = returnType.equals("int") ? Data.TYPE_INT : Data.TYPE_VOID;
        String name = rdr.readLine().trim();
        Function f = new Function(rtrn, name);
        String s = rdr.readLine().trim();
        if(s.equals("Param")){
            FuncParam first = GenCodeParam(rdr);
            f.setFirstParam(first);
            FuncParam fpP = f.getfirstParam();
            while(fpP != null){
                f.getTable().put(fpP.getName(), f.getNewRegNum());
                fpP = fpP.getNextParam();
            }
        }
        else{
            f.setFirstParam(new FuncParam());
        }
        // 3) Make Basic Block 0
        f.createBlock0();
        // 4) make basic block (block 0 is sentinal)
        BasicBlock b = new BasicBlock(f);
        f.getFirstBlock().setNextBlock(b);
        b.setPrevBlock(f.getFirstBlock());
        f.setCurrBlock(b);
        GenCodeCompoundStatement(rdr, b);
        return f;
    }
    
    public static FuncParam GenCodeParam(BufferedReader rdr) throws IOException{
        FuncParam first = new FuncParam(0, rdr.readLine().trim());
        FuncParam cp = first;
        while(!rdr.readLine().trim().equals("CompoundStatement")){
            FuncParam p = new FuncParam(0, rdr.readLine().trim());
            cp.setNextParam(p);
            cp = p;            
        }
        return first;
    }
    
    public static void GenCodeCompoundStatement(BufferedReader rdr,
                                              BasicBlock b) throws IOException,
                                                      CodeGenerationException{
        String s = rdr.readLine();
        int depth = countTabs(s);
        boolean breakOut = false;
        while(!breakOut && rdr.ready() && countTabs(s) >= depth){
            s = s.trim();
            switch(s){
                case "IfStatement":
                    GenCodeIfStatement(rdr, b);
                    break;

                case "VariableDeclaration":
                    GenCodeVariableDecl(rdr, b);
                    break;

                case "ExpressionStatement":
                    GenCodeExpressionStatement(rdr, b);
                    break;

                case "CompoundStatement":
                    GenCodeCompoundStatement(rdr, b);
                    break;

                case "WhileStatement":
                    GenCodeWhileStatement(rdr, b);
                    break;

                case "ReturnStatement":
                    GenCodeReturnStatement(rdr, b);
                    break;

                default:
                    breakOut = true;
                    break;
            }            
            s = rdr.readLine();
        }
    }

    private static void GenCodeIfStatement(BufferedReader rdr,
                                              BasicBlock b) throws IOException,
                                                      CodeGenerationException {
        //skip "BinaryExpression"
        rdr.readLine().trim();
        GenCodeBinaryExpression(rdr, b);
        
    }

    private static void GenCodeVariableDecl(BufferedReader rdr,
                                              BasicBlock b) throws IOException{
        //add to symbol table
        b.getFunc().getTable().put(rdr.readLine().trim(), b.getFunc().getNewRegNum());
    }

    private static void GenCodeExpressionStatement(BufferedReader rdr,
                    BasicBlock b) throws IOException, CodeGenerationException{
        GenCodeExpression(rdr, b);
    }

    private static void GenCodeWhileStatement(BufferedReader rdr,
                                              BasicBlock b) {
        
    }

    private static void GenCodeReturnStatement(BufferedReader rdr,
                    BasicBlock b) throws IOException, CodeGenerationException{
        Operation op = new Operation(Operation.OperationType.RETURN, b);
        Operand src1 = GenCodeExpression(rdr, b);
        op.setSrcOperand(0, src1);
        Operand dest = new Operand(Operand.OperandType.REGISTER,
                b.getFunc().getNewRegNum());
        op.setDestOperand(0, dest);
    }

    private static Operand GenCodeBinaryExpression(BufferedReader rdr,
                                              BasicBlock b) throws IOException,
                                                     CodeGenerationException {
        Operation.OperationType type;
        switch(rdr.readLine().trim()){
            case "<":
                type = Operation.OperationType.LT;
                break;
            case "<=":
                type = Operation.OperationType.LTE;
                break;
            case ">":
                type = Operation.OperationType.GT;
                break;
            case ">=":
                type = Operation.OperationType.GTE;
                break;
            case "==":
                type = Operation.OperationType.EQUAL;
                break;
            case "!=":
                type = Operation.OperationType.NOT_EQUAL;
                break;
            default:
                throw new CodeGenerationException("Unexpected token at "
                        + "GenCodeBinaryExpression");
        }
        Operation op = new Operation(type, b);
        Operand src1 = GenCodeExpression(rdr, b);
        Operand src2 = GenCodeExpression(rdr, b);
        Operand dest = new Operand(Operand.OperandType.REGISTER,
                b.getFunc().getNewRegNum());
        
        //Set pointers
       op.setPrevOper(b.getLastOper());
        if(b.getLastOper() == null){
            b.setFirstOper(op);
            b.setLastOper(op);
        }
        else{
            b.getLastOper().setNextOper(op);
            b.appendOper(op);
        }        
        return dest;
    }

    private static Operand GenCodeExpression(BufferedReader rdr, BasicBlock b)
            throws IOException, CodeGenerationException {
        String s = rdr.readLine();
        String trimmed = s.trim();
        switch(trimmed){
            case "ArithmeticExpression":
                return GenCodeArithmeticExpression(rdr, b);
                
            case "AssignExpression":
                return GenCodeAssignExpression(rdr, b);
                
            case "BinaryExpression":
                return GenCodeBinaryExpression(rdr, b);
                
            default:
                if(trimmed.matches("(0|1|2|3|4|5|6|7|8|9)+")){
                    return new Operand(Operand.OperandType.INTEGER,
                                                        Integer.parseInt(trimmed));
                }
                else if(b.getFunc().getTable().containsKey(trimmed)){
                    return new Operand(Operand.OperandType.REGISTER,
                    b.getFunc().getTable().get(trimmed));
                }
                else if(trimmed.contains("(")){//calls
                    return GenCodeCall(rdr, b, s);
                }
                else{
                    return new Operand(Operand.OperandType.STRING, trimmed);
                }
        }
    }

    private static Operand GenCodeArithmeticExpression(BufferedReader rdr, BasicBlock b) throws IOException, CodeGenerationException {
        Operation op;
        String math = rdr.readLine().trim();
        
        //Source 1
        Operand src1 = GenCodeExpression(rdr, b);
        Operand src2 = GenCodeExpression(rdr, b);
        boolean src1Imm = src1.getType() == Operand.OperandType.INTEGER;
        boolean src2Imm = src2.getType() == Operand.OperandType.INTEGER;;
        switch(math){
            case "+":
                if(src1Imm || src2Imm){
                    op = new Operation(Operation.OperationType.ADD_I, b);
                }
                else{
                    op = new Operation(Operation.OperationType.X64_ADD_Q, b);
                }
                break;
                
            case "-":
                if(src1Imm || src2Imm){
                    op = new Operation(Operation.OperationType.SUB_I, b);
                }
                else{
                    op = new Operation(Operation.OperationType.X64_SUB_Q, b);
                }
                break;
            case "*":
                op = new Operation(Operation.OperationType.MUL_I, b);
                break;
                
            case "/":
                op = new Operation(Operation.OperationType.DIV_I, b);
                break;
                
            default:
                throw new CodeGenerationException("Error in Arithmetic Gen Code");
        }
        op.setSrcOperand(0, src1);
        op.setSrcOperand(1, src2);
        Operand dest = new Operand(Operand.OperandType.REGISTER,
                b.getFunc().getNewRegNum());
        op.setDestOperand(0, src2);
        
        //Set pointers
        op.setPrevOper(b.getLastOper());
        if(b.getLastOper() == null){
            b.setFirstOper(op);
            b.setLastOper(op);
        }
        else{
            b.getLastOper().setNextOper(op);
            b.appendOper(op);
        }        
        return dest;
    }

    private static Operand GenCodeAssignExpression(BufferedReader rdr, BasicBlock b) throws IOException, CodeGenerationException {
        Operation op = new Operation(Operation.OperationType.ASSIGN, b);
        //right now we only do variables not arrays
        Operand dest;
        String name = rdr.readLine().trim();
        if(b.getFunc().getTable().containsKey(name)){//local variable
            dest = new Operand(Operand.OperandType.REGISTER,
            b.getFunc().getTable().get(name));
        }
        else{//global variable
            dest = new Operand(Operand.OperandType.STRING, name);
        }
        op.setDestOperand(0, dest);
        Operand src = GenCodeExpression(rdr, b);      
        op.setSrcOperand(0, src);
        
        //Set pointers
        op.setPrevOper(b.getLastOper());
        b.getLastOper().setNextOper(op);
        b.appendOper(op);
        
        return dest;
    }

    public static Data GenCodeGlobal(BufferedReader rdr) throws IOException {
        return new Data(Data.TYPE_INT, rdr.readLine().trim());
    }

    public static Operand GenCodeCall(BufferedReader rdr, BasicBlock b, 
                    String name)throws IOException, CodeGenerationException {
        Operation op = new Operation(Operation.OperationType.CALL, b);
        String s = rdr.readLine();
        int depth1 = countTabs(name);
        int depth2 = countTabs(s);
        int paramCount = 0;
        for(int i = 0; depth2 > depth1; i++){//params
            Operand src = GenCodeExpression(rdr, b);
            op.setSrcOperand(i, src);
            paramCount++;
            s = rdr.readLine();
            depth2 = countTabs(s);
        }
        Operand dest = new Operand(Operand.OperandType.STRING, name);
        op.setDestOperand(0, dest);
        op.addAttribute(new Attribute("numParams", Integer.toString(paramCount)));
        return dest;
    }
    
    private static int countTabs(String s){
        int count = 0;
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == '\t'){
                count++;
            }
        }
        return count;
    }
}
