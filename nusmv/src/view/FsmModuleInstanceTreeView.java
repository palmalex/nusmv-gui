/********************************************************************************
*                                                                               *
*   Module      :   FsmModuleInstanceTreeView.java                              *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicView;
import model.FsmModuleInstance;
import model.ModuleInstance;
import widget.TreeWidget;

import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;

import command.RemoveFsmModuleInstanceCommand;

/**
 * Vista nell'albero di progetto della variabile di istanza di un modulo fsm.
 * @author Silvia Lorenzini
 *
 */
public class FsmModuleInstanceTreeView extends ModuleInstanceTreeView
{
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public FsmModuleInstanceTreeView(ModuleInstance moduleInstance,
			TreeWidget tree, String modName, QMenu menu)
	{
		super(moduleInstance, tree, modName, menu);
		
		setIcon(0, new QIcon("src/pixmap/i_fsm_tree.png"));
		setText(0, "FSM Module instance");
	}
	
	/**
	 * Costruttore.
	 * @param instance variabile di istanza.
	 * @param parent elemento padre nell'albero di progetto.
	 * @param tree albero di progetto.
	 * @param menu menu di FsmModuleInstanceGraphicView
	 */
	public FsmModuleInstanceTreeView(ModuleInstance instance, QTreeWidgetItem parent, TreeWidget tree, QMenu menu)
	{
		super(instance, parent, tree, menu);
		
		setIcon(0, new QIcon("src/pixmap/i_fsm_tree.png"));
		setText(0, "FSM Module instance");
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Crea una copia di sé.
	 */
	@Override
	protected void copy(ModuleInstance mi, GraphicView view, FrameModuleTreeView fmTree)
	{
		new FsmModuleInstanceTreeView(mi, fmTree, mi.getProjectTree(), menu);
	}

	/**
	 * inserisce nello stack undo/redo il comando di eliminazione della variabile di istanza 
	 * del modulo fsm relativo a questa vista.
	 */
	@Override
	protected void delete(QTreeWidgetItem item)
	{
		if (item.equals(this))
		{
			project_tree.getUndoStack().push(new RemoveFsmModuleInstanceCommand((FsmModuleInstance)instance));		
		}
	}
}
