/********************************************************************************
*                                                                               *
*   Module      :   LocalVariable.java                                          *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import view.ModuleTreeView;
import view.ModuleWindowView;

/**
 * Classe relativa al modello di variabile locale.
 * @author Silvia Lorenzini
 *
 */
public class LocalVariable extends Variable
{	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public LocalVariable(Module module)
	{
		super(module);
		removed.connect(this, "removed()");
	}
	
	/**
	 * Costruttore.
	 * @param name nome della variabile.
	 * @param type tipo.
	 * @param values valori assumibili.
	 * @param initial_value valore iniziale.
	 * @param module modulo padre.
	 */
	public LocalVariable(String name, Type type, String values, String initial_value, Module module)
	{
		super(name, type, values, initial_value, module);
	}
	
	/**
	 * Crea una copia di sé e la restituisce.
	 * @param m modulo padre.
	 * @param m_view vista finestra del modulo padre. 
	 * @param m_tree vista albero del modulo padre.
	 * @return la copia di sé.
	 */
	public LocalVariable copy(Module m, ModuleWindowView m_view, ModuleTreeView m_tree)
	{
		LocalVariable l = new LocalVariable(name, type, values, initial_value, m);
		to_copy.emit(l, m_view, m_tree, null);
		l.added.emit();
		
		return l;
	}

	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Aggiunge un indice utilizzabile dal modulo padre per conteggiare le viariabili locali.
	 */
	@Override
	protected void addAvailableIndex(int countIndex)
	{
		parent.setVarCountAvailableInt(countIndex);
	}

	/**
	 * Restituisce il primo indice del contatore delle variabili locali.
	 */
	@Override
	protected int getCountIndex()
	{
		return parent.getVar_count();
	}
	
	/**
	 * Rimuove sé stessa dal modulo padre.
	 */
	protected void removed()
	{
		parent.removeLocalVariable(this);
	}
}
