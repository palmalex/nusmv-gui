/********************************************************************************
*                                                                               *
*   Module      :   InputVariable.java                                          *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import view.ModuleInstanceGraphicView;
import view.ModuleTreeView;
import view.ModuleWindowView;

import com.trolltech.qt.core.QPoint;

/**
 * Classe relativa al modello di una variabile di ingresso.
 * @author Silvia Lorenzini
 *
 */
public class InputVariable extends Variable
{
	private Variable entering_variable;
	
	public Signal2<InputVariable, ModuleInstanceGraphicView> copy_hook;
	public Signal1<QPoint> get_hook_point;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public InputVariable(Module module)
	{
		super(module);
		this.name = "in_" + count_index;
		setType(null);
		setValues(null);
		setInitVal(null);
		
		this.added = new Signal0();
		this.copy_hook = new Signal2<InputVariable, ModuleInstanceGraphicView>();
		this.get_hook_point = new Signal1<QPoint>();
	}
	
	/**
	 * Costruttore.
	 * @param module il modulo padre.
	 * @param entering_variable variabile passata come ingresso al modulo.
	 */ 
	public InputVariable(Module module, Variable entering_variable)
	{
		super(module);
		
		this.entering_variable = entering_variable;
		
		this.added = new Signal0();
		this.copy_hook = new Signal2<InputVariable, ModuleInstanceGraphicView>();
		this.get_hook_point = new Signal1<QPoint>();
	}
	
	/**
	 * Costruttore.
	 * @param name nome della variabile.
	 * @param type tipo.
	 * @param values valori assumibili.
	 * @param initial_value valore iniziale.
	 * @param module modulo padre.
	 */
	public InputVariable(String name, Type type, String values, String initial_value, Module module)
	{
		super(name, type, values, initial_value, module);
		
		this.added = new Signal0();
		this.copy_hook = new Signal2<InputVariable, ModuleInstanceGraphicView>();
		this.get_hook_point = new Signal1<QPoint>();
	}

	/**
	 * Restituisce la variabile passata in ingresso.
	 * @return la variabile di ingresso.
	 */
	public Variable getEntering_variable()
	{
		return entering_variable;
	}

	/**
	 * Imposta la variabile di ingresso.
	 * @param enteringVariable la variabile passata in ingresso.
	 */
	public void setEntering_variable(Variable enteringVariable)
	{
		entering_variable = enteringVariable;
	}
	
	/**
	 * Crea una copia di sé.
	 * @param m modulo padre.
	 * @param m_view finestra relativa al modulo padre.
	 * @param m_tree vista albero del modulo padre.
	 * @param m_gview vista della variabile di istanza da cui è partita la copia.
	 * @return la copia.
	 */
	public InputVariable copy(Module m, ModuleWindowView m_view, ModuleTreeView m_tree, ModuleInstanceGraphicView m_gview)
	{
		InputVariable in = new InputVariable(name, type, values, initial_value, m);
		to_copy.emit(in, m_view, m_tree, m_gview);
		in.added.emit();
		
		return in;
	}

	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Aggiunge un indice utilizzabile dal modulo padre per conteggiare le viariabili di ingresso.
	 */
	protected void addAvailableIndex(int countIndex)
	{
		parent.setInputCountAvailableInt(countIndex);
	}

	/**
	 * Restituisce il primo indice del contatore delle variabili di ingresso.
	 */
	protected int getCountIndex()
	{
		return parent.getInputCount();
	}
}
