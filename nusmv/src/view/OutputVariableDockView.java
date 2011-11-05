/********************************************************************************
*                                                                               *
*   Module      :   OutputVariableDockView.java                                 *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import model.OutputVariable;
import model.Variable;
import widget.TreeWidget;

import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;
import command.RemoveOutputVariableCommand;
import dialog.VariableOptionsDialog;

/**
 * Vista delle informazioni relative ad una variabile di uscita.
 * @author Silvia Lorenzini
 *
 */
public class OutputVariableDockView extends VariableDockView
{
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public OutputVariableDockView(OutputVariable out, TreeWidget output_tree)
	{
		super(out, output_tree);
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
		new OutputVariableDockView((OutputVariable)v, mView.getOutputTree());
	}
	
	/**
	 * Aggiunge allo stack undo/redo il comando relativo alla cancellazione della variabile 
	 * di ingresso di questa vista.
	 */
	protected void delete()
	{
		var_tree.getUndoStack().push(new RemoveOutputVariableCommand((OutputVariable)var));
	}
	
	/**
	 * Esegue la finestra per l'edit della variabile.
	 */
	protected void edit()
	{
		new VariableOptionsDialog(var).exec();
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
			
			var_tree.getUndoStack().push(new RemoveOutputVariableCommand((OutputVariable)var));
	}
	
	/********************************************************************************
	*                                                                               *
	*  						    PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	private void createMenu()
	{
		menu = new QMenu();
		
		QAction edit = new QAction(tr("&Edit"), menu);
		edit.triggered.connect(this, "edit()");
		
		menu.addAction(edit);
		
		QAction delete = new QAction(tr("&Delete"), menu);
		delete.setShortcut("Delete");
		delete.setIcon(new QIcon("src/pixmap/delete.png"));
		delete.triggered.connect(this, "delete()");
		
		menu.addAction(delete);
	}
}
