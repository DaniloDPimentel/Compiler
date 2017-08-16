package util;

import java.util.ArrayList;
import java.util.List;

public class Function extends VariablesScope{
	

	Type declaredReturnType;
	Type returnType;
	List<Param> params;
	
	public Function(String name, ArrayList<Param>params){
        super(name);
        if(params != null){
			this.params = params;
		}else{
			this.params = new ArrayList<Param>();
		}
	}

	public boolean isReturnValid(){
		return this.declaredReturnType.equals(this.returnType);
	}
	public Type getDeclaredReturnType() {
		return declaredReturnType;
	}

	public Type getReturnType(){
		return returnType;
	}

	public List<Param> getParams() {
		return this.params;
	}
	
	public void setDeclaredReturnedType(Type type) {
		this.declaredReturnType = type;
	}

	public void validateReturnedType() throws Exception { // Checks if the function returned what it was supposed to..
		if (!returnType.equals(declaredReturnType))
			throw new Exception("Function " + getName() + " was supposed to return " + declaredReturnType);
	}
	
	public void addParam(Param a){
		params.add(a);
	}
	@Override
	public boolean equals(Object obj){
		if(!(obj instanceof Function)) return false;
		Function f= (Function) obj;
		if(!f.getName().equals(getName()))return false;
		if(f.getParams().size() != getParams().size()) return false;
		
		for(int i=0;i<getParams().size();i++){
			if(! f.getParams().get(i).getType().equals(getParams().get(i).getType())) return false;
		}
		
		return true;
	}


}
