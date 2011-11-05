package command;

import model.LocalVariable;
import model.Module;

import com.trolltech.qt.gui.QUndoCommand;

public class RemoveLocalVariableCommand extends QUndoCommand
{
	private Module parent;
	private LocalVariable lv;
	
	public RemoveLocalVariableCommand(LocalVariable var)
	{
		this.lv = var;
		this.parent = var.getModule();
	}
	
	@Override
	public void undo()
	{
		lv.added.emit();
		parent.addLocalVariable(lv);
	}
	
	@Override
	public void redo()
	{
		lv.removed.emit();
		parent.removeLocalVariable(lv);
	}
}
