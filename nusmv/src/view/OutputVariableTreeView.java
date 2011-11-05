/********************************************************************************
*                                                                               *
*   Module      :   OutputVariableTreeView.java                                 *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import model.Module;
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
 * Vista nell'albero di progetto relativa al modello di variaible d'uscita. 
 * @author Silvia Lorenzini
 *
 */
public class OutputVariableTreeView extends VariableTreeView
{
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public OutputVariableTreeView(OutputVariable out, Module parent, TreeWidget tree)
	{
		super(out, tree);
		
		createMenu();
		
		getParent(parent.getName());
		
		setIcon(0, new QIcon("src/pixmap/output.png"));
		setText(0, "Output: " + out.getName());
		
	}
	
	/**
	 * Costruttore.
	 * @param var variaible d'uscita.
	 * @param parent vista nell'albero di progetto del modulo padre.
	 * @param tree albero di progetto.
	 * @param menu menu della variabile d'uscita.
	 */
	public OutputVariableTreeView(OutputVariable var, ModuleTreeView parent, TreeWidget tree, QMenu menu)
	{
		super(var, tree, parent, menu);
		createMenu();
		
		setIcon(0, new QIcon("src/pixmap/output.png"));
		setText(0, "Output: " + var.getName());
	}
	
	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Aggiunge nello stack undo/redo il comando relativo alla cancellazione della variaible di uscita.
	 * Segnale dall'albero.
	 */
	@Override
	protected void delete(QTreeWidgetItem item)
	{
		if (item.equals(this))
			
			project_tree.getUndoStack().push(new RemoveOutputVariableCommand((OutputVariable) var));
	}
	
	/**
	 * Aggiunge nello stack undo/redo il comando relativo alla cancellazione della variaible di uscita.
	 * Segnale da menu.
	 */
	protected void delete()
	{
		project_tree.getUndoStack().push(new RemoveOutputVariableCommand((OutputVariable) var));
	}
	
	/**
	 * Esegue il dialog per la modifica della variabile.
	 */
	protected void edit()
	{
		new VariableOptionsDialog(var).exec();
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
				parent =  item;
			}
		}
	}

	/**
	 * Aggiorna la vista a seguito di un cambiamento del modello.
	 */
	@Override
	protected void propertiesChanged()
	{
		setText(0, "Output variable: " + var.getName());
	}

	/**
	 * A seguito della copia del modello di variabile locale viene richiesto di copiare
	 * la vista stessa.
	 */
	@Override
	protected void toCopy(Variable v, ModuleWindowView mView, ModuleTreeView mTree, ModuleInstanceGraphicView m_gview)
	{
		new OutputVariableTreeView((OutputVariable)v, mTree, project_tree, menu);
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
