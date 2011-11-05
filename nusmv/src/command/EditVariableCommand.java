package command;

import model.Type;
import model.Variable;

import com.trolltech.qt.gui.QUndoCommand;

public class EditVariableCommand extends QUndoCommand
{
	private String old_name;
	private String new_name;
	private Type old_type;
	private Type new_type;
	private String old_values;
	private String new_values;
	private String old_init;
	private String new_init;
	private Variable var;
	private boolean initialization;
	
	public EditVariableCommand(Variable var, String old_name, Type old_type, String old_values, String old_init)
	{
		this.var = var;
		this.old_name = old_name;
		this.new_name = var.getName();
		this.old_type = old_type;
		this.new_type = var.getType();
		this.old_values = old_values;
		this.new_values = var.getValues();
		this.old_init = old_init;
		this.new_init = var.getInitial_value();	
		this.initialization = true;
	}
	
	@Override
	public void undo()
	{
		var.edit(old_name, old_type, old_values, old_init);
	}
	
	public void redo()
	{
		if (!initialization)
		{
			var.edit(new_name, new_type, new_values, new_init);
		}
		initialization = false;
	}
}
