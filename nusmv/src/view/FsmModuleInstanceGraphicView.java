/********************************************************************************
*                                                                               *
*   Module      :   FsmModuleWindowView.java                                    *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicView;
import model.FsmModuleInstance;
import model.ModuleInstance;
import widget.TreeWidget;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QPainter;
import command.RemoveFsmModuleInstanceCommand;

/**
 * Classe relativa alla vista grafica (rettangolo stondato) di una variabile di istanza di un modulo Fsm.
 * @author Silvia Lorenzini
 *
 */
public class FsmModuleInstanceGraphicView extends ModuleInstanceGraphicView
{
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public FsmModuleInstanceGraphicView(FsmModuleInstance fmi, int x, int y, GraphicView view, TreeWidget tree)
	{
		super(fmi, x, y, 150, 95, view, tree);
		
		this.mod_name = fmi.getInstancedModule().getName();
		this.var_name = fmi.getName();
		this.text.setText(var_name + ": <<" + mod_name + ">>");
		
		createMenu(fmi);
		connectSignals(fmi);
	}
	
	/**
	 * Disegna il rettangolo stondato che rappresenta graficamente la variabile di istanza di un modulo fsm. 
	 */
	public void paintItem(QPainter painter)
	{
		painter.setBrush(new QColor(250, 240, 220));
		painter.drawRoundedRect(boundingRect(), R, R);
	}

	/**
	 * Verifica la posizione del mouse sull'oggetto e imposta i flag utilizzati nella scelta della forma
	 * del cursore.
	 */
	public void checkMousePosition(QPointF p)
	{
		double r = Math.min(width, height) * 0.2;
		double px = p.x();
		double py = p.y();
		
		if (px > x + width - r && py > y + height - r)
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
		new FsmModuleInstanceGraphicView((FsmModuleInstance)mi, x, y, view, project_tree);
	}

	/**
	 * Inserisce nello stack undo/redo il comando relativo all'eliminazione della variabile di istanza del modulo fsm
	 * corrispondente a questa vista.
	 */
	@Override
	protected void removeInstanceVar()
	{
		view.getUndoStack().push(new RemoveFsmModuleInstanceCommand((FsmModuleInstance)instance));
	}
}
