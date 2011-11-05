package command;

import model.FsmModule;
import model.State;
import view.FsmModuleWindowView;
import view.StateGraphicView;
import view.StateTreeView;

import com.trolltech.qt.gui.QUndoCommand;

public class AddStateCommand extends QUndoCommand
{
	private State state;
	private FsmModule parent;
	
	public AddStateCommand(FsmModuleWindowView parent_window, int x, int y)
	{
		this.parent = parent_window.getModule();
		this.state = new State(parent);
		
		StateGraphicView sgv = new StateGraphicView(state, x, y, parent_window.getView());
		sgv.state_clicked.connect(parent_window, "stateClicked(StateGraphicView, int, int, LinePosition)");
		parent_window.getTransitionButton().toggled.connect(sgv, "drawingTransition(boolean)");
		
		new StateTreeView(state, parent_window.getProjectTree(), sgv.getMenu());
	}
	
	@Override
	public void redo()
	{
		parent.addState(state);
	}
	
	@Override
	public void undo()
	{
		parent.removeState(state);
	}
}
