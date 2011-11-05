/********************************************************************************
*                                                                               *
*   Module      :   TransitionGraphicView.java                                  *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicItem;
import item.GraphicText;
import item.GraphicView;
import item.LinePosition;
import item.TransitionText;

import java.util.Iterator;

import model.Transition;
import xml.XmlCreator;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.BrushStyle;
import com.trolltech.qt.core.Qt.PenCapStyle;
import com.trolltech.qt.core.Qt.PenJoinStyle;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QGraphicsItem;
import com.trolltech.qt.gui.QGraphicsItemInterface;
import com.trolltech.qt.gui.QGraphicsSceneHoverEvent;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QPolygonF;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QPainter.RenderHint;
import command.RemoveTransitionCommand;

import dialog.TransitionConditionDialog;

/**
 * Vista grafica di una transizione (linea cubica).
 * @author Silvia Lorenzini
 *
 */
public class TransitionGraphicView extends QGraphicsItem
{
	private int sx;
	private int sy;
	private int ex;
	private int ey;
	private int c1x;
	private int c1y;
	private int c2x;
	private int c2y;
	
	private LinePosition start_pos;
	private LinePosition end_pos;
	private Transition transition;
	private TransitionText condition;
	private boolean drawing;
	private GraphicView view;
	private QMenu menu;
	private StateGraphicView last_item; 
	
	public Signal1<Boolean> transition_drawing;
	public Signal0 transition_lost;
	public Signal0 to_set_transition;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 * @param t transizione
	 * @param sx coordinata x di partenza
	 * @param sy coordinata y di partenza
	 * @param start_pos posizione di partenza
	 * @param view vista della scena grafica.
	 */
	public TransitionGraphicView(Transition t, int sx, int sy, LinePosition start_pos, GraphicView view)
	{
		super(null, view.getScene());
		
		this.transition = t;
		this.view = view;
		this.drawing = true;
		this.start_pos = start_pos;
		this.end_pos = null;
		this.sx = sx;
		this.sy = sy;
		this.ex = view.getMousePos().x();
		this.ey = view.getMousePos().y();
		condition = new TransitionText(this, "");
		condition.setVisible(true);
		last_item = null;
		setInitialControlPoints();
		setTextPosition();		
		setZValue(1);
		createMenu();
		connectSignals();		
		
		setAcceptsHoverEvents(true);
		
		this.transition_drawing = new Signal1<Boolean>();
		this.transition_lost = new Signal0();
		this.to_set_transition = new Signal0();
	}
	
