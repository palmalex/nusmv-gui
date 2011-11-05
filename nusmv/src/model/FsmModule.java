/********************************************************************************
*                                                                               *
*   Module      :   smModule.java                                               *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import java.util.ArrayList;
import java.util.List;

import view.FsmModuleTreeView;
import view.FsmModuleWindowView;
import view.ModuleInstanceGraphicView;
import widget.TreeWidget;

/**
 * Classe relativa al modello di un modulo Fsm.
 * @author Silvia Lorenzini
 *
 */
public class FsmModule extends Module
{
	private List<State> states;
	private Counter state_counter;
	
	public Signal1<State> inital_state_setted;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public FsmModule()
	{
		super();
		
		this.states = new ArrayList<State>(0);	
		this.state_counter = new Counter();	
		
		this.inital_state_setted = new Signal1<State>();
		
	}
	
	/**
	 * Costruttore
	 * @param name nome del modulo.
	 */
	public FsmModule(String name)
	{
		super(name);
		
		this.states = new ArrayList<State>(0);
		this.state_counter = new Counter();
		
		this.inital_state_setted = new Signal1<State>();
	}
	
	/**
	 * Costruttore.
	 * @param name nome del modulo.
	 * @param index indice del modulo.
	 * @param var_count contatore delle variabili locali contenute nel modulo.
	 * @param in_count contatore delle variabili di ingresso.
	 * @param out_count contatore delle variabili d'uscita.
	 */
	public FsmModule(String name, int index, Counter var_count, Counter in_count, Counter out_count)
	{
		super(name, index, var_count, in_count, out_count);
		
		this.states = new ArrayList<State>(0);
		this.state_counter = new Counter();
		
		this.inital_state_setted = new Signal1<State>();
	}

	/**
	 * Restituisce la lista degli stati.
	 * @return la lista degli stati.
	 */
	public List<State> getStates()
	{
		return states;
	}

	/**
	 * Imposta la lista degli stati.
	 * @param states lista degli stati da settare.
	 */
	public void setStates(List<State> states)
	{
		this.states = states;
	}
	
	/**
	 * Aggiunge uno stato alla lista.
	 * @param state stato da aggiungere
	 */
	public void addState(State state)
	{
		states.add(state);
		state.added.emit();
	}
	
	/**
	 * Rimuove uno stato dalla lista.
	 * @param state stato da rimuovere.
	 */
	public void removeState(State state)
	{
		states.remove(state);
		state.removed.emit();
	}

	/**
	 * Restituisce il primo indice disponibile pergli stati.
	 * @return l'intero utilizzabile oer l'indicizzazione degli stati.
	 */
	public int getState_count()
	{
		return state_counter.next();
	}
	
	/**
	 * Aggiunge un indice utilizzabile per il conteggio degli stati.
	 * @param number intero utilizzabile.
	 */
	public void setStateCountAvailableInt(int number)
	{
		state_counter.addAvailableInt(number);
	}

	/**
	 * Crea un nuovo FsmModule e vi aggiunge le copie degli elementi contenuti in questo.
	 */
	public Module copy(TreeWidget tree, ModuleInstanceGraphicView m_view)
	{
		FsmModule fm = new FsmModule(name + "." + copy_counter.next());
		FsmModuleWindowView fm_view = new FsmModuleWindowView(fm, tree);
		FsmModuleTreeView fm_tree = new FsmModuleTreeView(fm, tree);
		
		copyVariables(fm, fm_view, fm_tree, m_view);
		
		return fm;
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Rimuove il modulo dalla lista dei moduli.
	 */
	protected void removed()
	{
		moduleList().removeModule(this);
	}
	
	/**
	 * Aggiunge il modulo alla lista dei moduli.
	 */
	protected void added()
	{
		module_list.addFsmModule(this);
	}
}
