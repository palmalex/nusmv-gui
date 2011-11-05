/********************************************************************************
*                                                                               *
*   Module      :   StateTreeView.java                                          *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import model.State;
import widget.TreeWidget;

import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;
import command.RemoveStateCommand;

/**
 * Vista nell'albero di progetto del modello di stato di una FSM.
 * @author Silvia Lorenzini
 *
 */
public class StateTreeView extends QTreeWidgetItem
{
	private QMenu menu;
	private TreeWidget project_tree;
	private QTreeWidgetItem parent;
	private State state;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 * @param state stato
	 * @param project_tree albero di progetto
	 * @param menu menu dello stato.
	 */
	public StateTreeView(State state, TreeWidget project_tree, QMenu menu)
	{
		this.state = state;
		this.project_tree = project_tree;
		this.menu = menu;
		getParent(state.getModule().getName());
		
		setText(0, "State: " + state.getName());
		setIcon(0, new QIcon("src/pixmap/state.png"));
		
		state.added.connect(this, "added()");
		state.removed.connect(this, "removed()");
		state.renamed.connect(this, "renamed()");
		
		project_tree.show_menu.connect(this, "showMenu()");
		project_tree.delete_item.connect(this, "delete(QTreeWidgetItem)");
	}
	
	public void showMenu()
	{
		if (project_tree.currentItem().equals(this))
		{	
			menu.exec(QCursor.pos());
		}
	}
	
	/**
	 * Aggiunge allo stack undo/redo il comando relativo alla cancellazione dello stato.
	 * Segnale da albero.
	 * @param item
	 */
	public void delete(QTreeWidgetItem item)
	{
		if (item.equals(this))
			
			project_tree.getUndoStack().push(new RemoveStateCommand(state));
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected void added()
	{
		if (parent != null)
		{
			parent.addChild(this);
		}
	}
	
	protected void removed()
	{
		if (parent != null)
		{
			parent.removeChild(this);
		}
	}
	
	protected void renamed()
	{
		setText(0, "State: " + state.getName());
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	private void getParent(String module_name)
	{
		QTreeWidgetItem root = project_tree.topLevelItem(0);
		
		for (int i = 0; i < root.childCount(); i++)
		{
			if (root.child(i).text(0).compareTo("Module: " + module_name) == 0)
			{	
				parent =  root.child(i);
				i = root.childCount();
			}
		}
	}
}
