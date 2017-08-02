package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SemanticUtil {

    private static SemanticUtil singleton;
    private static Map<String, List<String>> tiposCompativeis = new HashMap<String, List<String>>();
    private List<Variable> listVariables = new ArrayList<Variable>();
    private Stack<VariablesScope> variablesStackScope = new Stack<VariablesScope>();
    private HashMap<String,Variable> variables = new HashMap<String,Variable>();



	public static SemanticUtil getInstance(){
        if(singleton ==  null){
            singleton = new SemanticUtil();
            initTypeCompatibility();
        }
        return singleton;
    }
	
	private final List<Type> BASIC_TYPES = new ArrayList<Type>(){{
        add(new Type("int") );
        add(new Type("float"));
        add(new Type("double"));
        add(new Type("long"));
        add(new Type("char"));
        add(new Type("void"));
        add(new Type("String"));
        add(new Type("boolean"));
        add(new Type("Object"));
        add(new Type("Integer"));
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
        tiposCompativeis.put("Integer", intCompTypes);
        tiposCompativeis.put("string", stringCompTypes);
        tiposCompativeis.put("String", stringCompTypes);
    }
    
    public void checkSwitchExpression(Expression e) throws Exception{
        if (!e.isNumeric()) {
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
        if(variablesStackScope.isEmpty()){
            checkVariableGlobal(variable);
            variables.put(variable.getIdentifier(),variable);
        }else{
            checkVariableLocal(variable);
            getCurrentScope().addVariable(variable);
        }

        if (variable.getValue() != null){
            //checkVariableAttribution(variable.getIdentifier(), variable.getValue());
        }
    }
    
    private void checkVariableGlobal(Variable variable) throws Exception{
//        if (checkVariableExistenceGlobal(variable.getIdentifier())){
//            throw new Exception("ERRO VARIABLE. A variavel de nome " + variable.getIdentifier() + " e tipo " + variable.getType().getName() +
//                    " ja existe!");
//        }
//        if (!checkValidExistingType(variable.getType())){
//            if(!variable.getValue().getType().getName().equals("null")) {
//                throw new InvalidTypeException("ERRO VARIABLE. O tipo " + variable.getType().getName() + " da variavel "+ variable.getIdentifier()+
//                        " n√£o existe!");
//            }
//        }
    }
    
    private void checkVariableLocal(Variable variable) throws Exception{
//        if (checkVariableExistenceLocal(variable.getIdentifier())){
//            throw new Exception("ERRO VARIABLE. A variavel de nome " + variable.getIdentifier() + " e tipo " + variable.getType().getName() +
//                    " ja existe!");
//        }
//        if (!checkValidExistingType(variable.getType())){
//            if(!variable.getValue().getType().getName().equals("null")){
//                throw new Exception("ERRO VARIABLE. O tipo " + variable.getType().getName() + " da variavel "+ variable.getIdentifier()+
//                        " n√£o existe!");
//            }
//        }
    }
    
    public VariablesScope getCurrentScope() {
        return variablesStackScope.peek();
    }
	
}
