/********************************************************************************
*                                                                               *
*   Module      :   Specification.java                                          *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import java.util.Observable;

/**
 * Classe utilizzata per la definizione delle specifiche delle proprietà del sistema.
 * @author  silvia
 */
public class Specification extends Observable
{
	private String formula;
	private FormulaType type;
	private FrameModule parent;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public Specification(String formula, FormulaType type, FrameModule parent)
	{
		this.formula = formula;
		this.type = type;
		this.parent = parent;
	}

	/**
	 * 
	 * @return il modulo padre in cui sono specificate le formule.
	 */
	public FrameModule getParentModule()
	{
		return parent;
	}
	
	/**
	 * 
	 * @return la stringa contenente la formula.
	 */
	public String getFormula()
	{
		return formula;
	}

	/**
	 * Imposta la formula.
	 * @param formula
	 */
	public void setFormula(String formula)
	{
		this.formula = formula;
	}

	/**
	 * 
	 * @return il tipo (LTL o CTL) della formula.
	 */
	public FormulaType getType()
	{
		return type;
	}

	/**
	 * Imposta il tipo della formula.
	 * @param type
	 */
	public void setType(FormulaType type)
	{
		this.type = type;
	}
	
}
