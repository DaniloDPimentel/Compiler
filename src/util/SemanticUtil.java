package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SemanticUtil {

    private static SemanticUtil singleton;
    private static Map<String, List<String>> tiposCompativeis = new HashMap<String, List<String>>();
    private List<Type> tiposCriados = new ArrayList<Type>();

    private List<Variable> listVariables = new ArrayList<Variable>();
    private List<Object> listParams = new ArrayList<Object>();



    private Stack<VariablesScope> variablesStackScope = new Stack<VariablesScope>();
    private HashMap<String,Variable> variables = new HashMap<String,Variable>();
    private ArrayList<Function> functions = new ArrayList<Function>();
    public static boolean contextFor;
    public static CodeGenerator codeGenerator;
    public int forCounter = 0;
    
    public CodeGenerator getCodeGenerator() {
    	return codeGenerator;
    }


	public static SemanticUtil getInstance(){
        if(singleton ==  null){
            singleton = new SemanticUtil();
            initTypeCompatibility();
            codeGenerator = new CodeGenerator();
        }
        return singleton;
    }
	
	private final List<Type> listaTipos = new ArrayList<Type>(){{
		add(new Type("boolean"));
		add(new Type("char"));
		add(new Type("byte"));
        add(new Type("short") );
        add(new Type("int") );
        add(new Type("float"));
        add(new Type("long"));
        add(new Type("double"));
        add(new Type("string"));
    }};

    private static void initTypeCompatibility(){
        List<String> doubleCompTypes = new ArrayList<String>();
        doubleCompTypes.add("int");
        doubleCompTypes.add("float");
        doubleCompTypes.add("double");
        doubleCompTypes.add("long");

        List<String> floatCompTypes = new ArrayList<String>();
        floatCompTypes.add("int");
        floatCompTypes.add("float");
        floatCompTypes.add("long");

        List<String> longCompTypes = new ArrayList<String>();
        longCompTypes.add("long");
        longCompTypes.add("int");

        List<String> intCompTypes = new ArrayList<String>();
        intCompTypes.add("int");
        intCompTypes.add("Integer");

        List<String> stringCompTypes = new ArrayList<String>();
        stringCompTypes.add("int");
        stringCompTypes.add("double");
        stringCompTypes.add("long");
        stringCompTypes.add("float");
        stringCompTypes.add("char");
        stringCompTypes.add("null");
        stringCompTypes.add("boolean");

        tiposCompativeis.put("double", doubleCompTypes);
        tiposCompativeis.put("float", floatCompTypes);
        tiposCompativeis.put("long", longCompTypes);
        tiposCompativeis.put("int", intCompTypes);
        tiposCompativeis.put("string", stringCompTypes);
        tiposCompativeis.put("String", stringCompTypes);
    }
    
    public void checkSwitchExpression(Expression e) throws Exception{
    	
    	if (e == null){
    		return;
    	}
    	if (!(e.getType().getName().equals("int")
				||e.getType().getName().equals("long")
				||e.getType().getName().equals("Integer"))) {
            throw new Exception("SWITCH CASE COM ERRO. A expressão de tipo: " + e.getType() + ", e valor: " + e.getValue() + " não são numericos");
        }

    }   
    
    public void checkSwitchExpression(Object e) throws Exception{
    	if (e == null){
    		return;
    	}
    	
    	if (!checkVariableExistence(e.toString())){
            throw new Exception("Variavel nao declarada: " + e );
    	}
    }
    
    public boolean checkNumericExpression(Expression e) throws Exception{
    	if (!e.isNumeric()) {
			throw new Exception("A expressão " + e.toString() + "não é do tipo numérico.");
		}
    	return true;
    }
    
    public boolean checkNumericExpression(Expression e1, Expression e2) throws Exception{
        if(e1 != null && e1.isNumeric()){
            if(e2 != null && e2.isNumeric()){
                return true;
            }
        }else if(isStringExpression(e1,e2)){
            return true;
        }
        throw new Exception("ERRO: A expressão '"+ e1.getValue()+ "' com tipo '" + e1.getType().getName() +
                "' e/ou a expressão " + e2.getValue() + " com tipo '"+ e2.getType().getName()+"' não é expressão numérica ou entre string");
    }
    
    public boolean isStringExpression(Expression e1, Expression e2) throws Exception {
        if(e1 != null && e1.getType().getName().equalsIgnoreCase("String")){
            return true;
        }
        if(e2 != null && e2.getType().getName().equalsIgnoreCase("String")){
            return true;
        }
        return false;
    }
    
    public boolean checkExpression(Expression e1, Expression e2) throws Exception{
    	if (!e1.getType().equals(e2.getType())) {
    		throw new Exception("EXPRESSÃO COM ERRO. A expressão de tipo: " + e1.getType() + "deve ter o mesmo tipo " + e2.getValue());
		}
    	return true;
    }
    
    public Expression getExpression(Expression e1, Operation no, Expression e2) throws Exception {
        Register r;

        if (e2 == null || checkTypeCompatibility(e1.getType(), e2.getType()) || checkTypeCompatibility(e2.getType(), e1.getType())){
            switch (no) {
                case AND:
                    return new Expression(new Type("boolean"));
                case OR:
                    return new Expression(new Type("boolean"));
                case GTEQ:
                    if(!contextFor){
                    codeGenerator.generateSUBCode();
                    codeGenerator.generateBLTZCode(3);
                    r = codeGenerator.generateLDCode(new Expression(new Type(
                            "boolean"), "1"));
                    codeGenerator.generateBRCode(2);
                    codeGenerator.generateLDCode(r, new Expression(new Type(
                            "boolean"), "0"));
                    }

                    return new Expression(new Type("boolean"), e1.getValue()+" "+no+" "+e2.getValue());
                case EQEQ:
                    if(!contextFor) {
                        codeGenerator.generateBEQCode(3);
                        r = codeGenerator.generateLDCode(new Expression(new Type(
                                "boolean"), "0"));
                        codeGenerator.generateBRCode(2);
                        codeGenerator.generateLDCode(r, new Expression(new Type(
                                "boolean"), "1"));
                    }
                    return new Expression(new Type("boolean"), e1.getValue()+" "+no+" "+e2.getValue());

                case LTEQ:
                    if(!contextFor) {
                        codeGenerator.generateSUBCode();
                        codeGenerator.generateBGTZCode(3);
                        r = codeGenerator.generateLDCode(new Expression(new Type(
                                "boolean"), "1"));
                        codeGenerator.generateBRCode(2);
                        codeGenerator.generateLDCode(r, new Expression(new Type(
                                "boolean"), "0"));
                    }
                    return new Expression(new Type("boolean"), e1.getValue()+" "+no+" "+e2.getValue());
                case LT:
                    if(!contextFor) {
                        codeGenerator.generateSUBCode();
                        if (e1.getContext() == "for") {
                            codeGenerator.generateForCondition("BGEQZ", "forSTRINGCHAVEQUENAOVAIEXISTIRNOUTROCANTOTOP"+forCounter);
                        }
                        codeGenerator.generateBGEQZCode(3);
                        r = codeGenerator.generateLDCode(new Expression(new Type(
                                "boolean"), "1"));
                        codeGenerator.generateBRCode(2);
                        codeGenerator.generateLDCode(r, new Expression(new Type(
                                "boolean"), "0"));
                    }
                    return new Expression(new Type("boolean"), e1.getValue()+" "+no+" "+e2.getValue());
                case GT:
                    if(!contextFor) {
                        codeGenerator.generateSUBCode();
                        codeGenerator.generateBLEQZCode(3);
                        r = codeGenerator.generateLDCode(new Expression(new Type(
                                "boolean"), "1"));
                        codeGenerator.generateBRCode(2);
                        codeGenerator.generateLDCode(r, new Expression(new Type(
                                "boolean"), "0"));

                    }
                    return new Expression(new Type("boolean"), e1.getValue()+" "+no+" "+e2.getValue());
                case NOTEQ:
                    if(!contextFor) {
                        codeGenerator.generateBEQCode(3);
                        r = codeGenerator.generateLDCode(new Expression(new Type(
                                "boolean"), "1"));
                        codeGenerator.generateBRCode(2);
                        codeGenerator.generateLDCode(r, new Expression(new Type(
                                "boolean"), "0"));
                    }
                    return new Expression(new Type("boolean"), e1.getValue()+" "+no+" "+e2.getValue());
                case NOT:
                    return new Expression(new Type("boolean"));
                case XOREQ:
                    return new Expression(new Type("boolean"));
                case XOR:
                    return new Expression(new Type("boolean"));
                case OROR:
                    return new Expression(new Type("boolean"));
                case ANDAND:
                    return new Expression(new Type("boolean"));
                case ANDEQ:
                    return new Expression(new Type("boolean"));
                case OREQ:
                    return new Expression(new Type("boolean"));
                case OROREQ:
                    return new Expression(new Type("boolean"));
                case MINUS:
                    if(!contextFor) {
                        codeGenerator.generateSUBCode();
                    }
                    return new Expression(getMajorType(e1.getType(), e2.getType()), e1.getValue()+" "+no+" "+e2.getValue());
                case MULT:
                    if(!contextFor) codeGenerator.generateMULCode();
                    return new Expression(getMajorType(e1.getType(), e2.getType()), e1.getValue()+" "+no+" "+e2.getValue());
                case MOD:
                    if(!contextFor) codeGenerator.generateMODCode();
                    return new Expression(getMajorType(e1.getType(), e2.getType()), e1.getValue()+" "+no+" "+e2.getValue());
                case PLUS:
                    if(!contextFor) codeGenerator.generateADDCode();
                    return new Expression(getMajorType(e1.getType(), e2.getType()), e1.getValue()+" "+no+" "+e2.getValue());
                case DIV:
                    if(!contextFor) codeGenerator.generateDIVCode();
                    return new Expression(getMajorType(e1.getType(), e2.getType()), e1.getValue()+" "+no+" "+e2.getValue());
                case DIVEQ:
                    return new Expression(getMajorType(e1.getType(), e2.getType()));
                case PLUSEQ:
                    return new Expression(getMajorType(e1.getType(), e2.getType()));
                case MINUSEQ:
                    return new Expression(getMajorType(e1.getType(), e2.getType()));
                case MULTEQ:
                    return new Expression(getMajorType(e1.getType(), e2.getType()));
                case PLUSPLUS:
                    if(!contextFor) {
                        codeGenerator.generateADDCode("1");
                        codeGenerator.generateSTCode(e1);
                    }
                    return new Expression(e1.getType(), e1.getValue()+" "+no);
                case MINUSMINUS:
                    if(!contextFor) {
                        codeGenerator.generateSUBCode("1");
                        codeGenerator.generateSTCode(e1);

                    }
                    return new Expression(e1.getType(), e1.getValue()+" "+no);
                default:
                    throw new Exception("ERRO: A operação '"+ no+ "' não existe!");

            }
        }

        throw new Exception("ERRO: Operação formada pela expressão '"+e1.getValue()+" "+no+" " +e2.getValue() +"' não é permitida!");
    }
    
    private Type getMajorType(Type type1, Type type2) {
        return tiposCompativeis.get(type1.getName()).contains(type2.getName()) ? type1: type2;
    }
    
    public void addVariableType(Type type) throws Exception{
        for (Variable variable : listVariables) {
            variable.setType(type);
            addVariable(variable);
        }

        listVariables = new ArrayList<Variable>();
    }
    
    public void addVariable(Variable variable) throws Exception{
    	System.out.println("Variable: "+ variable.identifier);
    	System.out.println("Tipo: "+ variable.getType());
    	
    	if (!(variable.value == null)){
    		variable.type = variable.getValue().getType();
    	}
    	System.out.println("Tipo2: "+ variable.getType());

    	
        if(variablesStackScope.isEmpty()){
            checkVariableGlobal(variable);
            variables.put(variable.getIdentifier(),variable);
        }else{
            checkVariableLocal(variable);
            getCurrentScope().addVariable(variable);
        }

        if (variable.getValue() != null){
            checkVariableAttribution(variable.getIdentifier(), variable.getValue());
        }
    }
    
    private void checkVariableGlobal(Variable variable) throws Exception{
        if (checkVariableExistenceGlobal(variable.getIdentifier())){
            throw new Exception("ERRO VARIABLE. A variavel de nome " + variable.getIdentifier() + " e tipo " + variable.getType().getName() +
                    " ja existe!");
        }
        if (!checkValidExistingType(variable.getType())){
            if(!variable.getType().getName().equals("null")) {
                throw new Exception("ERRO VARIABLE. O tipo " + variable.getType().getName() + " da variavel "+ variable.getIdentifier()+
                        " nÃ£o existe!");
            }
        }
    }
    
    public boolean checkVariableExistenceGlobal(String variableName) {
        return variables.get(variableName) != null ? true : false;
    }
    
    public boolean checkValidExistingType(Type type) {
        return listaTipos.contains(type) || tiposCriados.contains(type);
    }
    
    private void checkVariableLocal(Variable variable) throws Exception{
        if (checkVariableExistenceLocal(variable.getIdentifier())){
            throw new Exception("ERRO VARIABLE. A variavel de nome " + variable.getIdentifier() + " e tipo " + variable.getType().getName() +
                    " ja existe!");
        }
        if (!checkValidExistingType(variable.getType())){
            if(!variable.getValue().getType().getName().equals("null")){
                throw new Exception("ERRO VARIABLE. O tipo " + variable.getType().getName() + " da variavel "+ variable.getIdentifier()+
                        " nÃ£o existe!");
            }
        }
    }
    
    public boolean checkVariableExistenceLocal(String variableName) {
        if(!variablesStackScope.isEmpty() && getCurrentScope().getVariable().get(variableName) != null){
            return true;
        }else{
            return false;
        }
    }
    
    public void checkVariableAttribution(String id, Expression expression) throws Exception{
        if (!checkVariableExistence(id)){
            throw new Exception("ERRO: A variavel chamada " +id+ " e com valor "+ expression.getValue()+" nÃ£o existe!");
        }
        if (!checkValidExistingType(expression.getType())){
            if(!expression.getType().getName().equals("null")) {
                throw new Exception("ERRO: O tipo " + expression.getType().getName()+" atribuido a variavel "+ id + " nÃ£o existe!");
            }
        }
        Type identifierType = findVariableByIdentifier(id).getType();
        if (!checkTypeCompatibility(identifierType, expression.getType())){
            String exceptionMessage = String.format("ERRO: Tipos incompativeis! %s nÃ£o e  compativel com %s", identifierType, expression.getType());
            throw new Exception(exceptionMessage);
        }
    }
    
    public boolean checkVariableExistence(String variableName) {
        if(!variablesStackScope.isEmpty() && getCurrentScope().getVariable().get(variableName) != null){
            return true;
        }else if(variables.get(variableName) != null){
            return true;
        }else{
            return false;
        }
    }
    
    public void validateVariableName(String variableName) throws Exception{
        if (!checkVariableExistence(variableName)){
            throw new Exception("ERRO: A variavel: " + variableName + " não foi declarada!");
        }
    }
    
    public Object validateCallMethod(Expression e, Expression eop) throws Exception{
        Object result = null;
    	
    	if (true){
            throw new Exception("ERRO: A variavel: foi declarada!");
        }
        return result;
    }
    
    public Variable findVariableByIdentifier(String variableName){
        if(!variablesStackScope.isEmpty() && getCurrentScope().getVariable().get(variableName) != null){
            return getCurrentScope().getVariable().get(variableName);
        }else{
            return variables.get(variableName);
        }

    }
    
    public boolean checkTypeCompatibility(Type leftType, Type rightType) {
        if (leftType.equals(rightType)){
            return true;
        } else {
            List<String> tipos = tiposCompativeis.get(leftType.getName());
            if(tipos == null) return false;
            return tipos.contains(rightType.getName());
        }
    }
    
    public VariablesScope getCurrentScope() {
        return variablesStackScope.peek();
    }
    
    private void createNewScope(VariablesScope scope) {
    	variablesStackScope.push(scope);
    }
    
    public void checkMethod(Type type, String functionName, ArrayList<Param> params) throws Exception {

    	System.out.println("funcao: "+type);
    	
        if(type == null){
            throw new Exception("ERRO FUNCTION: O método "+ functionName +
                    " não possui tipo associado");
        }
        Function temp = new Function(functionName, params);
        temp.setDeclaredReturnedType(type);
        if(checkFunctionExistence(temp)){
            if(params != null){
                checkExistingParameter(params);
            }
            String keyFunc = functionName + " ";
            if(params != null) {
                for (int i = 0; i < params.size(); i++) {
                	Variable p = (Variable) params.get(i);
                	keyFunc += p.getType().getName();
                }
            }
            //codeGenerator.addFunctionAddress(keyFunc);
            addFunctionAndNewScope(temp);
        }
    }
    
    public void checkMethodReturn(Expression e) throws Exception{
    	
    	Function tmp = new Function("", null);
    	
    	for (Function f : functions){
    		tmp = f;
    	}
    	    	
    	if (!e.getType().equals(tmp.getDeclaredReturnType()))
    		 throw new Exception("ERRO FUNCTION: O tipo de retorno: <"+ e.getType() +"> não pode ser associado ao metodo de tipo: <" + tmp.getDeclaredReturnType() + ">.");
    }
    
    
    
    public void addFunctionAndNewScope(Function f) throws Exception {
        functions.add(f);
        createNewScope(f);
        if(f.getParams() != null) {
            
        	for (int i = 0; i < f.getParams().size(); i++) {
            	Variable p = (Variable) f.getParams().get(i);
            	addVariable(p);
            }

        }
    }
    
    private void checkExistingParameter(ArrayList<Param> params) throws Exception {
        for(int i=0; i<params.size();i++){
            for(int k=i+1;k<params.size();k++){
                if( ((Variable)params.get(i)).getIdentifier().equals(((Variable)params.get(k)).getIdentifier())){
                    throw new Exception("ERRO PARAMETERs: O parametro: "+ ((Variable)params.get(k)).getIdentifier() + " ja foi definido.");
                }
            }
        }
    }
	
    public boolean checkFunctionExistence(Function temp) throws Exception {
        for(Function fun : functions){
            if(fun.getName().equals(temp.getName())) {
                if(!fun.getDeclaredReturnType().getName().equals(temp.getDeclaredReturnType().getName())){
                    throw new Exception("ERRO FUNCTION: O mÃ©todo "+temp.getName()+" ja foi declarado com um tipo de retorno diferente!");
                }
                if(temp.equals(fun)){
                    throw new Exception("ERRO FUNCTION: O metodo " + temp.getName() + " ja foi declarado com esses mesmos parametros!");
                }

            }
        }
        return true;
    }
    
	public static <T> ArrayList<T> createList(T... p) {
		ArrayList<T> list = new ArrayList<T>();
		for (int i = 0 ; i < p.length ; i++) {
			list.add(p[i]);
		}

		return list;
	}
	
    public <T> List<Object> addParamList(T... p) {
    	
		for (int i = 0 ; i < p.length ; i++) {
			listParams.add(p[i]);
		}
		
		return listParams;
	}
	
    public boolean checkObject(Object e) throws Exception {
		boolean result = true;
    	try{
    		( (Expression) e).getType();
		}
    	catch (Exception a){
			result = false;
		}
    	    	
    	return result;
    	
    }
	
    public boolean checkObjectReturn(Object e) throws Exception {
		boolean result = true;
    	try{
    		( (Expression) e).getType();
		}
    	catch (Exception a){
			result = false;
		}
    	
    	checkVariableReturn(e);
    	
    	return result;
    	
    }
    
    public void  checkVariableReturn(Object e) throws Exception {
		boolean result = true;

    	Variable var = findVariableByIdentifier(e.toString());
    	
    	Function tmp = null;
    	for (Function f : functions){
    		tmp = f;
    	}
    	    	
    	if (!var.getType().equals(tmp.getDeclaredReturnType()))
    		 throw new Exception("ERRO FUNCTION: O tipo de retorno: <"+ var.getType() +"> não pode ser associado ao metodo de tipo: <" + tmp.getDeclaredReturnType() + ">.");

    	
    }
    
    public void addType(Type type){
        if (type != null){
            if(type.getName().contains(".")){
                String[] typeNames = type.getName().split(".");
                String typeName = typeNames[typeNames.length-1];
                type.setName(typeName);
            }
        }

        if(!tiposCriados.contains(type)){
        	tiposCriados.add(type);
            List<String> tipos = new ArrayList<String>();
            tipos.add(type.getName());
            tiposCompativeis.put(type.getName(), tipos);
        }
    }
    
    public void addSupertype(String className, String superClassName) throws Exception{
        if (superClassName != null) {
            if (tiposCompativeis.containsKey(superClassName)){
                tiposCompativeis.get(superClassName).add(className);
                return;
            }

            throw new Exception("ERRO: A super classe " + superClassName + "não existe!");
        }
    }
    

}
