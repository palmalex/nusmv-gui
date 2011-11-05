package command;

import model.Module;
import model.OutputVariable;

import com.trolltech.qt.gui.QUndoCommand;

public class RemoveOutputVariableCommand extends QUndoCommand
{
	private Module parent;
	private OutputVariable out;
	
	public RemoveOutputVariableCommand(OutputVariable var)
	{
		this.out = var;
		this.parent = var.getModule();
	}
	
	@Override
	public void undo()
	{
		parent.addOutputVariable(out);
		out.added.emit();
	}
	
	@Override
	public void redo()
	{
		out.removed.emit();
		parent.removeOutputVariable(out);
	}
}
