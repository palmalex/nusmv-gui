package item;


import java.util.ArrayList;
import java.util.List;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.QGraphicsItem;
import com.trolltech.qt.gui.QGraphicsSceneDragDropEvent;
import com.trolltech.qt.gui.QGraphicsSceneHoverEvent;
import com.trolltech.qt.gui.QGraphicsSceneMouseEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPolygon;

public abstract class Triangle extends QGraphicsItem
{
	protected GraphicItem parent;
	protected boolean moveable;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	protected boolean to_up;
	protected boolean to_down;
	protected boolean to_left;
	protected boolean to_right;

	protected GraphicView view;
	protected GraphicHookText text;
	private boolean triangle_addeable;
	
	public Signal1<String> orientation_changed;
	
	public Triangle(GraphicItem parent, int x, int y, int w, int h, GraphicView view)
	{
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		setParentItem(parent);
		this.parent = parent;
		this.view = view;
		this.moveable = true;
		this.triangle_addeable = true;
		setAcceptHoverEvents(true);
		setAcceptDrops(true);
		
		to_up = to_down = to_left = to_right = false;
		view.moving_mouse.connect(this, "movingMouse(QPoint)");
		view.left_mouse_clicked.connect(this, "leftMouseClicked(int, int)");
		parent.resized.connect(this, "parentResized()");
		this.orientation_changed = new Signal1<String>();
	}
	
	public Triangle(GraphicItem parent, int x, int y, int w, int h, GraphicView view, String orientation)
	{
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		setParentItem(parent);
		this.parent = parent;
		this.view = view;
		this.moveable = false;
		this.triangle_addeable = false;
		setAcceptHoverEvents(true);
		setAcceptDrops(true);
		
		setOrientation(orientation);
		view.moving_mouse.connect(this, "movingMouse(QPoint)");
		view.left_mouse_clicked.connect(this, "leftMouseClicked(int, int)");
		if (parent != null)
			parent.resized.connect(this, "parentResized()");
		this.orientation_changed = new Signal1<String>();
	}
	
	public Triangle(GraphicItem parent, Triangle t)
	{
		this.parent = parent;
		this.x = (int)(t.getX() + parent.getX() - t.getParent().getX());
		this.y = (int)(t.getY() + parent.getY() - t.getParent().getY());
		this.width = t.width();
		this.height = t.height();
		setParentItem(parent);
		this.view = t.view;
		this.moveable = false;
		this.triangle_addeable = false;
		
		setAcceptHoverEvents(true);
		setAcceptDrops(true);
		
		to_up = t.to_up;
		to_down = t.to_down;
		to_right = t.to_right;
		to_left = t.to_left;
		
		view.moving_mouse.connect(this, "movingMouse(QPoint)");
		view.left_mouse_clicked.connect(this, "leftMouseClicked(int, int)");
		parent.resized.connect(this, "parentResized()");
		this.orientation_changed = new Signal1<String>();
		setVisible(true);
	}
	
	protected void drawUp(QPainter painter)
	{
		QPoint p1 = new QPoint(x, y + height);
		QPoint p2 = new QPoint(x + width, y + height);
		QPoint p3 = new QPoint(x + width/2, y);
		
		List<QPoint> points = new ArrayList<QPoint>(3);
		points.add(p1);
		points.add(p2);
		points.add(p3);
		painter.drawPolygon(new QPolygon(points));
	}
	
	protected void drawDown(QPainter painter)
	{
		QPoint p1 = new QPoint(x, y);
		QPoint p2 = new QPoint(x + width, y);
		QPoint p3 = new QPoint(x + width/2, y + height);
		
		List<QPoint> points = new ArrayList<QPoint>(3);
		points.add(p1);
		points.add(p2);
		points.add(p3);
		painter.drawPolygon(new QPolygon(points));
	}
	
	protected void drawLeft(QPainter painter)
	{
		QPoint p1 = new QPoint(x + width, y);
		QPoint p2 = new QPoint(x + width, y + height);
		QPoint p3 = new QPoint(x, y + height / 2);
		
		List<QPoint> points = new ArrayList<QPoint>(3);
		points.add(p1);
		points.add(p2);
		points.add(p3);
		painter.drawPolygon(new QPolygon(points));
	}
	
	protected void drawRight(QPainter painter)
	{
		QPoint p1 = new QPoint(x, y);
		QPoint p2 = new QPoint(x , y + height);
		QPoint p3 = new QPoint(x + width, y + height / 2);
		
		List<QPoint> points = new ArrayList<QPoint>(3);
		points.add(p1);
		points.add(p2);
		points.add(p3);
		painter.drawPolygon(new QPolygon(points));
	}
	
