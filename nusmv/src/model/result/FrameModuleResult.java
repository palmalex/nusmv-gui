package model.result;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.FrameModule;
import model.FrameModuleInstance;
import model.FsmModuleInstance;
import model.LocalVariable;
import model.OutputVariable;

public class FrameModuleResult implements ModuleIfc {
	private FrameModuleInstance instance;
	private ModuleIfc parent;
	private Map<String, ModuleIfc> childs;
	private boolean running=false;
	
	public FrameModuleResult(FrameModuleInstance instance, ModuleIfc parent) {
		this.instance = instance;
		this.parent = parent;
		
		childs = new HashMap<String,ModuleIfc>();
		List<FrameModuleInstance> frameChild = ((FrameModule)instance.getInstancedModule()).getFrameModuleInstances();
		for (Iterator<FrameModuleInstance> iterator = frameChild.iterator(); iterator.hasNext();) {
			FrameModuleInstance frameModuleInstance = (FrameModuleInstance) iterator
					.next();
			childs.put(frameModuleInstance.getName(),new FrameModuleResult(frameModuleInstance, this));
		}
		
		List<FsmModuleInstance> fsmChild = ((FrameModule)instance.getInstancedModule()).getFsmModuleInstances();
		for (Iterator<FsmModuleInstance> iterator = fsmChild.iterator(); iterator.hasNext();) {
			FsmModuleInstance fsmModuleInstance = (FsmModuleInstance) iterator
					.next();
			childs.put(fsmModuleInstance.getName(),new FsmModuleResult(fsmModuleInstance, this));
		}
		
		List<LocalVariable> variableChild = ((FrameModule) instance.getInstancedModule()).getLocal_variables();
		for (Iterator<LocalVariable> iterator = variableChild.iterator(); iterator.hasNext();) {
			LocalVariable localVariable = (LocalVariable) iterator.next();
			childs.put(localVariable.getName(),new VariableModuleResult(localVariable, this));
		}
		
		List<OutputVariable> outputChild = ((FrameModule) instance.getInstancedModule()).getOutput_variables();
		for (Iterator<OutputVariable> iterator = outputChild.iterator(); iterator.hasNext();) {
			OutputVariable outputVariable = (OutputVariable) iterator.next();
			childs.put(outputVariable.getName(),new VariableModuleResult(outputVariable, this));
		}
	

	}
	
	@Override
	public ModuleType getType() {
		return ModuleType.FRAME;
	}

	@Override
	public String getName() {
		return instance.getName();
	}

	@Override
	public ModuleIfc getParent() {
		return parent;
	}
	
	public boolean getRunning() {
		return running;
	}
	
	public String getModuleName() {
		return instance.getInstancedModule().getName();
	}
	
	public boolean isProcess() {
		return instance.isProcess();
	}

	@Override
	public Iterator<ModuleIfc> getChilds() {
		return childs.values().iterator();
	}
	
	public ModuleIfc getChild(String name) {
		return childs.get(name);
	}

}
