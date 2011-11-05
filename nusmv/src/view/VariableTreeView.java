/********************************************************************************
*                                                                               *
*   Module      :   VariableTreeView.java                                       *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import model.Variable;
import widget.TreeWidget;

import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;

/**
 * Vista nell'albero di progetto relativa al modello di variaible generica.
 * @author Silvia Lorenzini
 *
 */
public abstract class VariableTreeView extends QTreeWidgetItem
{
	protected QMenu menu;
	protected TreeWidget project_tree;
	protected QTreeWidgetItem parent;
	protected Variable var;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/

	/**
	 * Costruttore.
	 * @param var variaible.
	 * @param tree albero di progetto.
	 */
	public VariableTreeView(Variable var, TreeWidget tree)
	{
		this.var = var;
		this.project_tree = tree;
		
		var.added.connect(this, "added()");
		var.removed.connect(this, "removed()");
		var.properties_changed.connect(this, "propertiesChanged()");
		var.selected.connect(this, "selected()");
		var.to_copy.connect(this, "toCopy(Variable, ModuleWindowView, ModuleTreeView, ModuleInstanceGraphicView)");
		project_tree.show_menu.connect(this, "showMenu()");
		project_tree.delete_item.connect(this, "delete(QTreeWidgetItem)");
		project_tree.itemClicked.connect(this, "itemClicked()");
	}
	
	/**
	 * Costruttore.
	 * @param var variaible.
	 * @param parent vista nell'albero di progetto del modulo padre.
	 * @param tree albero di progetto.
	 * @param menu menu della variabile d'uscita.
	 */
	public VariableTreeView(Variable var, TreeWidget tree, ModuleTreeView parent, QMenu menu)
	{
		this.var = var;
		this.project_tree = tree;
		this.parent = parent;
		this.menu = menu;
		
		var.added.connect(this, "added()");
		var.removed.connect(this, "removed()");
		var.properties_changed.connect(this, "propertiesChanged()");
		var.selected.connect(this, "selected()");
		var.to_copy.connect(this, "toCopy(Variable, ModuleWindowView, ModuleTreeView, ModuleInstanceGraphicView)");
		project_tree.show_menu.connect(this, "showMenu()");
		project_tree.delete_item.connect(this, "delete(QTreeWidgetItem)");
		project_tree.itemClicked.connect(this, "itemClicked()");
	}
	
	public Variable getVariable()
	{
		return var;
	}
	
	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Variabile aggiunta.
	 */
	protected void added()
	{
		if (parent != null)
		{
			parent.addChild(this);
		}
	}
	
	/**
	 * Variabile rimossa.
	 */
	protected void removed()
	{
		if (parent != null)
		{
			parent.removeChild(this);
		}
	}
	
	public void showMenu()
	{
		if (project_tree.currentItem().equals(this))
		{	
			var.selected.emit();
			menu.exec(QCursor.pos());
		}
	}
	
	protected void itemClicked()
	{
		if (project_tree.currentItem().equals(this))
			
			var.selected.emit();
	}

	protected void selected()
	{
		project_tree.clearSelection();
		
		setSelected(true);
	}
	
	protected abstract void getParent(String mod_name);
	
	protected abstract void delete(QTreeWidgetItem item);
	
	protected abstract void propertiesChanged();
	
	protected abstract void toCopy(Variable v, ModuleWindowView m_view, ModuleTreeView m_tree, ModuleInstanceGraphicView m_gview);
}
