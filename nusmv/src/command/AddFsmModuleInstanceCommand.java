package command;

import item.GraphicView;

import java.util.Iterator;

import model.FrameModule;
import model.FsmModule;
import model.FsmModuleInstance;
import model.InputVariable;
import model.OutputVariable;
import view.FrameModuleInstanceTreeView;
import view.FsmModuleInstanceGraphicView;
import view.FsmModuleInstanceTreeView;
import view.FsmModuleTreeView;
import view.InputVariableHookView;
import view.OutputVariableHookView;
import widget.TreeWidget;

import com.trolltech.qt.gui.QUndoCommand;

public class AddFsmModuleInstanceCommand extends QUndoCommand
{
	private FrameModule parent;
	private FsmModuleInstance instance;
	
	public AddFsmModuleInstanceCommand(FrameModule parent, TreeWidget project_tree,
			String module_name, int x, int y, GraphicView scene_view, boolean duplicate)
	{		
		this.parent = parent;
		this.instance = new FsmModuleInstance(parent, project_tree);
		
		FsmModuleInstanceGraphicView graphic_view = new FsmModuleInstanceGraphicView(instance, x, y, scene_view, project_tree);
		
		new FsmModuleInstanceTreeView(instance, project_tree, module_name, graphic_view.getMenu());
		if (!duplicate)
		{
			new FsmModuleTreeView((FsmModule)instance.getInstancedModule(), project_tree);
		}
	}
	
	/*
	 * Per il duplicate e il copy
	 */
	public AddFsmModuleInstanceCommand(FsmModuleInstance fm, TreeWidget project_tree, String module_name,
										 int x, int y, GraphicView scene_view, boolean duplicate, FsmModuleInstance original_instance)
	{		
		this.instance = fm;
		this.parent = fm.getParentModule();
		
		FsmModuleInstanceGraphicView graphic_view = new FsmModuleInstanceGraphicView(instance, x, y, scene_view, project_tree);
		new FsmModuleInstanceTreeView(instance, project_tree, module_name, graphic_view.getMenu());
		if (!duplicate)
		{
			new FsmModuleTreeView((FsmModule)instance.getInstancedModule(), project_tree);
		}
		else
		{
			Iterator<InputVariable> it1 = instance.getInstancedModule().getInput_variables().iterator();
			while (it1.hasNext())
			{
				it1.next().to_duplicate.emit(graphic_view, original_instance);
			}
			
			Iterator<OutputVariable> it2 = instance.getInstancedModule().getOutput_variables().iterator();
			while (it2.hasNext())
			{
				it2.next().to_duplicate.emit(graphic_view, original_instance);
			}
		}
	}
	
	public AddFsmModuleInstanceCommand(FrameModule parent, FsmModule m, int x, int y, TreeWidget tree, GraphicView view)
	{
		this.parent = parent;
		instance = new FsmModuleInstance(parent, m, tree);
		
		FsmModuleInstanceGraphicView graphic_view = new FsmModuleInstanceGraphicView(instance, x, y, view, tree);
		new FrameModuleInstanceTreeView(instance, tree, parent.getName(), graphic_view.getMenu());
		
		Iterator<InputVariable> it1 = m.getInput_variables().iterator();
		
		while (it1.hasNext())
		{
			InputVariable in = it1.next();
			int px = (int)(graphic_view.getX() + graphic_view.getRadius());
			int py = (int)(graphic_view.getY());
			new InputVariableHookView(graphic_view, in, px, py - 8, view, "to_down");
		}
		
		Iterator<OutputVariable> it2 = m.getOutput_variables().iterator();
		while (it2.hasNext())
		{
			OutputVariable out = it2.next();
			int px = (int)(graphic_view.getX() + graphic_view.getRadius());
			int py = (int)(graphic_view.getY() + graphic_view.height());
			new OutputVariableHookView(graphic_view, out, px, py, view, "to_up");
		}
	}
	
	@Override
	public void redo()
	{
		parent.addFsmModuleInstance(instance);
		instance.added.emit();
		instance.getInstanced_module().added.emit();
	}
	
	@Override
	public void undo()
	{
		parent.removeFsmModuleInstance(instance);
		instance.removed.emit();
		instance.getInstanced_module().removed.emit();
	}
}
