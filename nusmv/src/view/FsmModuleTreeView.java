/********************************************************************************
*                                                                               *
*   Module      :   FsmModuleTreeView.java                                      *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import com.trolltech.qt.gui.QIcon;

import model.FsmModule;
import widget.TreeWidget;

/**
 * Vista nell'albero di progetto di un modulo fsm.
 * @author Silvia Lorenzini
 *
 */
public class FsmModuleTreeView extends ModuleTreeView
{
	/**
	 * Costruttore.
	 * @param module modulo fsm relativo alla vista.
	 * @param tree albero di progetto.
	 */
	public FsmModuleTreeView(FsmModule module, TreeWidget tree)
	{
		super(module, tree);

		setIcon(0, new QIcon("src/pixmap/fsm_module.png"));
	}

}
