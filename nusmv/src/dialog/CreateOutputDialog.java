package dialog;

import model.Module;
import model.OutputVariable;
import model.Variable;
import view.ModuleInstanceGraphicView;

import command.AddOutputVariableCommand;

public class CreateOutputDialog extends VariableOptionsDialog
{
	private Module instanced_module;
	private ModuleInstanceGraphicView parent;

	public CreateOutputDialog(Variable var, ModuleInstanceGraphicView parent, Module instanced_module)
	{
		super(var);
		this.parent = parent;
		this.instanced_module = instanced_module;
	}

	@Override
	protected void okClicked()
	{
		super.okClicked();
		
		parent.getView().getUndoStack().push(
				new AddOutputVariableCommand((OutputVariable)var, instanced_module, 100, 100, parent, parent.getProjectTree(), null));
	}
}
