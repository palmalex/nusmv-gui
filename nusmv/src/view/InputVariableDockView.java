/********************************************************************************
*                                                                               *
*   Module      :   InputVariableDockView.java                                  *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import model.InputVariable;
import model.Variable;
import widget.TreeWidget;

import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;
import command.RemoveInputVariableCommand;

import dialog.RenameInputVariableDialog;

/**
 * Vista delle informazioni relative ad una variabile di ingresso.
 * @author Silvia Lorenzini
 *
 */
public class InputVariableDockView extends VariableDockView
{
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public InputVariableDockView(InputVariable in, TreeWidget input_tree)
	{
		super(in, input_tree);
		
		createMenu();
	}
	
	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Crea una copia di questa vista da aggiungere alla copia del modulo contenente questa vista.
	 */
	@Override
	protected void toCopy(Variable v, ModuleWindowView mView, ModuleTreeView mTree, ModuleInstanceGraphicView m_gview)
	{
		new InputVariableDockView((InputVariable)v, mView.getInputTree());
	}
	
	/**
	 * Aggiunge allo stack undo/redo il comando relativo alla cancellazione della variabile 
	 * di ingresso di questa vista.
	 */
	protected void deleteVar()
	{
		var_tree.getUndoStack().push(new RemoveInputVariableCommand((InputVariable)var));		
	}

	/**
	 * Aggiunge allo stack undo/redo il comando relativo al cambio di nome della variabile 
	 * di ingresso di questa vista.
	 */
	protected void renameVar()
	{
		new RenameInputVariableDialog((InputVariable)var, var_tree.getUndoStack()).exec();
	}

	/**
	 * Aggiunge allo stack undo/redo il comando relativo alla cancellazione della variabile 
	 * di ingresso di questa vista.
	 * Segnale dall'albero di progetto.
	 */
	@Override
	protected void delete(QTreeWidgetItem item)
	{
		if (item.equals(this))
		{	
			var_tree.getUndoStack().push(new RemoveInputVariableCommand((InputVariable)var));
		}
	}
	
	/********************************************************************************
	*                                                                               *
	*  						    PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	private void createMenu()
	{
		menu = new QMenu();
		
		QAction delete = new QAction(tr("Delete"), menu);
		delete.setIcon(new QIcon("src/pixmap/delete.png"));
		delete.triggered.connect(this, "deleteVar()");
		
		QAction rename = new QAction(tr("Rename"), menu);
		rename.triggered.connect(this, "renameVar()");
		
		menu.addAction(delete);
		menu.addAction(rename);
	}
}
