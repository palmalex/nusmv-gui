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

public class MainModuleResult implements ModuleIfc {
	private FrameModule main;
	private Map<String,ModuleIfc> childs;
	private boolean running = false;
	
	public MainModuleResult(FrameModule main) {
		this.main = main;
		childs = new HashMap<String,ModuleIfc>();
		
		List<FrameModuleInstance> frameList = main.getFrameModuleInstances();
		for (Iterator<FrameModuleInstance> iterator = frameList.iterator(); iterator.hasNext();) {
			FrameModuleInstance frameModuleInstance = (FrameModuleInstance) iterator
					.next();
			childs.put(frameModuleInstance.getName(),new FrameModuleResult(frameModuleInstance, this));
		}
		
		List<FsmModuleInstance> fsmList = main.getFsmModuleInstances();
		for (Iterator<FsmModuleInstance> iterator = fsmList.iterator(); iterator.hasNext();) {
			FsmModuleInstance fsmModuleInstance = (FsmModuleInstance) iterator
					.next();
			childs.put(fsmModuleInstance.getName(),new FsmModuleResult(fsmModuleInstance,this));
		}
		
		List<LocalVariable> varList = main.getLocal_variables();
		for (Iterator<LocalVariable> iterator = varList.iterator(); iterator.hasNext();) {
			LocalVariable localVariable = (LocalVariable) iterator.next();
			childs.put(localVariable.getName(),new VariableModuleResult(localVariable, this));
		}
				
		List<OutputVariable> outputList = main.getOutput_variables();
		for (Iterator<OutputVariable> iterator = outputList.iterator(); iterator.hasNext();) {
			OutputVariable outputVariable = (OutputVariable) iterator.next();
			childs.put(outputVariable.getName(),new VariableModuleResult(outputVariable, this));
		}
		
	}
	
	@Override
	public ModuleType getType() {
		return ModuleType.MAIN;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Main";
	}

	@Override
	public ModuleIfc getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getRunning() {
		return running;
	}
	@Override
	public Iterator<ModuleIfc> getChilds() {
		
		return childs.values().iterator();
	}
	
	public ModuleIfc getChild(String name) {
		return childs.get(name);
	}

}
