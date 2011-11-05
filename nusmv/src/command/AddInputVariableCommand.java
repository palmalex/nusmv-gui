package command;

import item.GraphicView;
import model.InputVariable;
import model.Module;
import view.InputVariableDockView;
import view.InputVariableGraphicView;
import view.InputVariableHookView;
import view.InputVariableTreeView;
import view.ModuleInstanceGraphicView;
import widget.TreeWidget;

import com.trolltech.qt.gui.QUndoCommand;

public class AddInputVariableCommand extends QUndoCommand
{
	private Module parent;
	private InputVariable in;
	
	public AddInputVariableCommand(Module parent, int x, int y, ModuleInstanceGraphicView graphic_instance, TreeWidget tree, TreeWidget local)
	{
		this.parent = parent;
		in = new InputVariable(parent);
		
		new InputVariableHookView(graphic_instance, in, x, y, graphic_instance.getView());
		new InputVariableTreeView(in, in.getModule(), tree);
		parent.input_added.emit(in);
	}
	
	public AddInputVariableCommand(Module parent, InputVariable in, int x, int y, GraphicView view, TreeWidget tree, TreeWidget in_tree)
	{
		this.parent = parent;
		
		this.in = in;
		new InputVariableGraphicView(in, x, y, view);
		new InputVariableTreeView(in, in.getModule(), tree);
		new InputVariableDockView(in, in_tree);
	}
	
	public AddInputVariableCommand(Module parent, InputVariable in, TreeWidget tree)
	{
		this.parent = parent;
		this.in = in;
		
		new InputVariableTreeView(in, in.getModule(), tree);

		parent.input_added.emit(in);
	}
	
	@Override
	public void undo()
	{
		in.removed.emit();
		parent.removeInputVariable(in);
	}
	
	@Override
	public void redo()
	{	
		parent.addInputVariable(in);
		in.added.emit();
	}
}
