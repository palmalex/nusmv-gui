package command;

import item.GraphicView;
import model.LocalVariable;
import model.Module;
import view.LocalVariableDockView;
import view.LocalVariableGraphicView;
import view.LocalVariableTreeView;
import widget.TreeWidget;

import com.trolltech.qt.gui.QUndoCommand;

public class AddLocalVariableCommand extends QUndoCommand
{
	private Module parent;
	private LocalVariable lv;
	
	public AddLocalVariableCommand(Module parent, int x, int y, GraphicView view, TreeWidget tree, TreeWidget local)
	{
		this.parent = parent;
		
		lv = new LocalVariable(parent);
		LocalVariableGraphicView lvgv = new LocalVariableGraphicView(lv, x, y, view);
		new LocalVariableTreeView(tree, lv);
		new LocalVariableDockView(local, lv, lvgv.getMenu());
	}
	
	@Override
	public void undo()
	{
		lv.remove();
		parent.removeLocalVariable(lv);
	}
	
	@Override
	public void redo()
	{		
		parent.addLocalVariable(lv);
		lv.added.emit();
	}
}
