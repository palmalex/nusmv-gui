/********************************************************************************
*                                                                               *
*   Module      :   InputVariableTreeView.java                                  *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import model.InputVariable;
import model.Module;
import model.Variable;
import widget.TreeWidget;

import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;
import command.RemoveInputVariableCommand;

import dialog.RenameInputVariableDialog;

/**
 * Vista nell'albero di progetto relativa al modello di variaible di ingresso. 
 * @author Silvia Lorenzini
 *
 */
public class InputVariableTreeView extends VariableTreeView
{
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public InputVariableTreeView(InputVariable in, Module parent, TreeWidget tree)
	{
		super(in, tree);
		getParent(parent.getName());
		createMenu();
		
		setIcon(0, new QIcon("src/pixmap/input.png"));
		setText(0, "Input: " + in.getName());
	}
	
	/**
	 * Costruttore.
	 * @param var variaible d'ingresso.
	 * @param parent vista nell'albero di progetto del modulo padre.
	 * @param tree albero di progetto.
	 * @param menu menu della variabile d'uscita.
	 */
	public InputVariableTreeView(InputVariable var, ModuleTreeView parent, TreeWidget tree, QMenu menu)
	{
		super(var, tree, parent, menu);
		createMenu();
		
		setIcon(0, new QIcon("src/pixmap/input.png"));
		setText(0, "Input: " + var.getName());
	}
	
	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Aggiunge nello stack undo/redo il comando relativo alla cancellazione della variaible di ingresso.
	 * Segnale dall'albero.
	 */	
	@Override
	protected void delete(QTreeWidgetItem item)
	{
		if (item.equals(this))
			
			project_tree.getUndoStack().push(new RemoveInputVariableCommand((InputVariable)var));
	}
	
	/**
	 * Aggiunge nello stack undo/redo il comando relativo alla cancellazione della variaible di ingresso.
	 * Segnale da menu.
	 */
	protected void delete()
	{
		project_tree.getUndoStack().push(new RemoveInputVariableCommand((InputVariable)var));
	}

	/**
	 * Imposta l'elemento padre, dato il nome del modulo che contiene la variaible.
	 */
	@Override
	protected void getParent(String name)
	{
		QTreeWidgetItem root = project_tree.topLevelItem(0);
		for (int i = 0; i < root.childCount(); i++)
		{
			QTreeWidgetItem item = root.child(i);
			
			if (item.text(0).compareTo("Module: " + name) == 0)
			{
				parent = item;
			}
		}
	}

	/**
	 * Aggiorna la vista a seguito di un cambiamento del modello.
	 */
	@Override
	protected void propertiesChanged()
	{
		setText(0, "Input: " + var.getName());
	}

	/**
	 * A seguito della copia del modello di variabile locale viene richiesto di copiare
	 * la vista stessa.
	 */
	@Override
	protected void toCopy(Variable v, ModuleWindowView mView, ModuleTreeView mTree, ModuleInstanceGraphicView m_gview)
	{
		new InputVariableTreeView((InputVariable)v, mTree, project_tree, menu);
	}

	/**
	 * Esegue il dialog per la modifica del nome della variabile.
	 */
	protected void renameVar()
	{
		new RenameInputVariableDialog((InputVariable)var, project_tree.getUndoStack()).exec();
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
		delete.triggered.connect(this, "delete()");
		
		QAction rename = new QAction(tr("Rename"), menu);
		rename.triggered.connect(this, "renameVar()");
		
		menu.addAction(delete);
		menu.addAction(rename);
	}
}
