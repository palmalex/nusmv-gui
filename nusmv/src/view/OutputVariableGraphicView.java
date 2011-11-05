/********************************************************************************
*                                                                               *
*   Module      :   OutputVariableGraphicView.java                              *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicView;
import model.OutputVariable;
import model.Variable;

import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPolygon;
import command.RemoveOutputVariableCommand;

import dialog.VariableOptionsDialog;

/**
 * Vista grafica (rettangolo con punta uscente a destra) di una variabile di uscita.
 * @author Silvia Lorenzini
 *
 */
public class OutputVariableGraphicView extends VariableGraphicView
{
	private OutputVariable out;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public OutputVariableGraphicView(OutputVariable out, int x, int y, GraphicView view)
	{
		super(out, x, y, 75, 50, view);
		createMenu();
		
		this.out = out;
	}
	
	/**
	 * 
	 * @return la variabile.
	 */
	public OutputVariable getOutputVariable()
	{
		return out;
	}

	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Disegna l'oggetto grafico.
	 */
	protected void paintItem(QPainter painter)
	{
		painter.setBrush(new QColor(250, 250, 200));
		
		QPolygon p = new QPolygon();
		p.add(x, y);
		p.add(x + width - 10, y);
		p.add(x + width, y + height / 2);
		p.add(x  + width - 10, y + height);
		p.add(x, y + height);
		
		painter.drawPolygon(p);
	}
	
	protected void delete()
	{
		view.getUndoStack().push(new RemoveOutputVariableCommand((OutputVariable)variable));
	}
	
	/**
	 * Esegue il widget per l'inserimento delle informazioni della varibile di uscita.
	 */
	protected void edit()
	{
		new VariableOptionsDialog(variable).exec();
	}

	/**
	 * Copia questa vista.
	 */
	@Override
	protected void toCopy(Variable v, ModuleWindowView mView, 	ModuleTreeView mTree, ModuleInstanceGraphicView m_gview)
	{
		new OutputVariableGraphicView((OutputVariable)v, x, y, mView.getView());
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
