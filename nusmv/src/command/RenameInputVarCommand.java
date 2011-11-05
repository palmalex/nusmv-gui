package command;

import model.Variable;

import com.trolltech.qt.gui.QUndoCommand;

public class RenameInputVarCommand extends QUndoCommand
{
	private String old_name;
	private String new_name;
	private Variable var;
	
	public RenameInputVarCommand(Variable var, String old_name, String new_name)
	{
		this.var = var;
		this.old_name = old_name;
		this.new_name = new_name;
	}
	
	@Override
	public void redo()
	{
		var.edit(new_name, var.getType(), var.getValues(), var.getInitial_value());	
	}
	
	@Override
	public void undo()
	{
		var.edit(old_name, var.getType(), var.getValues(), var.getInitial_value());	
	}
}
