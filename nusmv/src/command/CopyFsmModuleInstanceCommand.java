package command;

import item.GraphicView;
import model.FsmModule;
import model.FsmModuleInstance;
import model.InputVariable;
import model.OutputVariable;
import view.FrameModuleInstanceTreeView;
import view.FsmModuleInstanceGraphicView;
import view.ModuleInstanceGraphicView;
import widget.TreeWidget;

import com.trolltech.qt.gui.QUndoCommand;

public class CopyFsmModuleInstanceCommand extends QUndoCommand
{
private FsmModuleInstance copied_instance;
	
	public CopyFsmModuleInstanceCommand(FsmModuleInstance instance, int x, int y, 
			GraphicView scene_view, TreeWidget project_tree, String mod_name, FsmModule original_module)
	{
		this.copied_instance = instance;
		
		FsmModuleInstanceGraphicView graphic_view = new FsmModuleInstanceGraphicView(instance, x, y, scene_view, project_tree);
		new FrameModuleInstanceTreeView(instance, project_tree, mod_name, graphic_view.getMenu());
		copyInOut(graphic_view, original_module);
	}
	
	@Override
	public void redo()
	{
		copied_instance.added.emit();
		copied_instance.getInstancedModule().added.emit();
	}
	
	@Override
	public void undo()
	{
		copied_instance.removed.emit();
		copied_instance.getInstancedModule().removed.emit();
	}
	
	private void copyInOut(ModuleInstanceGraphicView g_view, FsmModule module)
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
