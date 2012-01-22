package model.result;

import java.util.Iterator;

public class StateResult implements ModuleIfc {

	private ModuleIfc parent;
	private String value;
	
	
	public StateResult(ModuleIfc parent) {
		this.parent = parent;
	}

	@Override
	public ModuleType getType() {
		return ModuleType.STATE;
	}

	@Override
	public String getName() {
		return "state";
	}

	@Override
	public ModuleIfc getParent() {
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
	
	public ModuleIfc getChild(String name) {
		return null;
	}

}
