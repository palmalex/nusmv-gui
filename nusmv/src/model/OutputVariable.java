/********************************************************************************
*                                                                               *
*   Module      :   OutputVariable.java                                         *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import java.util.ArrayList;
import java.util.List;

import view.InputVariableHookView;
import view.ModuleInstanceGraphicView;
import view.ModuleTreeView;
import view.ModuleWindowView;

/**
 * Classe relativa al modello di una variabile di uscita.
 * @author Silvia Lorenzini
 *
 */
public class OutputVariable extends Variable
{
	private List<InputVariable> exiting_variables;
	public Signal2<OutputVariable, ModuleInstanceGraphicView> copy_hook;
	public Signal1<InputVariableHookView> add_input;
	public Signal2<InputVariableHookView, ModuleInstance> add_input_to_instance;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public OutputVariable(Module module)
	{
		super(module);
		
		this.name = "out_" + count_index;
		
		this.exiting_variables = new ArrayList<InputVariable>(0);
		this.added = new Signal0();
		this.copy_hook = new Signal2<OutputVariable, ModuleInstanceGraphicView>();
		this.add_input = new Signal1<InputVariableHookView>();
		this.add_input_to_instance = new Signal2<InputVariableHookView, ModuleInstance>();
	}
	
	/**
	 * Costruttore.
	 * @param name nome della variabile.
	 * @param type tipo.
	 * @param values valori assumibili.
	 * @param initial_value valore iniziale.
	 * @param module modulo padre.
	 */
	public OutputVariable(String name, Type type, String values, String initial_value, Module module)
	{
		super(name, type, values, initial_value, module);
		
		this.exiting_variables = new ArrayList<InputVariable>(0);
		this.added = new Signal0();
		this.copy_hook = new Signal2<OutputVariable, ModuleInstanceGraphicView>();
		this.add_input = new Signal1<InputVariableHookView>();
		this.add_input_to_instance = new Signal2<InputVariableHookView, ModuleInstance>();
	}

	/**
	 * 
	 * @return la lista delle variabili in cui questa va come ingresso.
	 */
	public List<InputVariable> getExiting_variables()
	{
		return exiting_variables;
	}

	/**
	 * Imposta la lista di variabili cui va come ingresso.
	 * @param exitingVariables lista delle variabili di ingresso.
	 */
	public void setExiting_variables(List<InputVariable> exitingVariables)
	{
		exiting_variables = exitingVariables;
	}
	
	/**
	 * Crea una copia di sé.
	 * @param m modulo padre.
	 * @param m_view finestra relativa al modulo padre.
	 * @param m_tree vista albero del modulo padre.
	 * @param m_gview vista della variabile di istanza da cui è partita la copia.
	 * @return la copia di sé. 
	 */
	public OutputVariable copy(Module m, ModuleWindowView m_view, ModuleTreeView m_tree, ModuleInstanceGraphicView m_gview)
	{
		OutputVariable out = new OutputVariable(name, type, values, initial_value, m);
		to_copy.emit(out, m_view, m_tree, m_gview);
		out.added.emit();
		
		return out;
	}

	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	@Override
	protected void addAvailableIndex(int countIndex)
	{
		parent.setOutputCountAvailableInt(countIndex);
	}

	@Override
	protected int getCountIndex()
	{
		return parent.getOutputCount();
	}
}
