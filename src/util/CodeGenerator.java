package util;

import java.util.HashMap;
import java.util.Map;

public class CodeGenerator {
	
	private int labels;
	private Register[] registers;
	private int register;
	private String assemblyCode;
	private Map<String, Integer> functionAddress;
	
	public CodeGenerator() {
        this.labels = 100;
        this.register = -1;
        this.registers = Register.values();
        this.assemblyCode = initAssemblyCode();
        this.functionAddress = new HashMap<String, Integer>();
    }
	
	private String initAssemblyCode() {
        return "100: LD SP, #4000\n";
    }

	public void generateSUBCode() {
        labels += 8;
        Register one = registers[register - 1];
        Register two = allocateRegister();

        register++;
        Register result = allocateRegister();
        addCode(labels + ": SUB " + result + ", " + one + ", " + two);
    }

    public void generateSUBCode(Register result, Register one, Expression exp) {
        labels += 8;
        addCode(labels + ": SUB " + result + ", " + one + ", #" + exp.getAssemblyValue());
    }

    public void generateSUBCode(String cons) {
        labels += 8;
        Register one = registers[register];
        register++;
        Register result = allocateRegister();
        addCode(labels + ": SUB " + result + ", " + one + ", #" + cons);
    }

    public void generateSUBCode(Register result, Register one, String cons) {
        labels += 8;
        addCode(labels + ": SUB " + result + ", " + one + ", " + cons);
    }
    
    public void generateBLTZCode(int br) {
        labels += 8;
        int jump = (br * 8) + labels;

        Register current = allocateRegister();
        addCode(labels + ": BLTZ " + current + ", " + jump);
    }
    
    public Register generateLDCode(Expression expression) {
        Register r = null;
        if ((expression.getAssemblyValue() != null) && (expression.getValue() != null)) {
            register++;
            labels += 8;
            r = allocateRegister();
            addCode(labels + ": LD " + r + ", #" + expression.getAssemblyValue());
        }
        return r;
    }
    
    public void generateBRCode(Integer address) {
        labels += 8;
        addCode(labels + ": BR " + address);
    }

    public void generateBRCode(Register register) {
        labels += 8;
        addCode(labels + ": BR " + register);
    }

    public Register generateLDCode(Variable var) {

        Register r = null;
        if (var.getIdentifier() != null) {
            register++;
            labels += 8;
            r = allocateRegister();
            addCode(labels + ": LD " + r + ", " + var.getIdentifier());
        }
        return r;
    }

    public Register generateLDCode(Register r, Expression expression) {
        if ((expression.getAssemblyValue() != null) && (expression.getValue() != null)) {
            labels += 8;
            addCode(labels + ": LD " + r + ", #" + expression.getAssemblyValue());
        }
        return r;
    }
    
    public void generateBGTZCode(int br) {
        labels += 8;
        int jump = (br * 8) + labels;

        Register current = allocateRegister();
        addCode(labels + ": BGTZ " + current + ", " + jump);
    }
    
    public void generateBEQCode(int br) {
        labels += 8;
        Register r1 = registers[register-1];
        Register r2 = allocateRegister();

        int jump = (br * 8) + labels;

        addCode(labels + ": BEQ " + r1 + ", " + r2 + ", " + jump);
    }
    
    public void generateForCondition(String op, String jump){
        labels += 8;
        Register current = allocateRegister();
        addCode(labels + ": " +op + " " + current + ", " + jump);
    }
    
    public void generateBGEQZCode(int br) {
        labels += 8;
        int jump = (br * 8) + labels;

        Register current = allocateRegister();
        addCode(labels + ": BGEQZ " + current + ", " + jump);
    }
    
    public void generateBLEQZCode(int br) {
        labels += 8;
        int jump = (br * 8) + labels;

        Register current = allocateRegister();
        addCode(labels + ": BLEQZ " + current + ", " + jump);
    }
    
    public void generateMULCode() {
        labels += 8;

        Register one = registers[register - 1];
        Register two = allocateRegister();

        register++;
        Register result = allocateRegister();
        addCode(labels + ": MUL " + result + ", " + one + ", " + two);
    }
    
    public void generateMULCode(Register result, Register one, Expression exp) {
        labels += 8;
        addCode(labels + ": MUL " + result + ", " + one + ", #" + exp.getValue());
    }
    
    public void generateMODCode() {
        labels += 8;
        Register one = registers[register - 1];
        Register two = allocateRegister();

        register++;
        Register result = allocateRegister();
        addCode(labels + ": MOD " + result + ", " + one + ", " + two);
    }

    /*public void generateBRCode(String s) {
        labels += 8;
        addCode(labels + ": BR " + s);
    }*/
    
    public Register allocateRegister(){
        try {
            Register allocated = registers[register];
            return allocated;
        } catch (Exception e) {
            register++;
            return allocateRegister();
        }
    }
    
    public void generateADDCode() {
        labels += 8;
        Register one = registers[register - 1];
        Register two = allocateRegister();

        register++;
        Register result = allocateRegister();
        addCode(labels + ": ADD " + result + ", " + one + ", " + two);
    }

    public void generateADDCode(String cons) {
        labels += 8;
        Register one = registers[register];
        register++;
        Register result = allocateRegister();
        addCode(labels + ": ADD " + result + ", " + one + ", #" + cons);
    }

    public void generateADDCode(Register result, Register one, String cons) {
        labels += 8;
        addCode(labels + ": ADD " + result + ", " + one + ", " + cons);
    }

    public void generateADDCode(Register result, Register one, Expression exp) {
        labels += 8;
        addCode(labels + ": ADD " + result + ", " + one + ", #" + exp.getAssemblyValue());
    }
    
    public void generateDIVCode() {
        labels += 8;

        Register one = registers[register - 1];
        Register two = allocateRegister();

        register++;
        Register result = allocateRegister();
        addCode(labels + ": DIV " + result + ", " + one + ", " + two);
    }
    
    public void generateSTCode(Variable variable) {
        labels += 8;
        addCode(labels + ": ST " + variable.getIdentifier() + ", " + allocateRegister());
        this.register = -1;
    }

    public void generateSTCode(Register one, Expression exp) {
        labels += 8;
        addCode(labels + ": ST " + one + ", " + exp.getAssemblyValue());
        this.register = -1;
    }

    public void generateSTCode(Expression exp) {
        labels += 8;
        addCode(labels + ": ST " + exp.getAssemblyValue() + ", " + allocateRegister());
        this.register = -1;
    }
    
    public void addCode(String assemblyString) {
        assemblyCode += assemblyString + "\n";
    }

}
