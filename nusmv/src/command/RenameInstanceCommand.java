package command;

import view.ModuleInstanceGraphicView;

import com.trolltech.qt.gui.QUndoCommand;

public class RenameInstanceCommand extends QUndoCommand
{
	private String old_mod_name;
	private String new_mod_name;
	private String old_var_name;
	private String new_var_name;
	private ModuleInstanceGraphicView module;
	
	public RenameInstanceCommand(ModuleInstanceGraphicView module, String old_mod_name, String old_var_name,
			String new_mod_name, String new_var_name)
	{
		this.old_mod_name = old_mod_name;
		this.new_mod_name = new_mod_name;
		this.old_var_name = old_var_name;
		this.new_var_name = new_var_name;
		this.module = module;
	}
	
	@Override
	public void undo()
	{
		module.module_name_changed.emit(old_mod_name);
		module.var_name_changed.emit(old_var_name);
	}
	
	@Override
	public void redo()
	{
		module.module_name_changed.emit(new_mod_name);
		module.var_name_changed.emit(new_var_name);
	}
}
