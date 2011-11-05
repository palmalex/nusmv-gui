package command;

import model.Module;

import com.trolltech.qt.gui.QUndoCommand;

public class RenameModuleCommand extends QUndoCommand
{
	private Module module;
	private String old_name;
	private String new_name;
	
	public RenameModuleCommand(Module module, String old_name, String new_name)
	{
		this.module = module;
		this.old_name = old_name;
		this.new_name = new_name;
	}
	
	@Override
	public void undo()
	{
		module.setName(old_name);
	}
	
	@Override
	public void redo()
	{
		module.setName(new_name);
	}
}
