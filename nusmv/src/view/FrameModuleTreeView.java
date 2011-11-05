/********************************************************************************
*                                                                               *
*   Module      :   FrameModuleTreeView.java                                    *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import com.trolltech.qt.gui.QIcon;

import model.FrameModule;
import widget.TreeWidget;

/**
 * Vista nell'albero di progetto di un modulo frame.
 * @author Silvia Lorenzini
 *
 */
public class FrameModuleTreeView extends ModuleTreeView
{
	/**
	 * Costruttore.
	 * @param module modulo frame relativo alla vista.
	 * @param tree albero di progetto.
	 */
	public FrameModuleTreeView(FrameModule module, TreeWidget tree)
	{
		super(module, tree);
		
		setIcon(0, new QIcon("src/pixmap/frame_module.png"));		
	}
}
