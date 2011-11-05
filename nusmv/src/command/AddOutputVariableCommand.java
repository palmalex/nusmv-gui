package command;

import item.GraphicView;
import model.Module;
import model.OutputVariable;
import view.ModuleInstanceGraphicView;
import view.OutputVariableDockView;
import view.OutputVariableGraphicView;
import view.OutputVariableHookView;
import view.OutputVariableTreeView;
import widget.TreeWidget;

import com.trolltech.qt.gui.QUndoCommand;

public class AddOutputVariableCommand extends QUndoCommand
{
	private Module parent;
	private OutputVariable out;
	
	public AddOutputVariableCommand(OutputVariable out, Module parent, int x, int y, ModuleInstanceGraphicView graphic_instance, TreeWidget tree, TreeWidget local)
	{
		this.parent = parent;
		this.out = out;
		new OutputVariableHookView(graphic_instance, out, x, y, graphic_instance.getView());
		new OutputVariableTreeView(out, out.getModule(), tree);
		parent.output_added.emit(out);
	}
	
	public AddOutputVariableCommand(Module parent, OutputVariable out, TreeWidget tree)
	{
		this.parent = parent;
		this.out = out;
		
		new OutputVariableTreeView(out, out.getModule(), tree);

		parent.output_added.emit(out);
	}
	
	public AddOutputVariableCommand(Module parent, OutputVariable out, int x, int y, GraphicView view, TreeWidget tree, TreeWidget out_tree)
	{
		this.parent = parent;
		
		this.out = out;
		new OutputVariableGraphicView(out, x, y, view);
		new OutputVariableTreeView(out, out.getModule(), tree);
		new OutputVariableDockView(out, out_tree);
	}
	
	@Override
	public void undo()
	{
		out.removed.emit();
		parent.removeOutputVariable(out);
	}
	
	@Override
	public void redo()
	{	
		parent.addOutputVariable(out);
		out.added.emit();
	}
}