	/**
	 * Costruttore.
	 * @param t transizione
	 * @param sx coordinata x di partenza.
	 * @param sy coordinata y di partenza.
	 * @param ex coordinata x di arrivo.
	 * @param ey coordinata y di arrivo.
	 * @param c1x coordinata x del primo punto di controllo.
	 * @param c1y coordinata y del primo punto di controllo.
	 * @param c2x coordinata x del secondo punto di controllo.
	 * @param c2y coordinata y del secondo punto di controllo.
	 * @param start_pos posizione di partenza.
	 * @param end_pos posizione di arrivo.
	 * @param view vista grafica.
	 */
	public TransitionGraphicView(Transition t, int sx, int sy, int ex, int ey, int c1x, int c1y, int c2x, int c2y, 
			LinePosition start_pos, LinePosition end_pos, GraphicView view)
	{
		super(null, view.getScene());
		
		this.transition = t;
		this.view = view;
		this.sx = sx;
		this.sy = sy;
		this.ex = ex;
		this.ey = ey;
		this.c1x = c1x;
		this.c1y = c1y;
		this.c2x = c2x;
		this.c2y = c2y;
		this.start_pos = start_pos;
		this.end_pos = end_pos;
		this.drawing = false;
		last_item = null;
		condition = new TransitionText(this, t.getCondition());
		if(transition.getConversionError()){
			condition.setDefaultTextColor(QColor.darkRed);
		}
		setTextPosition();
		condition.setVisible(true);
		
		this.transition_drawing = new Signal1<Boolean>();
		this.transition_lost = new Signal0();
		
		setZValue(1);
		createMenu();
		connectSignals();
		setAcceptsHoverEvents(true);		
	}
	
	
	/**
	 * Costruttore.
	 * @param t transizione
	 * @param sx coordinata x di partenza
	 * @param sy coordinata y di partenza
	 * @param start_pos posizione di partenza
	 * @param ex coordinata x di arrivo
	 * @param ey coordinata y di arrivo
	 * @param end_pos posizione di arrivo
	 * @param view vista della scena grafica.
	 */
	public TransitionGraphicView(Transition t, int sx, int sy, int ex, int ey,LinePosition start_pos,LinePosition end_pos, GraphicView view)
	{
		super(null, view.getScene());
		
		this.transition = t;
		this.view = view;
		this.sx = sx;
		this.sy = sy;
		this.ex = ex;
		this.ey = ey;

		this.start_pos = start_pos;
		this.end_pos = end_pos;
		setStartControlPoints();
		setEndControlPoints();
		this.drawing = false;
		last_item = null;
		condition = new TransitionText(this, t.getCondition());
		if(transition.getConversionError()){
			condition.setDefaultTextColor(QColor.darkRed);
		}
		setTextPosition();
		condition.setVisible(true);
		
		this.transition_drawing = new Signal1<Boolean>();
		this.transition_lost = new Signal0();
		
		setZValue(1);
		createMenu();
		connectSignals();
		setAcceptsHoverEvents(true);
	}
	/**
	 * @return il rettangolo contenente la transizione.
	 */
	@Override
	public QRectF boundingRect()
	{
		int psx = Math.min(sx, Math.min(ex, Math.min(c1x, c2x)));
		int psy = Math.min(sy, Math.min(ey, Math.min(c1y, c2y)));
		int pex = Math.max(sx, Math.max(ex, Math.max(c1x, c2x)));
		int pey = Math.max(sy, Math.max(ey, Math.max(c1y, c2y)));
		
		return new QRectF(psx, psy, pex-psx, pey-psy);
	}

	/**
	 * Disegna la cubica rappresentante la transizione.
	 */
	@Override
	public void paint(QPainter painter, QStyleOptionGraphicsItem arg1, QWidget arg2)
	{
		painter.setRenderHint(RenderHint.Antialiasing);
		
		QPen pen = new QPen();
		pen.setCapStyle(PenCapStyle.RoundCap);
		pen.setWidthF(1);
		pen.setJoinStyle(PenJoinStyle.RoundJoin);
		
		if (isSelected())
		{
			pen.setColor(QColor.red);
		}
		else
		{
			pen.setColor(QColor.black);
		}
		painter.setPen(pen);
		
		QPainterPath path = new QPainterPath();
		path.moveTo(sx, sy);
		path.cubicTo(c1x, c1y, c2x, c2y, ex, ey);
		painter.drawPath(path);
		
		path = new QPainterPath();
		path.moveTo(ex, ey);
		path.addPolygon(drawArrow());
		QBrush b = new QBrush(pen.color(), BrushStyle.SolidPattern);
		painter.setBrush(b);
		painter.drawPath(path);
	}
	
	public QPointF getStartPoint()
	{
		return new QPointF(sx, sy);
	}
	
	public QPointF getEndPoint()
	{
		return new QPointF(ex, ey);
	}
	
	public QPoint getCtrl1()
	{
		return new QPoint(c1x, c1y);
	}
	
	public QPoint getCtrl2()
	{
		return new QPoint(c2x, c2y);
	}
	
	public void setEndPoint(QPoint p)
	{
		ex = p.x();
		ey = p.y();
		setEndControlPoints();
	}
	
