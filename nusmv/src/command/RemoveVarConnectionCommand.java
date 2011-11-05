package command;

import view.InputVariableHookView;
import view.OutputVariableHookView;
import view.VariableGraphicView;
import item.GraphicLine;

import com.trolltech.qt.gui.QUndoCommand;

public class RemoveVarConnectionCommand extends QUndoCommand
{
	private GraphicLine line;
	
	public RemoveVarConnectionCommand(GraphicLine line)
	{
		this.line = line;
	}
	
	@Override
	public void redo()
	{
		((InputVariableHookView)line.getEndObject()).removeEnteringLine();
		
		if (line.getStartObject().getClass().getSuperclass().getName().compareTo("view.VariableGraphicView") == 0)
		{
			((VariableGraphicView)line.getStartObject()).removeExitLine(line);
		}
		else
		{
			((OutputVariableHookView)line.getStartObject()).removeExitLine(line);
		}
		line.setVisible(false);
	}
	
	@Override
	public void undo()
	{
		((InputVariableHookView)line.getEndObject()).setEnteringLine(line);
		
		if (line.getStartObject().getClass().getSuperclass().getName().compareTo("view.VariableGraphicView") == 0)
		{
			((VariableGraphicView)line.getStartObject()).addExitLine(line);
		}
		else
		{
			((OutputVariableHookView)line.getStartObject()).addExitLine(line);
		}
		line.setVisible(true);
	}
}
