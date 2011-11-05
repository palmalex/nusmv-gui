/********************************************************************************
*                                                                               *
*   Module      :   LocalVariableDockView.java                                  *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import model.LocalVariable;
import model.Variable;
import widget.TreeWidget;

import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;

import command.RemoveLocalVariableCommand;

/**
 * Vista delle informazioni relative ad una variabile locale.
 * @author Silvia Lorenzini
 *
 */
public class LocalVariableDockView extends VariableDockView
{
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	
	public LocalVariableDockView(TreeWidget tree, LocalVariable var, QMenu menu)
	{
		super(var, tree);
		this.menu = menu;
		
		var_tree.show_menu.connect(this, "showMenu()");
		var_tree.delete_item.connect(this, "delete(QTreeWidgetItem)");
		var_tree.itemClicked.connect(this, "itemClicked()");
	}
	
	/**
	 * Mostra il menu.
	 */
	public void showMenu()
	{
		if (var_tree.currentItem().equals(this))
		{
			var.selected.emit();
			menu.exec(QCursor.pos());
		}
	}
	
	/**
	 * Aggiunge allo stack undo/redo il comando relativo alla cancellazione della variabile 
	 * di ingresso di questa vista.
	 * Segnale dall'albero di progetto.
	 */
	public void delete(QTreeWidgetItem item)
	{
		if (item.equals(this))
			
			var_tree.getUndoStack().push(new RemoveLocalVariableCommand((LocalVariable)var));
	}
	
	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	/**
	 * Crea una copia di questa vista da aggiungere alla copia del modulo contenente questa vista.
	 */
	protected void toCopy(Variable v, ModuleWindowView m_view, ModuleTreeView m_tree, ModuleInstanceGraphicView m_gview)
	{
		new LocalVariableDockView(m_view.getLocalTree(), (LocalVariable)v, menu);
	}
}