	public void setStartPoint(QPoint p)
	{
		sx = p.x();
		sy = p.y();
		setStartControlPoints();
	}
	
	public void setEndPosition(LinePosition p)
	{
		end_pos = p;
	}
	
	public String getEndPosintion()
	{
		return end_pos.toString();
	}
	
	public String getStartPosition()
	{
		return start_pos.toString();
	}	
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected void transitionRemoved()
	{
		setVisible(false);
	}
	
	protected void transitionAdded()
	{
		setVisible(true);
	}
	
	protected void conditionPressed()
	{
		setSelected(true);
		update();
	}
	
	protected void rightMouseClicked(int px, int py)
	{
		Iterator<QGraphicsItemInterface> it = scene().items(new QPointF(px, py)).iterator();
		while (it.hasNext())
		{
			if (it.next().getClass().getName().compareTo("view.StateGraphicView") == 0)
				
				return;
		}
		if (this.isUnderMouse())
			
			menu.exec(new QPoint(px, py));
	}
	
	/**
	 * Se sta disegnando la transizione e rilascia il mouse su uno stato allora viene
	 * effettuato il collegamento.
	 * @param pos posizione del mouse.
	 */
	protected void mouseReleased(QPointF pos)
	{
		if (drawing)
		{
			QGraphicsItemInterface item = view.getScene().itemAt(pos);
			StateGraphicView state = null;
			if (item != null)
			{		
				if (item.getClass().getName().compareTo("view.StateGraphicView") == 0 && ((StateGraphicView)item).mouseIsOnBorder(pos))
				{
					state = (StateGraphicView)item;
				}
				else if (item.getClass().getName().compareTo("item.GraphicText") == 0)
				{
					item = ((GraphicText)item).parentItem();
					if (((StateGraphicView)item).mouseIsOnBorder(pos))
					{
						state = (StateGraphicView)item;
					}
				}
				else
				{
					Iterator<QGraphicsItemInterface> it = scene().items(pos).iterator();
					
					while (it.hasNext())
					{
						item = it.next();
						if (item.getClass().getName().compareTo("view.StateGraphicView") == 0)
						{
							if (((StateGraphicView)item).mouseIsOnBorder(pos))
							{
								state = (StateGraphicView)item;
							}
							break;
						}
					}
				}
				if (state != null)
				{
					transition.setEnd_state(state.getState());
					state.setEndTransition(pos, this);
				}
				else
				{
					transition_lost.emit();
					scene().removeItem(this);
				}
			}
			else
			{
				transition_lost.emit();
				scene().removeItem(this);
			}
			drawing = false;
			transition_drawing.emit(false);
			Iterator<QGraphicsItemInterface> it = scene().selectedItems().iterator();
			
			while (it.hasNext())
			{
				item = it.next();
				
				if (item.getClass().getName().compareTo("view.StateGraphicView") == 0)
				{
					item.setSelected(false);
				}
				scene().update();
			}
		}
	}
	
	/**
	 * Gestisce il movimento del mouse durante l'aggiunta di una transizione.
	 * @param pos posizione del mouse.
	 */
	protected void movingMouse(QPoint pos)
	{
		if (drawing)
		{
			ex = pos.x();
			ey = pos.y();
			
			setInitialControlPoints();
			scene().update();
			
			QGraphicsItemInterface item = scene().itemAt(ex, ey);
			
			if (item != null)
			{
				if (last_item != item)
				{
					if (item.getClass().getName().compareTo("view.StateGraphicView") == 0)
						
						last_item = (StateGraphicView)item;
					
					else if (item.getClass().getName().compareTo("item.GraphicItem") == 0)
						
						last_item =(StateGraphicView)((GraphicItem)item).parentItem();
					
					else
					{
						Iterator<QGraphicsItemInterface> it = scene().items(new QPointF(pos)).iterator();
						
						while (it.hasNext())
						{
							item = it.next();
							if (item.getClass().getName().compareTo("view.StateGraphicView") == 0)
							{
								last_item = (StateGraphicView)item;
								break;
							}
						}
						if (last_item == null)
							
							return;
					}					
					last_item.checkMousePosition(new QPointF(pos));
					last_item.hoverEnterEvent(new QGraphicsSceneHoverEvent());
				}
				else
				{
					last_item.checkMousePosition(new QPointF(pos));
					last_item.hoverMoveEvent(new QGraphicsSceneHoverEvent());
				}
			}
			else if (item == null && last_item != null)
			{
				last_item.checkMousePosition(new QPointF(pos));
				last_item.hoverLeaveEvent(new QGraphicsSceneHoverEvent());
				last_item = null;
			}
		}
	}
	
