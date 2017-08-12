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



	public static SemanticUtil getInstance(){
        if(singleton ==  null){
            singleton = new SemanticUtil();
            initTypeCompatibility();
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
            throw new Exception("SWITCH CASE COM ERRO. A express„o de tipo: " + e.getType() + ", e valor: " + e.getValue() + " n„o s„o numericos");
        }
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
                        " n√£o existe!");
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
                        " n√£o existe!");
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
            throw new Exception("ERRO: A variavel chamada " +id+ " e com valor "+ expression.getValue()+" n√£o existe!");
        }
        if (!checkValidExistingType(expression.getType())){
            if(!expression.getType().getName().equals("null")) {
                throw new Exception("ERRO: O tipo " + expression.getType().getName()+" atribuido a variavel "+ id + " n√£o existe!");
            }
        }
        Type identifierType = findVariableByIdentifier(id).getType();
        if (!checkTypeCompatibility(identifierType, expression.getType())){
            String exceptionMessage = String.format("ERRO: Tipos incompativeis! %s n√£o e  compativel com %s", identifierType, expression.getType());
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
            throw new Exception("ERRO: A variavel: " + variableName + " n„o foi declarada!");
        }
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
            throw new Exception("ERRO FUNCTION: O mÈtodo "+ functionName +
                    " n„o possui tipo associado");
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
    		 throw new Exception("ERRO FUNCTION: O tipo de retorno: <"+ tmp.getDeclaredReturnType() +"> n„o pode ser associado ao metodo de tipo: <" + e.getType() + ">.");
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
                    throw new Exception("ERRO FUNCTION: O m√©todo "+temp.getName()+" ja foi declarado com um tipo de retorno diferente!");
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
	
	

    
	
    
}
