/********************************************************************************
*                                                                               *
*   Module      :   State.java                                                  *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import java.util.ArrayList;
import java.util.List;

import view.TransitionGraphicView;
import xml.XmlCreator;

import com.trolltech.qt.QSignalEmitter;

/**
 * Classe relativa al modello di uno stato di una FSM.
 * @author Silvia Lorenzini
 *
 */
public class State extends QSignalEmitter
{
	private String name; //nome dello stato
	private List<Transition> exiting_transitions; //lista di transizioni uscenti dallo stato
	private List<Transition> entering_transitions; //lista di transizioni entranti nello stato
	private boolean initial; //true se lo stato è iniziale.
	private List<String> onentry; //lista delle azioni onentry
	private List<String> during; //lista delle azioni during
	private List<String> onexit; //lista delle azioni onexit
	private FsmModule module; //modulo FSM padre
	private int state_index; //indice di default utilizzato per il nome dello stato.
	
	public Signal0 added;
	public Signal0 removed;
	public Signal0 renamed;
	public Signal1<Boolean> change_initial;
	public Signal0 actions_changed;
	public Signal1<TransitionGraphicView> added_entry_transition;
	public Signal1<TransitionGraphicView> added_exit_transition;
	public Signal1<Transition> removed_entry_transition;
	public Signal1<Transition> removed_exit_transition;
	public Signal1<XmlCreator> need_view_info;
	public Signal2<Integer, Integer> state_moved;
	public Signal2<Integer, Integer> state_resized;
		
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public State(FsmModule module)
	{
		this.exiting_transitions = new ArrayList<Transition>(0);
		this.entering_transitions = new ArrayList<Transition>(0);
		this.module = module;
		this.state_index = module.getState_count();
		this.name = "State_" + state_index;
		this.initial = false;
		this.onentry = new ArrayList<String>(0);
		this.onexit = new ArrayList<String>(0);
		this.during = new ArrayList<String>(0);
		
		this.added = new Signal0();
		this.removed = new Signal0();
		this.renamed = new Signal0();
		this.change_initial = new Signal1<Boolean>();
		this.actions_changed = new Signal0();
		this.added_entry_transition = new Signal1<TransitionGraphicView>();
		this.added_exit_transition = new Signal1<TransitionGraphicView>();
		this.removed_entry_transition = new Signal1<Transition>();
		this.removed_exit_transition = new Signal1<Transition>();
		this.need_view_info = new Signal1<XmlCreator>();
		this.state_moved = new Signal2<Integer, Integer>();
		this.state_resized = new Signal2<Integer, Integer>();
		
		module.inital_state_setted.connect(this, "initialStateSetted(State)");
	}
	
	/**
	 * Costruttore
	 * @param module modulo FSM padre.
	 * @param name nome dello stato.
	 */
	public State(FsmModule module, String name)
	{
		this.module = module;
		this.name = name;
		this.exiting_transitions = new ArrayList<Transition>(0);
		this.entering_transitions = new ArrayList<Transition>(0);
		this.initial = false;
		this.onentry = new ArrayList<String>(0);
		this.onexit = new ArrayList<String>(0);
		this.during = new ArrayList<String>(0);
		
		this.added = new Signal0();
		this.removed = new Signal0();
		this.renamed = new Signal0();
		this.change_initial = new Signal1<Boolean>();
		this.actions_changed = new Signal0();
		this.added_entry_transition = new Signal1<TransitionGraphicView>();
		this.added_exit_transition = new Signal1<TransitionGraphicView>();
		this.removed_entry_transition = new Signal1<Transition>();
		this.removed_exit_transition = new Signal1<Transition>();
		this.need_view_info = new Signal1<XmlCreator>();
		this.state_moved = new Signal2<Integer, Integer>();
		this.state_resized = new Signal2<Integer, Integer>();
		
		NuSmvKeywords.checkName(name);
	}

	/**
	 * 
	 * @return il nome dello stato.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Imposta il nome dello stato e notifica le viste.
	 * @param name
	 */
	public void setName(String name)
	{
		this.name = name;
		NuSmvKeywords.checkName(name);
		renamed.emit();
	}

	/**
	 * 
	 * @return la lista di transizioni uscenti.
	 */
	public List<Transition> getExiting_transitions()
	{
		return exiting_transitions;
	}

