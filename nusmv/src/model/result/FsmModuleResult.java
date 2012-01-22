package model.result;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.FsmModule;
import model.FsmModuleInstance;
import model.LocalVariable;
import model.OutputVariable;

public class FsmModuleResult implements ModuleIfc {
	private FsmModuleInstance instance;
	private ModuleIfc parent;
	private Map<String,ModuleIfc> childs;
	private boolean running = false;
	
	public FsmModuleResult(FsmModuleInstance instance, ModuleIfc parent) {
		this.instance = instance;
		this.parent = parent;
		this.childs = new HashMap<String,ModuleIfc>();
		
		FsmModule fsmModule = ((FsmModule)instance.getInstancedModule());
		List<LocalVariable> variableList = fsmModule.getLocal_variables();
		for (Iterator<LocalVariable> iterator = variableList.iterator(); iterator.hasNext();) {
			LocalVariable localVariable = (LocalVariable) iterator.next();
			childs.put(localVariable.getName(),new VariableModuleResult(localVariable, this));
		}
		
		childs.put("state",new StateResult(this));
				
		List<OutputVariable> outputList = fsmModule.getOutput_variables();
		for (Iterator<OutputVariable> iterator = outputList.iterator(); iterator.hasNext();) {
			OutputVariable outputVariable = (OutputVariable) iterator.next();
			childs.put(outputVariable.getName(),new VariableModuleResult(outputVariable, this));
		}
	}
	
	public ModuleType getType() {
		return ModuleType.FSM;
	}

	public boolean getRunning() {
		return running;
	}
	@Override
	public String getName() {
		return instance.getName();
	}

	@Override
	public ModuleIfc getParent() {
		return parent;
	}

	@Override
	public Iterator<ModuleIfc> getChilds() {
		return childs.values().iterator();
	}
	
	public ModuleIfc getChild(String name) {
		return childs.get(name);
	}

}
