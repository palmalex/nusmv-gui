/********************************************************************************
*                                                                               *
*   Module      :   Transition.java                                             *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import xml.XmlCreator;

import com.trolltech.qt.QSignalEmitter;

/**
 * Classe relativa al modello di transizione fra due stati.
 * @author Silvia Lorenzini
 *
 */
public class Transition extends QSignalEmitter
{
	private String condition; // Condizione di transizione.
	private State start_state; // Stato iniziale.
	private State end_state; //  Stato finale.
	private boolean conversionError;
	public Signal0 removed;
	public Signal0 added;
	public Signal0 condition_changed;
	public Signal1<XmlCreator> need_view_info;
	public Signal2<Integer, Integer> start_state_moved;
	public Signal2<Integer, Integer> end_state_moved;
	public Signal2<Integer, Integer> start_state_resized;
	public Signal2<Integer, Integer> end_state_resized;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public Transition(State start_state)
	{
		setConversionError(false);
		start_state_moved = new Signal2<Integer, Integer>();
		end_state_moved = new Signal2<Integer, Integer>();
		start_state_resized = new Signal2<Integer, Integer>();
		end_state_resized = new Signal2<Integer, Integer>();
		setStart_state(start_state);
		this.condition = "";
		this.end_state = null;
		this.removed = new Signal0();
		this.added = new Signal0();
		this.need_view_info = new Signal1<XmlCreator>();
		
		
		start_state.removed.connect(this, "startStateRemoved()");
		start_state.added.connect(this, "startStateAdded()");
		
		removed.connect(this, "removed()");
		added.connect(this, "added()");
		condition_changed = new Signal0();
	}

	/**
	 * 
	 * @return la condizione.
	 */
	public String getCondition()
	{
		return condition;
	}

	/**
	 * Imposta la condizione
	 * @param condition condizione di transizione.
	 */
	public void setCondition(String condition)
	{
		this.condition = condition;
		condition_changed.emit();
	}

	/**
	 * 
	 * @return lo stato iniziale.
	 */
	public State getStart_state()
	{
		return start_state;
	}

	/**
	 * Imposta lo stato iniziale e vi aggiunge quasta transizione come uscente.
	 * @param startState stato iniziale.
	 */
	public void setStart_state(State startState)
	{
		start_state = startState;
		startState.addExitingTransition(this);
	}

	/**
	 * 
	 * @return lo satto finale.
	 */
	public State getEnd_state()
	{
		return end_state;
	}

	/**
	 * Imposta lo stato finale e vi aggiunge questa transizione come entrante.
	 * @param endState
	 */
	public void setEnd_state(State endState)
	{
		end_state = endState;
		end_state.addEnteringTransition(this);
		
		end_state.removed.connect(this, "endStateRemoved()");
		end_state.added.connect(this, "endStateAdded()");
	}	
	
	/**
	 * Quando la transizione non si aggancia a nessuno stato finale viene eliminata anche dallo stato iniziale.
	 */
	public void transitionLost()
	{
		start_state.removeExitingTransition(this);
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Rimuove la transizione dallo stato iniziale a dallo stato finale.
	 */
	protected void removed()
	{
		start_state.removeExitingTransition(this);
		end_state.removeEnteringTransition(this);
	}
	
	/**
	 * Aggiunge la transizione allo stato iniziale e allo stato finale.
	 */
	protected void added()
	{
		start_state.addExitingTransition(this);
		end_state.addEnteringTransition(this);
	}
	
	/**
	 * Rimuove la transizione da uno stato iniziale eliminato
	 */
	protected void startStateRemoved()
	{
		start_state.removeExitingTransition(this);
	}
	
	/**
	 * Aggiunge la transizione ad uno stato iniziale aggiunto nuovamente dopo la rimozione.
	 */
	protected void startStateAdded()
	{
		if (!start_state.getExiting_transitions().contains(this))
			
			start_state.addExitingTransition(this);
	}
	
	/**
	 * Rimuove la transizione da uno stato finale eliminato
	 */
	protected void endStateRemoved()
	{
		start_state.removeEnteringTransition(this);
	}
	
	/**
	 * Aggiunge la transizione ad uno stato finale aggiunto nuovamente dopo la rimozione.
	 */
	protected void endStateAdded()
	{
		if (!end_state.getEntering_transitions().contains(this))
			
			end_state.addEnteringTransition(this);
	}

	public void setConversionError(boolean conversionError) {
		this.conversionError = conversionError;
	}

	public boolean getConversionError() {
		return conversionError;
	}
}
