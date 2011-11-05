package command;


import model.FrameModule;
import model.FrameModuleInstance;

import com.trolltech.qt.gui.QUndoCommand;

public class RemoveFrameModuleInstanceCommand extends QUndoCommand
{
	private FrameModule parent;
	private FrameModuleInstance fm;
	
	public RemoveFrameModuleInstanceCommand(FrameModuleInstance fm)
	{
		this.fm = fm;
		this.parent = (FrameModule)fm.getParentModule();
	}
	
	@Override
	public void undo()
	{
		fm.added.emit();
		parent.addFrameModuleInstance(fm);
		parent.removeVarCountAvailableInt(fm.getVarIndex());
	}
	
	@Override
	public void redo()
	{
		fm.removed.emit();
		parent.removeFrameModuleInstance(fm);
		parent.setVarCountAvailableInt(fm.getVarIndex());
	}
}
