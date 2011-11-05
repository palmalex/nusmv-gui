package item;

import item.GraphicText.TextPosition;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.CursorShape;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.core.Qt.PenCapStyle;
import com.trolltech.qt.core.Qt.PenJoinStyle;
import com.trolltech.qt.core.Qt.PenStyle;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QGraphicsItem;
import com.trolltech.qt.gui.QGraphicsSceneHoverEvent;
import com.trolltech.qt.gui.QGraphicsSceneMouseEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QPainter.RenderHint;

public abstract class GraphicItem extends QGraphicsItem
{
	protected final int MIN_X_GAP = 30;
	protected final int MIN_Y_GAP = 30;
	protected final double R;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	protected boolean mouse_on_center;
	protected boolean mouse_on_border;
	protected boolean mouse_on_tl_corner;
	protected boolean dash_line;
	protected boolean resizing;
	protected boolean moving;
	protected GraphicView view;
	protected GraphicText text;
	
	public Signal0 resized;
	public Signal2<QPointF, QPointF> moved;
	
	public GraphicItem(int x, int y, int w, int h, GraphicView view)
	{		
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;		
		this.text = new GraphicText(this, "", TextPosition.CENTER);
		this.R = height * 0.2;
		
		mouse_on_center = false;
		mouse_on_border = false;
		mouse_on_tl_corner = false;
		resizing = false;
		moving = false;
		resized = new Signal0();
		
		setFlag(GraphicsItemFlag.ItemIsMovable, true);
		setFlag(GraphicsItemFlag.ItemIsSelectable);
		setFlag(GraphicsItemFlag.ItemIsFocusable);
		setAcceptHoverEvents(true);
		
		this.view = view;
		view.scene().addItem(this);
		this.resized = new Signal0();
		this.moved = new Signal2<QPointF, QPointF>();
		resized.connect(this, "resize()");
		
		resized.connect(view.view_changed);
		moved.connect(view.view_changed);
	}

	public void removeFromScene()
	{
		if (view.scene().items().contains(this))
		{
			view.scene().removeItem(this);
			view.getScene().adjustSceneRect(view.rect());
			view.view_changed.emit();
		}
	}
	
	public void addToScene()
	{
		if (!view.scene().items().contains(this))
		{
			view.scene().addItem(this);
			view.getScene().adjustSceneRect(view.rect());
			view.view_changed.emit();
		}
	}
	
	@Override
	public QRectF boundingRect()
	{
		return new QRectF(x, y, width, height);
	}

	@Override
	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget arg2)
	{
		painter.setRenderHint(RenderHint.Antialiasing);
		
		QPen pen = new QPen();
		pen.setCapStyle(PenCapStyle.RoundCap);
		pen.setWidthF(2);
		pen.setJoinStyle(PenJoinStyle.RoundJoin);
		painter.setPen(pen);
		
		if (dash_line)
		{	
			pen.setStyle(PenStyle.DashLine);
			painter.setPen(pen);
		}
		else
		{		
			pen.setStyle(PenStyle.SolidLine);
			painter.setPen(pen);
		}
		if (isSelected())
		{
			pen.setColor(QColor.red);
			painter.setPen(pen);
		}		
		if (mouse_on_center)
		{
			view.usingCursorShape(false);
		}
		else if (mouse_on_border)
		{
			view.setCursor(new QCursor(CursorShape.CrossCursor));
			view.usingCursorShape(true);
		}
		else if (mouse_on_tl_corner)
		{
			view.setCursor(new QCursor(CursorShape.SizeFDiagCursor));
			view.usingCursorShape(true);
		}
		paintItem(painter);
	}	
	
	@Override
	public void hoverEnterEvent(QGraphicsSceneHoverEvent event)
	{
		update(boundingRect());
		super.hoverEnterEvent(event);
	}
	
	@Override
	public void hoverLeaveEvent(QGraphicsSceneHoverEvent event)
	{
		mouse_on_center = false;
		mouse_on_border = false;
		mouse_on_tl_corner = false;
		view.usingCursorShape(false);
		update(boundingRect());
		super.hoverLeaveEvent(event);
	}
	
	@Override
	public void hoverMoveEvent(QGraphicsSceneHoverEvent e)
	{
		checkMousePosition(e.pos());
		update(boundingRect());
		super.hoverMoveEvent(e);
	}
	
	public abstract void checkMousePosition(QPointF p);
	
	@Override
	public void mousePressEvent(QGraphicsSceneMouseEvent e)
	{
		setZValue(3);
		if (e.button() == MouseButton.RightButton)
		{
			setSelected(true);
			showMenu();
		}
		if (mouse_on_center)
		{
			setFlag(GraphicsItemFlag.ItemIsMovable, true);
		}
		else
		{
			setFlag(GraphicsItemFlag.ItemIsMovable, false);
		}
		if (mouse_on_tl_corner)
		{
			resizing = true;
		}	
		super.mousePressEvent(e);
	}
	
	@Override
	public void mouseReleaseEvent(QGraphicsSceneMouseEvent e)
	{
		setZValue(2);
		if (resizing)
		{
			resizing = false;
		}
		else if (moving)
		{
			moving = false;
			
		}
		super.mouseReleaseEvent(e);
	}
	
	@Override
	public void mouseMoveEvent(QGraphicsSceneMouseEvent e)
	{
		int px = (int)e.pos().x();
		int py = (int)e.pos().y();
		
		if (resizing)
		{			
			if (mouse_on_tl_corner && px > x + width / 2)
			{
				width = Math.max(text.width() + MIN_X_GAP, px - x);
				height = Math.max(text.height() + MIN_Y_GAP, py - y);
				resized.emit();
				text.setTextPosition();
				scene().update();
			}
		}
		else if (mouse_on_center)
		{			
			moving = true;
			moved.emit(e.pos(), e.lastPos());
		}
		super.mouseMoveEvent(e);
	}
	
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
		update();
	}
	
	public GraphicText getText()
	{
		return text;
	}
	public GraphicView getView()
	{
		return view;
	}
	
	public void resize()
	{
		width = Math.max(width, text.width() + MIN_X_GAP);
		text.setTextPosition();
		if (scene() != null)
			
			scene().update();
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int width()
	{
		return width;
	}
	
	public int height()
	{
		return height;
	}
	
	public void setWidth(int w)
	{
		this.width = w;
	}
	
	public void setHeight(int h)
	{
		this.height = h;
	}
	
	public final double getRadius()
	{
		return R;
	}
	
	protected void added()
	{
		setVisible(true);
		view.view_changed.emit();
	}
	
	protected void removed()
	{
		setVisible(false);
		view.view_changed.emit();
	}
	
	public void scale(double factor)
	{
		scale(factor, factor);
		width *= factor;
		height *= factor;		
	}
	
	protected abstract void paintItem(QPainter painter);
	
	protected abstract void showMenu();
}