	protected void movingMouse(QPoint pos)
	{
		int mx = pos.x();
		int my = pos.y();
		
		if (moveable)
		{
			if (my < parent.getY() + parent.y())
			{				
				y = parent.getY() - height;
				if (!to_down)
				{
					changeOrientation("to_down");
				}
					
				moveHorizontally(mx);
			}
			else if (my > parent.getY() + parent.y() + parent.height())
			{
				y = parent.getY() + parent.height();
				if (!to_up)
				{
					changeOrientation("to_up");
				}
				
				moveHorizontally(mx);
			}
			else if (mx < parent.getX() + parent.x())
			{
				x = parent.getX() - width;
				if (!to_right)
				{
					changeOrientation("to_right");
				}
				
				moveVertically(my);
			}
			else if (mx > parent.getX() + parent.x() + parent.width)
			{
				x = parent.getX() + parent.width();
				if (!to_left)
				{	
					changeOrientation("to_left");				
				}
				
				moveVertically(my);
			}
			if (text != null)
			{
				text.parentMoving();
			}
			view.getScene().update();
			moveLines();
		}
	}
	
	private void moveHorizontally(int mx)
	{
		if (mx > parent.getX() + parent.x() + parent.width() - parent.getRadius())
		{
			x = parent.getX()  + parent.width() - (int)parent.getRadius() - width / 2;
		}
		else if (mx < parent.getX() + parent.x() + parent.getRadius())
		{
			x = parent.getX() + (int)parent.getRadius() - width / 2;
		}
		else
		{
			x = mx - (int)parent.x();
		}
	}
	
	private void moveVertically(int my)
	{
		if (my > parent.getY() + parent.y() + parent.height() - parent.getRadius())
		{
			y = parent.getY()  + parent.height() - (int)parent.getRadius() - height / 2;
		}
		else if (my < parent.getY() + parent.y() + parent.getRadius())
		{
			y = parent.getY()  + (int)parent.getRadius() - height / 2;
		}
		else
		{
			y = my - (int)parent.y();
		}
	}
	
	protected void leftMouseClicked(int x, int y)
	{
		if (moveable)
		{
			moveable = false;
			setFlag(GraphicsItemFlag.ItemIsMovable, false);
			setText();
			if (triangle_addeable)
				
				triangleAdded();
			
			triangle_addeable = false;
		}
	}
	
	private void setOrientation(String orientation)
	{
		if (orientation.compareTo("to_up") == 0)
		{
			to_up = true;
			to_down = to_left = to_right = false;
		}
		else if (orientation.compareTo("to_down") == 0)
		{
			to_down = true;
			to_up = to_left = to_right = false;
		}
		else if (orientation.compareTo("to_left") == 0)
		{
			to_left = true;
			to_up = to_down = to_right = false;
		}
		else if (orientation.compareTo("to_right") == 0)
		{
			to_right = true;
			to_up = to_left = to_down = false;
		}
		else
			
			to_right = to_up = to_left = to_down = false;
	}

	public abstract void setText();
	
	protected void parentResized()
	{
		if (to_up)
		{
			y = parent.getY() + parent.height();
			text.parentMoving();
			moveLines();
			
		}
		else if (to_left)
		{
			x = parent.getX() + parent.width();
			text.parentMoving();
			moveLines();
		}
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
	
	@Override
	public void mousePressEvent(QGraphicsSceneMouseEvent event)
	{
		if (event.button() == MouseButton.RightButton)
		{
			showMenu();
		}
		super.mousePressEvent(event);
	}
	
	@Override
	public void hoverEnterEvent(QGraphicsSceneHoverEvent event)
	{
		x -= 3;
		y -= 3;
		width += 6;
		height += 6;
		scene().update();
		super.hoverEnterEvent(event);
	}
	
	@Override
	public void dragEnterEvent(QGraphicsSceneDragDropEvent event)
	{
		x -= 3;
		y -= 3;
		width += 6;
		height += 6;
		scene().update();
		super.dragEnterEvent(event);
	}	
	
	@Override
	public void hoverLeaveEvent(QGraphicsSceneHoverEvent event)
	{
		x += 3;
		y += 3;
		width -= 6;
		height -= 6;
		scene().update();
		super.hoverLeaveEvent(event);
	}
	
	public int getX()
	{
		return x + (int)x();
	}
	
	public int getY()
	{
		return y + (int)y();
	}
	
	public GraphicItem getParent()
	{
		return parent;
	}
	
	public int width()
	{
		return width;
	}
	
	public int height()
	{
		return height;
	}
	
	public String getOrientation()
	{
		if (to_up)
		{
			return "to_up";
		}
		if (to_down)
		{
			return "to_down";
		}
		if (to_left)
		{
			return "to_left";
		}
		if(to_right)
		{
			return "to_right";
		}
		return "";
	}
	
	protected abstract void showMenu();
	
	protected void moveHook()
	{
		this.moveable = true;
	}
	
	protected abstract void changeOrientation(String new_orientation);
	
	protected abstract void moveLines();
	
	protected abstract void triangleAdded();
}