	/**
	 * Imposta la lista di transizioni uscenti.
	 * @param exitingTransitions lista di transizioni uscenti 
	 */
	public void setExiting_transitions(List<Transition> exitingTransitions)
	{
		exiting_transitions = exitingTransitions;
	}
	
	/**
	 * Aggiunge una transizione alla lista di transizioni uscenti.
	 * @param t transizione da aggiungere.
	 */
	public void addExitingTransition(Transition t)
	{
		if (!exiting_transitions.contains(t))
		{
			exiting_transitions.add(t);
			state_moved.connect(t.start_state_moved);
			state_resized.connect(t.start_state_resized);
		}
	}
	
	/**
	 * Rimuove una transizione dalla lista di transizioni uscenti.
	 * @param t transizione da rimuovere.
	 */
	public void removeExitingTransition(Transition t)
	{
		if (exiting_transitions.remove(t))
		{
			removed_exit_transition.emit(t);
			state_moved.disconnect(t.start_state_moved);
			state_resized.disconnect(t.start_state_resized);
		}
	}

	public List<Transition> getEntering_transitions()
	{
		return entering_transitions;
	}

	public void setEntering_transitions(List<Transition> enteringTransitions)
	{
		entering_transitions = enteringTransitions;
	}
	
	public void addEnteringTransition(Transition t)
	{
		if (!entering_transitions.contains(t))
		{
			entering_transitions.add(t);
			state_moved.connect(t.end_state_moved);
			state_resized.connect(t.end_state_resized);
		}
	}
	
	public void removeEnteringTransition(Transition t)
	{
		if (entering_transitions.remove(t))
		{
			removed_entry_transition.emit(t);
			state_moved.disconnect(t.end_state_moved);
			state_resized.disconnect(t.end_state_resized);
		}
	}

	/**
	 * 
	 * @return true se lo stato è iniziale.
	 */
	public boolean isInitial()
	{
		return initial;
	}

	public void setInitial(boolean initial)
	{
		this.initial = initial;
	}
	
	/**
	 * Cambia lo stato da iniziale a non o viceversa. 
	 * Metodo usato per non avere due stati iniziali contemporaneamente.
	 */
	public void changeInitial()
	{
		initial = !initial;
		change_initial.emit(initial);
		
		if (initial)
		{
			module.inital_state_setted.emit(this);
		}
	}

	/**
	 *
	 * @return la lista delle azioni onentry.
	 */
	public List<String> getOnentry()
	{
		return onentry;
	}

	/**
	 * Imposta la lista delle azioni onentry.
	 * @param onentry lista da assegnare.
	 */
	public void setOnentry(List<String> onentry)
	{
		this.onentry = onentry;
	}
	
	/**
	 * Aggiunge un'azione onentry.
	 * @param onentry azione da aggiungere.
	 */
	public void addOnentry(String onentry)
	{
		this.onentry.add(onentry);
		actions_changed.emit();
	}
	
	/**
	 * Rimuove un'azione onentry.
	 * @param onentry azione da rimuovere.
	 */
	public void removeOnentry(String onentry)
	{
		this.onentry.remove(onentry);
		actions_changed.emit();
	}

	public List<String> getDuring()
	{
		return during;
	}

	public void setDuring(List<String> during)
	{
		this.during = during;
	}
	
	public void addDuring(String during)
	{
		this.during.add(during);
		actions_changed.emit();
	}
	
	public void removeDuring(String d)
	{
		during.remove(d);
		actions_changed.emit();
	}

	public List<String> getOnexit()
	{
		return onexit;
	}

	public void setOnexit(List<String> onexit)
	{
		this.onexit = onexit;
	}
	
	public void addOnexit(String onexit)
	{
		this.onexit.add(onexit);
		actions_changed.emit();
	}
	
	public void removeOnexit(String s)
	{
		onexit.remove(s);
		actions_changed.emit();
	}

	/**
	 * 
	 * @return il modulo FSM padre.
	 */
	public FsmModule getModule()
	{
		return module;
	}	
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Se un altro stato è settato come iniziale e questo lo era fin'ora, il flag initial deve essere settato a false.
	 */
	protected void initialStateSetted(State s)
	{
		if (initial && !s.equals(this))
		{
			changeInitial();
		}
	}
}
