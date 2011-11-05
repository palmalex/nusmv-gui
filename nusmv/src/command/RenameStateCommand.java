package command;

import model.State;

import com.trolltech.qt.gui.QUndoCommand;

public class RenameStateCommand extends QUndoCommand
{
	private State state;
	private String old_name;
	private String new_name;
	
	public RenameStateCommand(State state, String old_name, String new_name)
	{
		this.state = state;
		this.old_name = old_name;
		this.new_name = new_name;
	}
	
	@Override
	public void redo()
	{
		state.setName(new_name);
	}
	
	@Override
	public void undo()
	{
		state.setName(old_name);
	}
}
