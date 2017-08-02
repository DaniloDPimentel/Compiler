package util;

import java.util.HashMap;
import java.util.Map;

public class VariablesScope  {

    private HashMap<String, Variable> variables;
    private HashMap<String, Type> types;

    public VariablesScope(String name) {
        variables = new HashMap<String, Variable>();
    }

    public Map<String, Variable> getVariable() {
        return variables;
    }

    public void addVariable(Variable v) {
        this.variables.put(v.getIdentifier(), v);
    }

    public void addType(Type t) {
        this.types.put(t.getName(), t);
    }

    public Map<String, Type> getTypes() {
        return types;
    }

}
