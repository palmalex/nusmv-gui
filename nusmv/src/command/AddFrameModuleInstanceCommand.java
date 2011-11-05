package command;

import item.GraphicView;

import java.util.Iterator;

import model.FrameModule;
import model.FrameModuleInstance;
import model.InputVariable;
import model.OutputVariable;
import view.FrameModuleInstanceGraphicView;
import view.FrameModuleInstanceTreeView;
import view.FrameModuleTreeView;
import view.InputVariableHookView;
import view.OutputVariableHookView;
import widget.TreeWidget;

import com.trolltech.qt.gui.QUndoCommand;

public class AddFrameModuleInstanceCommand extends QUndoCommand
{
	private FrameModule parent;
	private FrameModuleInstance instance;
	
	public AddFrameModuleInstanceCommand(FrameModule parent, TreeWidget project_tree,
			String module_name, int x, int y, GraphicView scene_view, boolean duplicate)
	{		
		this.parent = parent;
		
		instance = new FrameModuleInstance(parent, project_tree);
		FrameModuleInstanceGraphicView graphic_view = new FrameModuleInstanceGraphicView(instance, x, y, scene_view, project_tree);
		
		new FrameModuleInstanceTreeView(instance, project_tree, module_name, graphic_view.getMenu());
		if (!duplicate)
		{
			new FrameModuleTreeView((FrameModule)instance.getInstancedModule(), project_tree);
		}
		else
		{
			Iterator<InputVariable> it1 = instance.getInstancedModule().getInput_variables().iterator();
			while (it1.hasNext())
			{
				it1.next().to_duplicate.emit(graphic_view, instance);
			}
		}
	}
	
	/*
	 * Per il duplicate e il copy
	 */
	public AddFrameModuleInstanceCommand(FrameModuleInstance instance, TreeWidget project_tree, String module_name,
			 int x, int y, GraphicView scene_view, boolean duplicate, FrameModuleInstance original_instance)
	{		
		this.parent = (FrameModule)instance.getParentModule();
		
		this.instance = instance;
		
		FrameModuleInstanceGraphicView graphic_view = new FrameModuleInstanceGraphicView(instance, x, y, scene_view, project_tree);
		new FrameModuleInstanceTreeView(instance, project_tree, module_name, graphic_view.getMenu());
		if (!duplicate)
		{
			new FrameModuleTreeView((FrameModule)instance.getInstancedModule(), project_tree);
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
	
	public AddFrameModuleInstanceCommand(FrameModule parent, FrameModule m, int x, int y, TreeWidget tree, GraphicView view)
	{
		this.parent = parent;
		instance = new FrameModuleInstance(parent, m, tree);
		
		FrameModuleInstanceGraphicView graphic_view = new FrameModuleInstanceGraphicView(instance, x, y, view, tree);
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
		parent.addFrameModuleInstance(instance);
		instance.added.emit();
		instance.getInstanced_module().added.emit();
	}
	
	@Override
	public void undo()
	{
		parent.removeFrameModuleInstance(instance);
		instance.removed.emit();
		instance.getInstanced_module().removed.emit();
	}
}
