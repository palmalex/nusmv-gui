package command;

import model.Transition;

import com.trolltech.qt.gui.QUndoCommand;

public class RemoveTransitionCommand extends QUndoCommand
{
	private Transition t;
	
	
	public RemoveTransitionCommand(Transition t)
	{
		this.t = t;
	}
	
	@Override
	public void redo()
	{
		t.removed.emit();
	}
	
	@Override
	public void undo()
	{
		t.added.emit();
	}
}
