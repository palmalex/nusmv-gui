package command;

import model.InputVariable;
import model.Module;

import com.trolltech.qt.gui.QUndoCommand;

public class RemoveInputVariableCommand extends QUndoCommand
{
	private Module parent;
	private InputVariable in;
	
	public RemoveInputVariableCommand(InputVariable var)
	{
		this.in = var;
		this.parent = var.getModule();
	}
	
	@Override
	public void undo()
	{
		parent.addInputVariable(in);
		in.added.emit();
	}
	
	@Override
	public void redo()
	{
		in.removed.emit();
		parent.removeInputVariable(in);
	}
}
