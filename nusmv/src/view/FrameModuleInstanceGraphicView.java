/********************************************************************************
*                                                                               *
*   Module      :   FrameModuleInstanceGraphicView.java                         *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicView;
import model.FrameModuleInstance;
import model.ModuleInstance;
import widget.TreeWidget;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QPainter;
import command.RemoveFrameModuleInstanceCommand;

/**
 * Classe relativa alla vista grafica (rettangolo) di una variabile di istanza di un modulo Frame.
 * @author Silvia Lorenzini
 *
 */
public class FrameModuleInstanceGraphicView extends ModuleInstanceGraphicView
{
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public FrameModuleInstanceGraphicView(FrameModuleInstance fmi, int x, int y, GraphicView view, TreeWidget tree)
	{
		super(fmi, x, y, 150, 95, view, tree);
		
		this.mod_name = fmi.getInstancedModule().getName();
		this.var_name = fmi.getName();
		this.text.setText(var_name + ": <<" + mod_name + ">>");
		
		createMenu(fmi);
		connectSignals(fmi);
	}
	
	/**
	 * Disegna il rettangolo che rappresenta graficamente la variabile di istanza di un modulo frame. 
	 */
	public void paintItem(QPainter painter)
	{
		painter.setBrush(new QColor(250, 240, 220));
		painter.drawRect(boundingRect());
	}
	
	/**
	 * Verifica la posizione del mouse sull'oggetto e imposta i flag utilizzati nella scelta della forma
	 * del cursore.
	 */
	public void checkMousePosition(QPointF p)
	{
		double px = p.x();
		double py = p.y();
		
		if (px > x + width - R && py > y + height - R)
		{	
			mouse_on_tl_corner = true;
			mouse_on_center = mouse_on_border = false;
		}
		else if (px > x  && px < x + width && py > y  && py < y + height)
		{	
			mouse_on_center = true;
			mouse_on_border = mouse_on_tl_corner = false;
		}
	}

	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Crea una copia di questa vista.
	 */
	@Override
	protected void copy(ModuleInstance mi, GraphicView view, FrameModuleTreeView fm_tree)
	{
		new FrameModuleInstanceGraphicView((FrameModuleInstance)mi, x, y, view, project_tree);
	}

	/**
	 * Inserisce nello stack undo/redo il comando relativo all'eliminazione della variabile di istanza del modulo frame
	 * corrispondente a questa vista.
	 */
	@Override
	protected void removeInstanceVar()
	{
		view.getUndoStack().push(new RemoveFrameModuleInstanceCommand((FrameModuleInstance)instance));
	}
}
