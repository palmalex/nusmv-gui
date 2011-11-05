package command;

import model.FrameModule;
import model.FsmModuleInstance;

import com.trolltech.qt.gui.QUndoCommand;

public class RemoveFsmModuleInstanceCommand extends QUndoCommand
{
	private FrameModule parent;
	private FsmModuleInstance fm;
	
	public RemoveFsmModuleInstanceCommand(FsmModuleInstance fm)
	{
		this.parent = fm.getParentModule();
		this.fm = fm;
	}
	
	@Override
	public void undo()
	{
		fm.added.emit();
		parent.addFsmModuleInstance(fm);
//		fm.getInstanced_module().added.emit();
	}
	
	@Override
	public void redo()
	{
		fm.removed.emit();
		parent.removeFsmModuleInstance(fm);
		fm.getInstancedModule().mod_index_available.emit();
		parent.setVarCountAvailableInt(fm.getVarIndex());
//		fm.getInstanced_module().removed.emit();
	}
}
