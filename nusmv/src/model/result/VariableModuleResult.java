package model.result;

import java.util.Iterator;

import model.Variable;

public class VariableModuleResult implements ModuleIfc {
	private Variable variable;
	private ModuleIfc parent;
	private String value;
	
	public VariableModuleResult(Variable variable, ModuleIfc parent) {
		this.parent=parent;
		this.variable=variable;
		value = variable.getInitial_value();
	}
	@Override
	public ModuleType getType() {
		return ModuleType.VARIABLE;
	}

	@Override
	public String getName() {
		return variable.getName();
	}

	@Override
	public ModuleIfc getParent() {
		// TODO Auto-generated method stub
		return parent;
	}
	

	@Override
	public Iterator<ModuleIfc> getChilds() {
		return new EmptyIterator();
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public ModuleIfc getChild(String name){
		return null;
	}

}
