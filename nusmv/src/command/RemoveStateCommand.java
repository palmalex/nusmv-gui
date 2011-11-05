package command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.FsmModule;
import model.State;
import model.Transition;

import com.trolltech.qt.gui.QUndoCommand;

public class RemoveStateCommand extends QUndoCommand
{
	private State state;
	private FsmModule parent;
	private List<Transition> entering_transitions;
	private List<Transition> exiting_transitions;
	
	public RemoveStateCommand(State state)
	{
		this.state = state;
		this.parent = state.getModule();
		this.entering_transitions = new ArrayList<Transition>(0);
		
		Iterator<Transition> it = state.getEntering_transitions().iterator();
		
		while (it.hasNext())
		{
			Transition t = it.next();
			if (!entering_transitions.contains(t))
			
				entering_transitions.add(t);
		}
		
		this.exiting_transitions = new ArrayList<Transition>(0);
		
		it = state.getExiting_transitions().iterator();
		
		while (it.hasNext())
		{
			Transition t = it.next();
			if (!exiting_transitions.contains(t))
			
				exiting_transitions.add(t);
		}
	}
	
	@Override
	public void redo()
	{		
		Iterator<Transition> it = entering_transitions.iterator();
		
		while (it.hasNext())
		{
			Transition t = it.next();
//			t.getStart_state().removeExitingTransition(t);
			t.removed.emit();
		}

		it = exiting_transitions.iterator();
		
		while (it.hasNext())
		{
			Transition t = it.next();
//			t.getEnd_state().removeEnteringTransition(t);
			t.removed.emit();
		}
		parent.removeState(state);
	}
	
	@Override
	public void undo()
	{
		Iterator<Transition> it = entering_transitions.iterator();
		
		while (it.hasNext())
		{
			Transition t = it.next();
//			t.getStart_state().addExitingTransition(t);
			t.added.emit();
		}
		
		it = exiting_transitions.iterator();
		
		while (it.hasNext())
		{
			Transition t = it.next();
//			t.getStart_state().addEnteringTransition(t);
			t.added.emit();
		}
		parent.addState(state);		
	}

}
