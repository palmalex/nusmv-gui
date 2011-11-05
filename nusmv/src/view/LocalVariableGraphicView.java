/********************************************************************************
*                                                                               *
*   Module      :   LocalVariableGraphicView.java                               *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicLine;
import item.GraphicView;

import java.util.Iterator;

import model.LocalVariable;
import model.Type;
import model.Variable;

import com.trolltech.qt.core.Qt.Key;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QGraphicsSceneMouseEvent;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QKeyEvent;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPainter;
import command.RemoveLocalVariableCommand;

import dialog.VariableOptionsDialog;

/**
 * Vista grafica (ellisse) di una variabile di ingresso.
 * @author Silvia Lorenzini
 *
 */
public class LocalVariableGraphicView extends VariableGraphicView
{	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public LocalVariableGraphicView(LocalVariable lv, int x, int y, GraphicView view)
	{
		super(lv, x, y, 50, 50, view);
		
		createMenu();
	}
	
	public void changeName(String name)
	{
		this.name = name;
		text.setPlainText(name);
		update(boundingRect());
	}
	
	public String getName()
	{
		return variable.getName();
	}
	
	public Type getType()
	{
		return variable.getType();
	}
	
	public String getValues()
	{
		return variable.getValues();
	}
	
	public String getInitVal()
	{
		return variable.getInitial_value();
	}
	
	@Override
	public void mouseDoubleClickEvent(QGraphicsSceneMouseEvent event)
	{
		if (event.button() == MouseButton.LeftButton)
		
			edit();
		
		super.mouseDoubleClickEvent(event);
	}
	
	@Override
	public void keyPressEvent(QKeyEvent k)
	{
		if (k.key() == Key.Key_Delete.value())
		{
			delete();
		}
		super.keyPressEvent(k);
	}
	
	public QMenu getMenu()
	{
		return menu;
	}
	
	/**
	 * Sposta le linee uscenti del passaggio di variabile in caso di ridimensionamento.
	 */
	@Override
	public void resize()
	{
		Iterator<GraphicLine> it = exit_lines.iterator();
		
		while (it.hasNext())
		{
			GraphicLine l = it.next();
			l.setStartPoint((int)(x + x()) + width/2, (int)(y + y()) + height/2);
		}
		super.resize();
	}
	
	public Variable getLocalVariable()
	{
		return variable;
	}
	
	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected void delete()
	{
		view.getUndoStack().push(new RemoveLocalVariableCommand((LocalVariable)variable));
	}
	
	protected void edit()
	{
		new VariableOptionsDialog(variable).exec();
	}
	
	@Override
	protected void paintItem(QPainter painter)
	{
		painter.setBrush(new QColor(250, 250, 200));
		painter.drawEllipse(boundingRect());		
	}

	@Override
	protected void toCopy(Variable v, ModuleWindowView mView, ModuleTreeView mTree, ModuleInstanceGraphicView m_gview)
	{
		new LocalVariableGraphicView((LocalVariable)v, x, y, mView.getView());
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
