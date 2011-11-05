/********************************************************************************
*                                                                               *
*   Module      :   ModuleInstanceTreeView.java                                 *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicView;
import model.ModuleInstance;
import widget.TreeWidget;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;

/**
 * Vista nell'albero di progetto della variabile di istanza di un modulo generico.
 * @author Silvia Lorenzini
 *
 */
public abstract class ModuleInstanceTreeView extends QTreeWidgetItem
{
	public Signal0	selected;
	protected  QMenu menu;
	protected TreeWidget project_tree;
	protected ModuleInstance instance;
	protected QTreeWidgetItem parent;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 * @param module_instance variaible di istanza
	 * @param tree albero di progetto.
	 * @param parent_mod_name nome del modulo padre.
	 * @param menu menu della variabile di istanza.
	 */
	public ModuleInstanceTreeView(ModuleInstance module_instance, TreeWidget tree, String parent_mod_name, QMenu menu)
	{		
		parent = getParent(tree.topLevelItem(0), parent_mod_name);
		this.project_tree = tree;
		this.menu = menu;
		this.instance = module_instance;
		
		selected = new Signal0();
		selected.connect(module_instance, "select()");
		module_instance.selected.connect(this, "selected()");	
		
		QTreeWidgetItem var = new QTreeWidgetItem();
		var.setText(0, "instance name: " + module_instance.getName());
		addChild(var);
		
		QTreeWidgetItem mod = new QTreeWidgetItem();
		mod.setText(0,"module name: " +  module_instance.getInstancedModule().getName());
		addChild(mod);
		
		module_instance.var_name_changed.connect(this, "varNameChanged(String)");
		module_instance.getInstancedModule().mod_name_changed.connect(this, "modNameChanged(String)");
		module_instance.removed.connect(this, "remove()");
		module_instance.added.connect(this, "add()");
		module_instance.to_copy.connect(this, "copy(ModuleInstance, GraphicView, FrameModuleTreeView)");
		project_tree.show_menu.connect(this, "showMenu()");
		project_tree.delete_item.connect(this, "delete(QTreeWidgetItem)");
	}
	
	/**
	 * Costruttore
	 * @param instance variabile di istanza
	 * @param parent vista albero del modulo padre.
	 * @param tree albero di progetto.
	 * @param menu menu dela variabile di istanza.
	 */
	public ModuleInstanceTreeView(ModuleInstance instance, QTreeWidgetItem parent, TreeWidget tree, QMenu menu)
	{
		this.parent = parent;
		this.project_tree = tree;
		this.menu = menu;
		this.instance = instance;
		
		selected = new Signal0();
		selected.connect(instance, "select()");
		instance.selected.connect(this, "selected()");		
		
		QTreeWidgetItem var = new QTreeWidgetItem();
		var.setText(0, "instance name: " + instance.getName());
		addChild(var);
		
		QTreeWidgetItem mod = new QTreeWidgetItem();
		mod.setText(0,"module name: " +  instance.getInstancedModule().getName());
		addChild(mod);
		
		instance.var_name_changed.connect(this, "varNameChanged(String)");
		instance.getInstancedModule().mod_name_changed.connect(this, "modNameChanged(String)");
		instance.removed.connect(this, "remove()");
		instance.added.connect(this, "add()");
		instance.to_copy.connect(this, "copy(ModuleInstance, GraphicView, FrameModuleTreeView)");
		project_tree.show_menu.connect(this, "showMenu()");
		project_tree.delete_item.connect(this, "delete(QTreeWidgetItem)");
		
		add();
	}
	
	public void varNameChanged(String name)
	{
		child(0).setText(0, "instance name: " +name);
	}
	
	public void modNameChanged(String name)
	{
		child(1).setText(0, "module name: " + name);
	}
	
	public void showMenu()
	{
		if (project_tree.currentItem().equals(this))
		{	
			selected.emit();
			menu.exec(new QPoint(QCursor.pos().x(), QCursor.pos().y()));
		}
	}
	
	public void selected()
	{
		for (int i = 0; i < project_tree.selectedItems().size(); i++)
		{
			project_tree.selectedItems().get(i).setSelected(false);
		}
		
		setSelected(true);
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected void remove()
	{
		if (parent != null)
			
			parent.removeChild(this);
	}
	
	protected void add()
	{
		if (parent != null)
		{
			parent.addChild(this);
		}
	}
	
	protected abstract void delete(QTreeWidgetItem item);
	
	protected abstract void copy(ModuleInstance mi, GraphicView view, FrameModuleTreeView fm_tree);
	
	/********************************************************************************
	*                                                                               *
	*  							PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	private QTreeWidgetItem getParent(QTreeWidgetItem root, String mod_name)
	{
		for (int i = 0; i < root.childCount(); i++)
		{
			if (root.child(i).text(0).compareTo("Module: " + mod_name) == 0)
				
				return root.child(i);
		}
		return null;
	}
}
