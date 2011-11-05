package command;

import model.Module;

import com.trolltech.qt.gui.QUndoCommand;

public class RemoveModuleCommand extends QUndoCommand
{
	private Module module;
	
	public RemoveModuleCommand(Module m)
	{
		this.module = m;
	}
	
	@Override
	public void redo()
	{
		module.removed.emit();
	}
	
	@Override
	public void undo()
	{
		module.added.emit();
	}
}
