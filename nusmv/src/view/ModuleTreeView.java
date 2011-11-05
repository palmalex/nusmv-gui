/********************************************************************************
*                                                                               *
*   Module      :   ModuleTreeView.java                                         *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import model.Module;
import widget.TreeWidget;

import com.trolltech.qt.gui.QTreeWidgetItem;

/**
 * Vista nell'albero di progetto di un modulo generico.
 * @author Silvia Lorenzini
 *
 */
public class ModuleTreeView extends QTreeWidgetItem
{
	public Signal0 double_click;
	
	protected Module module;
	protected TreeWidget project_tree;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 * @param module modulo frame relativo alla vista.
	 * @param tree albero di progetto.
	 */
	public ModuleTreeView(Module module, TreeWidget tree)
	{
		setText(0, "Module: " + module.getName());
		
		this.project_tree = tree;
		this.module = module;
		
		double_click = new Signal0();
		double_click.connect(module.show_window);
		
		module.mod_name_changed.connect(this, "nameChanged(String)");
		module.removed.connect(this, "removed()");
		module.added.connect(this, "added()");
		
		project_tree.delete_item.connect(this, "delete(QTreeWidgetItem)");
	}
	
	public void nameChanged(String name)
	{
		setText(0, "Module: " + name);
	}
	
	public void added()
	{
		project_tree.topLevelItem(0).addChild(this);
	}
	
	public void removed()
	{
		project_tree.topLevelItem(0).removeChild(this);
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected void delete(QTreeWidgetItem item)
	{
		if (item.equals(this))
		{
			module.removed.emit();
		}
	}
}
