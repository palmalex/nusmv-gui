/********************************************************************************
*                                                                               *
*   Module      :   InputVariableGraphicView.java                               *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicView;
import model.InputVariable;
import model.Variable;

import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPolygon;
import command.RemoveInputVariableCommand;

import dialog.RenameInputVariableDialog;

/**
 * Vista grafica (rettangolo con punta entrante a sinistra) di una variabile di ingresso.
 * @author Silvia Lorenzini
 *
 */
public class InputVariableGraphicView extends VariableGraphicView
{
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * 
	 * Costruttore.
	 */
	public InputVariableGraphicView(InputVariable in, int x, int y, GraphicView view)
	{
		super(in, x, y, 75, 50, view);
		
		createMenu();
	}
	
	/**
	 * 
	 * @return la variabile.
	 */
	public Variable getInputVariable()
	{
		return variable;
	}

	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected void deleteVar()
	{
		view.getUndoStack().push(new RemoveInputVariableCommand((InputVariable)variable));		
	}

	protected void renameVar()
	{
		new RenameInputVariableDialog((InputVariable)variable, view.getUndoStack()).exec();
	}
	
	/**
	 * Disegna l'oggetto grafico.
	 */
	@Override
	protected void paintItem(QPainter painter)
	{
		painter.setBrush(new QColor(250, 250, 200));
		
		QPolygon p = new QPolygon();
		p.add(x, y);
		p.add(x + width, y);
		p.add(x + width, y + height);
		p.add(x , y + height);
		p.add(x + 10, y + height / 2);
		
		painter.drawPolygon(p);
	}

	/**
	 * Copia questa vista.
	 */
	@Override
	protected void toCopy(Variable v, ModuleWindowView mView, ModuleTreeView mTree, ModuleInstanceGraphicView m_gview)
	{
		new InputVariableGraphicView((InputVariable)v, x, y, mView.getView());
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
		delete.triggered.connect(this, "deleteVar()");
		
		QAction rename = new QAction(tr("Rename"), menu);
		rename.triggered.connect(this, "renameVar()");
		
		menu.addAction(delete);
		menu.addAction(rename);
	}
	
}
