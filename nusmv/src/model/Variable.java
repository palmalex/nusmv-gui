/********************************************************************************
*                                                                               *
*   Module      :   Variable.java                                               *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import view.InputVariableHookView;
import view.ModuleInstanceGraphicView;
import view.ModuleTreeView;
import view.ModuleWindowView;
import xml.XmlCreator;

import com.trolltech.qt.QSignalEmitter;

/**
 * Classe astratta relativa al modello di variabile generica.
 * @author Silvia Lorenzini
 *
 */
public abstract class Variable extends QSignalEmitter
{
	protected String name;
	protected Type type;
	protected String values;
	protected String initial_value;
	protected Module parent;
	
	public Signal0 properties_changed;
	public Signal0 selected;
	public Signal0 added;
	public Signal0 removed;
	public Signal2<ModuleInstanceGraphicView, ModuleInstance> to_duplicate; // dice alle viste di copiarsi..
	public Signal4<Variable, ModuleWindowView, ModuleTreeView, ModuleInstanceGraphicView> to_copy;
	public Signal1<XmlCreator> need_view_info;
	public Signal1<XmlCreator> need_hook_view_info;
	public Signal1<InputVariableHookView> connect_to_input;
	
	protected final int count_index;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public Variable(Module module)
	{
		this.parent = module;
		count_index = getCountIndex();
		this.name = "v_" + count_index;
		this.type = Type.Boolean;
		this.values = "{0, 1}";
		this.initial_value = "0";
		this.to_duplicate = new Signal2<ModuleInstanceGraphicView, ModuleInstance>();
		this.to_copy = new Signal4<Variable, ModuleWindowView, ModuleTreeView, ModuleInstanceGraphicView>();
		this.need_view_info = new Signal1<XmlCreator>();
		this.need_hook_view_info = new Signal1<XmlCreator>();
		this.connect_to_input = new Signal1<InputVariableHookView>();
		
		removed = new Signal0();
		added = new Signal0();
		
		properties_changed = new Signal0();
		selected = new Signal0();
		
		removed.connect(this, "remove()");
	}
	
	/**
	 * Costruttore.
	 * @param name nome della variabile.
	 * @param type tipo.
	 * @param values valori assumibili.
	 * @param initial_value valore iniziale.
	 * @param module modulo padre.
	 */
	public Variable(String name, Type type, String values, String initial_value, Module module)
	{
		count_index = -1;
		this.name = name;
		this.type = type;
		this.values = values;
		this.initial_value = initial_value;
		this.parent = module;
		this.to_duplicate = new Signal2<ModuleInstanceGraphicView, ModuleInstance>();
		this.to_copy = new Signal4<Variable, ModuleWindowView, ModuleTreeView, ModuleInstanceGraphicView>();
		this.need_view_info = new Signal1<XmlCreator>();
		this.need_hook_view_info = new Signal1<XmlCreator>();
		this.connect_to_input = new Signal1<InputVariableHookView>();
		
		removed = new Signal0();
		added = new Signal0();
		
		properties_changed = new Signal0();
		selected = new Signal0();
		
		removed.connect(this, "remove()");
		
		NuSmvKeywords.checkName(name);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
		
		if (count_index >= 0)
		{
			addAvailableIndex(count_index);
		}
		NuSmvKeywords.checkName(name);
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	public String getValues()
	{
		return values;
	}

	public void setValues(String values)
	{
		this.values = values;
	}

	public String getInitial_value()
	{
		return initial_value;
	}

	public void setInitVal(String initialValue)
	{
		initial_value = initialValue;
	}

	public Module getModule()
	{
		return parent;
	}

	public void setModule(Module module)
	{
		this.parent = module;
	}
	
	public void remove()
	{
		if (count_index != -1)
		{
			addAvailableIndex(count_index);
		}
	}
	
	public void edit(String name, Type type, String values, String init)
	{
		if (this.name.compareTo(name) != 0)
		{
			setName(name);
		}
		setType(type);
		setValues(values);
		setInitVal(init);
		properties_changed.emit();
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected abstract void addAvailableIndex(int count_index);
	
	protected abstract int getCountIndex();
}
