package command;

import item.GraphicView;
import model.FrameModule;
import model.FrameModuleInstance;
import model.InputVariable;
import model.OutputVariable;
import view.FrameModuleInstanceGraphicView;
import view.FrameModuleInstanceTreeView;
import view.ModuleInstanceGraphicView;
import widget.TreeWidget;

import com.trolltech.qt.gui.QUndoCommand;

public class CopyFrameModuleInstanceCommand extends QUndoCommand
{
	private FrameModuleInstance copied_instance;
	private FrameModule parent_module;
	
	public CopyFrameModuleInstanceCommand(FrameModule parent_module, FrameModuleInstance instance, int x, int y, 
			GraphicView scene_view, TreeWidget project_tree, String mod_name, FrameModule original_module)
	{
		this.copied_instance = instance;
		this.parent_module = parent_module;
		
		FrameModuleInstanceGraphicView graphic_view = new FrameModuleInstanceGraphicView(instance, x, y, scene_view, project_tree);
		new FrameModuleInstanceTreeView(instance, project_tree, mod_name, graphic_view.getMenu());
		copyInOut(graphic_view, original_module);
	}
	
	@Override
	public void redo()
	{
		copied_instance.added.emit();
		copied_instance.getInstancedModule().added.emit();
		parent_module.addFrameModuleInstance(copied_instance);
	}
	
	@Override
	public void undo()
	{
		copied_instance.removed.emit();
		copied_instance.getInstancedModule().removed.emit();
		parent_module.removeFrameModuleInstance(copied_instance);
	}
	
	private void copyInOut(ModuleInstanceGraphicView g_view, FrameModule module)
	{
		for (int i = 0; i < module.getInput_variables().size(); i++)
		{
			InputVariable old_v = module.getInput_variables().get(i);
			InputVariable new_v = g_view.getInstance().getInstancedModule().getInput_variables().get(i);
			
			old_v.copy_hook.emit(new_v, g_view);
		}
		
		for (int i = 0; i < module.getOutput_variables().size(); i++)
		{
			OutputVariable old_v = module.getOutput_variables().get(i);
			OutputVariable new_v = g_view.getInstance().getInstancedModule().getOutput_variables().get(i);
			
			old_v.copy_hook.emit(new_v, g_view);
		}
	}
}
