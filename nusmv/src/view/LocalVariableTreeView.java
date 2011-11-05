/********************************************************************************
*                                                                               *
*   Module      :   LocalVariableTreeView.java                                  *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import model.LocalVariable;
import model.Variable;
import widget.TreeWidget;

import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;
import command.RemoveLocalVariableCommand;

/**
 * Vista nell'albero di progetto relativa al modello di variaible locale. 
 * @author Silvia Lorenzini
 *
 */
public class LocalVariableTreeView extends VariableTreeView
{
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public LocalVariableTreeView(TreeWidget tree, LocalVariable var)
	{
		super(var, tree);
		
		getParent(var.getModule().getName());
		
		setIcon(0, new QIcon("src/pixmap/lv_tree.png"));
		setText(0, "Local variable: " + var.getName());
	}
	
	/**
	 * Costruttore.
	 * @param var variaible locale.
	 * @param parent vista nell'albero di progetto del modulo padre.
	 * @param tree albero di progetto.
	 * @param menu menu della variabile locale.
	 */
	public LocalVariableTreeView(LocalVariable var, ModuleTreeView parent, TreeWidget tree, QMenu menu)
	{
		super(var, tree, parent, menu);
		
		setIcon(0, new QIcon("src/pixmap/lv_tree.png"));
		setText(0, "Local variable: " + var.getName());
	}
	
	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Imposta l'elemento padre, dato il nome del modulo che contiene la variaible.
	 */
	protected void getParent(String mod_name)
	{
		QTreeWidgetItem root = project_tree.topLevelItem(0);
		for (int i = 0; i < root.childCount(); i++)
		{
			if (root.child(i).text(0).compareTo("Module: " + mod_name) == 0)
			{	
				parent =  root.child(i);
				i = root.childCount();
			}
		}
	}
		
	/**
	 * Aggiunge nello stack undo/redo il comando relativo alla cancellazione della variaible locale.
	 */
	protected void delete(QTreeWidgetItem item)
	{
		if (item.equals(this))
			
			project_tree.getUndoStack().push(new RemoveLocalVariableCommand((LocalVariable)var));
	}
	
	/**
	 * Aggiorna la vista a seguito di un cambiamento del modello.
	 */
	protected void propertiesChanged()
	{
		setText(0, "Local variable: " + var.getName());
	}

	/**
	 * A seguito della copia del modello di variabile locale viene richiesto di copiare
	 * la vista stessa.
	 */
	@Override
	protected void toCopy(Variable v, ModuleWindowView mView, ModuleTreeView mTree, ModuleInstanceGraphicView m_gview)
	{
		new LocalVariableTreeView((LocalVariable)v, mTree, project_tree, menu);
	}
}
