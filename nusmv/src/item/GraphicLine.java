package item;

import model.Variable;
import view.InputVariableGraphicView;
import view.InputVariableHookView;
import view.LocalVariableGraphicView;
import view.OutputVariableGraphicView;
import view.OutputVariableHookView;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.core.Qt.PenCapStyle;
import com.trolltech.qt.core.Qt.PenJoinStyle;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QGraphicsItem;
import com.trolltech.qt.gui.QGraphicsItemInterface;
import com.trolltech.qt.gui.QGraphicsSceneHoverEvent;
import com.trolltech.qt.gui.QGraphicsSceneMouseEvent;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QPainter.RenderHint;
import command.RemoveVarConnectionCommand;

public class GraphicLine extends QGraphicsItem
{
	protected int sx;
	protected int sy;
	protected int ex;
	protected int ey;
	
	protected Object start_obj;
	protected Object end_obj;
	
	private boolean drawing;
	
	public Signal2<GraphicLine, Object> removed;
	public Signal2<GraphicLine, Object> added;
	protected GraphicScene scene;
	private GraphicView view;
	private InputVariableHookView last_item;
	private QMenu menu;
	
	public GraphicLine(int sx, int sy, Object start_obj, GraphicView view)
	{
		super(null, view.getScene());
		this.view = view;
		this.scene = view.getScene();
		this.sx = sx;
		this.sy = sy;
		this.start_obj = start_obj;
		this.ex = view.getMousePos().x();
		this.ey = view.getMousePos().y();
		this.drawing = true;
		this.removed = new Signal2<GraphicLine, Object>();
		this.added = new Signal2<GraphicLine, Object>();
		this.last_item = null;
		setZValue(1);
		createMenu();
		
		view.mouse_released.connect(this, "mouseReleased(QPointF)");
		view.moving_mouse.connect(this, "movingMouse(QPoint)");
		removed.connect(this, "deleted()");
		added.connect(this, "added()");
	}
	
	public GraphicLine(int sx, int sy, int ex, int ey, Object start_obj, Object end_obj, GraphicView view)
	{
		super(null, view.getScene());
		this.view = view;
		this.scene = view.getScene();
		this.sx = sx;
		this.sy = sy;
		this.ex = ex;
		this.ey = ey;
		this.start_obj = start_obj;
		this.end_obj = end_obj;
		this.drawing = false;
		this.removed = new Signal2<GraphicLine, Object>();
		this.added = new Signal2<GraphicLine, Object>();
		this.last_item = null;
		setZValue(1);
		createMenu();
		
		removed.connect(this, "deleted()");
		added.connect(this, "added()");
	}

	@Override
	public QRectF boundingRect()
	{
		return new QRectF(sx, sy, ex-sx, ey-sy);
	}

	@Override
	public void paint(QPainter painter, QStyleOptionGraphicsItem arg1, QWidget arg2)
	{
		painter.setRenderHint(RenderHint.Antialiasing);
		
		QPen pen = new QPen();
		pen.setCapStyle(PenCapStyle.RoundCap);
		pen.setWidthF(1);
		pen.setJoinStyle(PenJoinStyle.RoundJoin);
		painter.setPen(pen);
		painter.drawLine(sx, sy, ex, ey);
	}
	
	private void createMenu()
	{
		this.menu = new QMenu();
		
		QAction delete = new QAction(tr("Delete"), menu);
		delete.setIcon(new QIcon("src/pixmap/delete.png"));
		delete.triggered.connect(this, "delete()");
		
		menu.addAction(delete);
	}
	
	@Override
	public void mousePressEvent(QGraphicsSceneMouseEvent event)
	{
		if (event.button() == MouseButton.RightButton)
		{
			menu.exec(QCursor.pos());
		}
		super.mousePressEvent(event);
	}
	
	protected void mouseReleased(QPointF pos)
	{
		if (drawing)
		{
			QGraphicsItemInterface item = scene.itemAt(pos);
			if (item == null || item.getClass().getName().compareTo("view.InputVariableHookView") != 0)
			{
				removed.emit(this, null);
			}
			else if (item != null)
			{
				item.hoverLeaveEvent(new QGraphicsSceneHoverEvent());
				end_obj = item;
				QPoint ep = ((InputVariableHookView)item).getHookPoint();
				setEndPoint(ep.x(), ep.y());
				Variable sv;
				if (start_obj.getClass().getName().compareTo("view.LocalVariableGraphicView") == 0)
				{
					sv = ((LocalVariableGraphicView)start_obj).getLocalVariable();
				}
				else if (start_obj.getClass().getName().compareTo("view.OutputVariableHookView") == 0)
				{
					sv = ((OutputVariableHookView)start_obj).getOutputVariable();
				}
				else if (start_obj.getClass().getName().compareTo("view.OutputVariableGraphicView") == 0)
				{
					sv = ((OutputVariableGraphicView)start_obj).getOutputVariable();
				}
				else
				{
					sv = ((InputVariableGraphicView)start_obj).getInputVariable();
				}
				Variable ev = ((InputVariableHookView)end_obj).getInputVariable();
				ev.edit(ev.getName(), sv.getType(), sv.getValues(), sv.getInitial_value());
				((InputVariableHookView)end_obj).addEnteringLine(this);
			}
			drawing = false;
		}
	}
	
	protected void movingMouse(QPoint pos)
	{
		if (drawing)
		{
			ex = pos.x();
			ey = pos.y();
			scene().update();
			QGraphicsItemInterface item = scene.itemAt(new QPointF(pos));
			if (item != null && item.getClass().getName().compareTo("view.InputVariableHookView") == 0)
			{
				if (item != last_item)
				{
					item.hoverEnterEvent(new QGraphicsSceneHoverEvent());
					last_item = (InputVariableHookView)item;
				}
			}
			else if (last_item != null)
			{
				last_item.hoverLeaveEvent(new QGraphicsSceneHoverEvent());
				last_item = null;
			}
		}
	}
	
	public void setStartPoint(int sx, int sy)
	{
		this.sx = sx;
		this.sy = sy;
	}
	
	public void setEndPoint(int ex, int ey)
	{
		this.ex = ex;
		this.ey = ey;
	}
	
	public int sx()
	{
		return sx;
	}
	
	public int sy()
	{
		return sy;
	}
	
	public int ex()
	{
		return ex;
	}
	
	public int ey()
	{
		return ey;
	}
	
	//se non aggancia o se cancello start o end
	protected void deleted()
	{
		setVisible(false);
		view.view_changed.emit();
	}
	
	protected void added()
	{
		setVisible(true);
		view.view_changed.emit();
	}
	
	//da menu
	protected void delete()
	{
		view.getUndoStack().push(new RemoveVarConnectionCommand(this));
		view.view_changed.emit();
	}
	
	public Object getEndObject()
	{
		return end_obj;
	}
	
	public Object getStartObject()
	{
		return start_obj;
	}
}