	/**
	 * Fornisce le informazioni di layout in fase di salvataggio.
	 * @param xml oggetto che costruisce l'albero xml di salvataggio.
	 */
	protected void getViewInfo(XmlCreator xml)
	{
		xml.setViewObject(this);
	}
		
	protected void delete()
	{
		view.getUndoStack().push(new RemoveTransitionCommand(transition));
	}
	
	protected void setCondition()
	{
		new TransitionConditionDialog(transition).exec();
	}
	
	protected void conditionChanged()
	{
		condition.setText(transition.getCondition());
		setTextPosition();
		if(transition.getConversionError()){
			transition.setConversionError(false);
			condition.setDefaultTextColor(QColor.black);

		}
		
	}

	/********************************************************************************
	*                                                                               *
	*  					       PRIVATE FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	private QPolygonF drawArrow()
	{
		QPolygonF arrow = new QPolygonF();
		arrow.add(ex, ey);
		
		if (end_pos != null)
		{
			switch(end_pos)
			{
			case TOP:
				arrow.add(ex - 4, ey - 8);
				arrow.add(ex + 4, ey - 8);
				break;
			case BOTTOM:
				arrow.add(ex - 4, ey + 8);
				arrow.add(ex + 4, ey + 8);
				break;
			case LEFT:
				arrow.add(ex - 8, ey + 4);
				arrow.add(ex - 8, ey - 4);
				break;
			case RIGHT:
				arrow.add(ex + 8, ey + 4);
				arrow.add(ex + 8, ey - 4);
				break;
			} 
		}
		return arrow;
	}
	
	
	
	private void setEndControlPoints()
	{
		switch(end_pos)
		{
		case TOP:
			c2x = ex;
			c2y = ey - Math.max((int)(Math.abs((sy - ey)) * 0.8), 100);
			break;
		case BOTTOM:
			c2x = ex;
			c2y = ey + Math.max((int)(Math.abs((sy - ey)) * 0.8), 100);
			break;
		case LEFT: 
			c2x = ex - Math.max((int)(Math.abs((sx - ex)) * 0.8), 100);
			c2y = ey;
			break;
		case RIGHT:
			c2x = ex + Math.max((int)(Math.abs((sx - ex)) * 0.8), 100);
			c2y = ey;
			break;
		}
	}
	
	private void setStartControlPoints()
	{
		switch(start_pos)
		{
		case TOP:
			c1x = sx; 
			c1y = sy - Math.max((int)(Math.abs((sy - ey)) * 0.8), 100);
			break;
		case BOTTOM:
			c1x = sx; 
			c1y = sy + Math.max((int)(Math.abs((sy - ey)) * 0.8), 100);
			break;
		case LEFT:
			c1x = sx - Math.max((int)(Math.abs((sx - ex)) * 0.8), 100);
			c1y = sy;
			break;
		case RIGHT:
			c1x = sx + Math.max((int)(Math.abs((sx - ex)) * 0.8), 100);
			c1y = sy;
			break;
		}
	}
	
	

	private void setInitialControlPoints()
	{
		switch(start_pos)
		{
		case TOP:
			c1x = sx; 
			c1y = sy - Math.max((int)(Math.abs((sy - ey)) * 0.8), 100);
			c2x = ex;
			c2y = ey + (sy - ey) / 2;
			break;
		case BOTTOM:
			c1x = sx; 
			c1y = sy + Math.max((int)(Math.abs((sy - ey)) * 0.8), 100);
			c2x = ex;
			c2y = ey + (sy - ey) / 2;
			break;
		case LEFT:
			c1x = sx - Math.max((int)(Math.abs((sx - ex)) * 0.8), 100);
			c1y = sy;
			c2x = ex + (sx - ex) / 2;
			c2y = ey;
			break;
		case RIGHT:
			c1x = sx + Math.max((int)(Math.abs((sx - ex)) * 0.8), 100);
			c1x = Math.max(c1x, 100);
			c1y = sy;
			c2x = ex + (sx - ex) / 2;
			c2y = ey;
			break;
		}
	}
	
	
	
	protected void startStateMoved(int gapx, int gapy)
	{
		setStartPoint(new QPoint(sx + gapx, sy + gapy));
		setStartControlPoints();		
		setTextPosition();
		scene().update();
	}
	
	protected void endStateMoved(int gapx, int gapy)
	{
		setEndPoint(new QPoint(ex + gapx, ey + gapy));
		setEndControlPoints();
		setTextPosition();
		scene().update();
	}
	
	protected void startStateResized(int x, int y)
	{
		if (start_pos == LinePosition.RIGHT)
		{
			setStartPoint(new QPoint(x, sy));
		}
		else if (start_pos == LinePosition.BOTTOM)
		{
			setStartPoint(new QPoint(sx, y));
		}
		setStartControlPoints();		
		setTextPosition();
		scene().update();
	}
	
	protected void endStateResized(int x, int y)
	{
		if (end_pos == LinePosition.RIGHT)
		{
			setEndPoint(new QPoint(x, ey));
		}
		else if (end_pos == LinePosition.BOTTOM)
		{
			setEndPoint(new QPoint(ex, y));
		}
		setEndControlPoints();	
		scene().update();
	}
	
	private void connectSignals()
	{
		condition.pressed.connect(this, "conditionPressed()");
		transition.need_view_info.connect(this, "getViewInfo(XmlCreator)");
		transition.condition_changed.connect(this, "conditionChanged()");
		view.moving_mouse.connect(this, "movingMouse(QPoint)");
		view.mouse_released.connect(this, "mouseReleased(QPointF)");
		view.right_mouse_clicked.connect(this, "rightMouseClicked(int, int)");
		
		transition.removed.connect(this, "transitionRemoved()");
		transition.added.connect(this, "transitionAdded()");
		transition.start_state_moved.connect(this, "startStateMoved(int, int)");
		transition.end_state_moved.connect(this, "endStateMoved(int, int)");
		transition.start_state_resized.connect(this, "startStateResized(int, int)");
		transition.end_state_resized.connect(this, "endStateResized(int, int)");
	}
	
	private void createMenu()
	{
		menu = new QMenu();
		
		QAction delete = new QAction("Remove", menu);
		delete.setIcon(new QIcon("src/pixmap/delete.png"));
		delete.triggered.connect(this, "delete()");
		
		QAction condition = new QAction("Condition", menu);
		condition.triggered.connect(this, "setCondition()");
		
		menu.addAction(delete);
		menu.addAction(condition);
	}
	
	private void setTextPosition()
	{
		switch(start_pos)
		{
		case BOTTOM: case RIGHT:
			condition.setPos(sx + x(), sy + y());
			break;
		case TOP:
			condition.setPos(sx + x(), sy + y() - condition.height());
			break;
		case LEFT:
			condition.setPos(sx + x() - condition.width() - 4, sy + y());
			break;
		default:
			System.out.println("Errore: start_pos non definita!");
			break;
		}
	}
}
