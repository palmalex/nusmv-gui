/********************************************************************************
*                                                                               *
*   Module      :   FrameModuleInstanceTreeView.java                            *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicView;
import model.FrameModuleInstance;
import model.ModuleInstance;
import widget.TreeWidget;

import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;
import command.RemoveFrameModuleInstanceCommand;

/**
 * Vista nell'albero di progetto della variabile di istanza di un modulo frame.
 * @author Silvia Lorenzini
 *
 */
public class FrameModuleInstanceTreeView extends ModuleInstanceTreeView
{

	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public FrameModuleInstanceTreeView(ModuleInstance moduleInstance,
			TreeWidget tree, String modName, QMenu menu)
	{
		super(moduleInstance, tree, modName, menu);
		
		setIcon(0, new QIcon("src/pixmap/i_frame_tree.png"));
		setText(0, "Frame Module instance");
	}
	
	/**
	 * Costruttore.
	 * @param instance variabile di istanza.
	 * @param parent elemento padre nell'albero di progetto.
	 * @param tree albero di progetto.
	 * @param menu menu di FrameModuleInstanceGraphicView
	 */
	public FrameModuleInstanceTreeView(ModuleInstance instance, QTreeWidgetItem parent, TreeWidget tree, QMenu menu)
	{
		super(instance, parent, tree, menu);
		
		setIcon(0, new QIcon("src/pixmap/i_frame_tree.png"));
		setText(0, "Frame Module instance");
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
	protected void copy(ModuleInstance mi, GraphicView view, FrameModuleTreeView fm_tree)
	{
		new FrameModuleInstanceTreeView(mi, fm_tree, mi.getProjectTree(), menu);
	}
	
	/**
	 * inserisce nello stack undo/redo il comando di eliminazione della variabile di istanza 
	 * del modulo frame relativo a questa vista.
	 */
	protected void delete(QTreeWidgetItem item)
	{
		if (item.equals(this))
		{
			project_tree.getUndoStack().push(new RemoveFrameModuleInstanceCommand((FrameModuleInstance)instance));
		}
	}

}
