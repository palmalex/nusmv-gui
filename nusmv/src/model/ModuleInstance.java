/********************************************************************************
*                                                                               *
*   Module      :   ModuleiInstance.java                                        *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import item.GraphicView;
import view.FrameModuleTreeView;
import view.ModuleInstanceGraphicView;
import widget.TreeWidget;
import xml.XmlCreator;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.gui.QTreeWidgetItem;

/**
 * Classe astratta relativa al modello di una variabile di istanza generica.
 * @author Silvia Lorenzini
 *
 */
public abstract class ModuleInstance extends QSignalEmitter
{
	public Signal1<String>  var_name_changed;
	public Signal1<Boolean> process_activated;
	public Signal0 added;
	public Signal0 removed;
	public Signal0 selected;
	public Signal2<ModuleInstance, Module> copied; //emesso quando viene chiesto di copiare un modulo
	public Signal3<ModuleInstance, GraphicView, FrameModuleTreeView> to_copy; // dice alle viste di copiarsi
	public Signal1<Module> copy_instance;
	public Signal1<ModuleInstanceGraphicView> duplicated; // dice alla vista hook degli input di duplicarsi..
	public Signal1<XmlCreator> need_view_info;
	
	protected String name;
	protected FrameModule parent_module;
	protected boolean process;
	protected final int var_index;
	protected Module instanced_module;
	protected TreeWidget tree;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public ModuleInstance(FrameModule parent, TreeWidget tree)
	{
		this.parent_module = parent;
		this.var_index = parent_module.getVar_count();
		this.name = "v_" + var_index;
		this.process = false;
		this.tree = tree;
		
		this.var_name_changed = new Signal1<String>();
		this.process_activated = new Signal1<Boolean>();
		this.copied = new Signal2<ModuleInstance, Module>();
		this.to_copy = new Signal3<ModuleInstance, GraphicView, FrameModuleTreeView>();
		this.copy_instance = new Signal1<Module>();
		this.need_view_info = new Signal1<XmlCreator>();
		
		removed = new Signal0();
		added = new Signal0();
		selected = new Signal0();
		duplicated = new Signal1<ModuleInstanceGraphicView>();
	}
	
	/**
	 * Costruttore
	 * @param parent modulo Frame padre.
	 * @param tree albero di progetto.
	 * @param module_index indice del modulo istanziato.
	 */
	public ModuleInstance(FrameModule parent, TreeWidget tree, int module_index)
	{
		this.parent_module = parent;
		this.var_index = parent_module.getVar_count();
		this.name = "v_" + var_index;
		this.process = false;
		this.tree = tree;
		
		this.var_name_changed = new Signal1<String>();
		this.process_activated = new Signal1<Boolean>();
		this.copied = new Signal2<ModuleInstance, Module>();
		this.to_copy = new Signal3<ModuleInstance, GraphicView, FrameModuleTreeView>();
		this.copy_instance = new Signal1<Module>();
		this.need_view_info = new Signal1<XmlCreator>();
		
		removed = new Signal0();
		added = new Signal0();
		selected = new Signal0();
		duplicated = new Signal1<ModuleInstanceGraphicView>();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		if (var_index != -1 && this.name.compareTo(name) != 0)
		{
			parent_module.setVarCountAvailableInt(var_index);
		}
		this.name = name;
		var_name_changed.emit(name);		
	}
	
	public FrameModule getParentModule()
	{
		return parent_module;
	}

	public boolean isProcess()
	{
		return process;
	}

	public void setProcess(Boolean process)
	{
		this.process = process;
		process_activated.emit(process);
	}
	
	public Module getInstancedModule()
	{
		return instanced_module;
	}
	
	public Module getInstanced_module()
	{
		return instanced_module;
	}

	public QTreeWidgetItem getRoot()
	{
		return tree.topLevelItem(0);
	}
	
	public TreeWidget getProjectTree()
	{
		return tree;
	}
	
	public int getVarIndex()
	{
		return var_index;
	}
	
	public void select()
	{
		selected.emit();
	}

	public abstract void duplicate(Integer x, Integer y, GraphicView view);
	
	public abstract void copyInstance(Module instanced_module, ModuleInstanceGraphicView m_view);
	
	public abstract void removeInstanceVar();
	
	public abstract void edit();
}
