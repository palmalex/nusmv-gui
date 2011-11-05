package item;


import java.util.Iterator;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.CursorShape;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.core.Qt.PenStyle;
import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QGraphicsItemInterface;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QKeyEvent;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QUndoStack;

public class GraphicView extends QGraphicsView
{
	public Signal2<Integer, Integer> 	left_mouse_clicked;
	public Signal2<Integer, Integer>   right_mouse_clicked;
	public Signal1<QPoint> 				moving_mouse;
	public Signal1<QPointF>				mouse_released;
	public Signal0						view_changed;
	private GraphicScene	 			scene;
	private QMainWindow 				window;
	private int 						mouse_x;
	private int 						mouse_y;
	private QUndoStack undo_stack;
	private boolean cursor_shape_setted;
	private QCursor arrow_cursor;
	private QCursor add_object_cursor;
	private double scale_factor;
	private QPoint start_point;
	private QGraphicsRectItem selection_rect;
	private boolean left_pressed;
	public Signal1<QGraphicsRectItem> selected_rect;
	
	public GraphicView(QMainWindow parent)
	{
		super(parent);
		
		selected_rect = new Signal1<QGraphicsRectItem>();
		view_changed = new Signal0();
		window = parent;
		scene = new GraphicScene(this, rect());
		setScene(scene);
		cursor_shape_setted = false;
		this.undo_stack = new QUndoStack();
		arrow_cursor = new QCursor(CursorShape.ArrowCursor);
		add_object_cursor = null;
		scale_factor = 1;
		this.start_point = null;
		left_pressed = false;
		setSelectionRect();
		
		
		setHorizontalScrollBarPolicy(Qt.ScrollBarPolicy.ScrollBarAlwaysOn);
		setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOn);
		
		left_mouse_clicked = new Signal2<Integer, Integer>();
		left_mouse_clicked.connect(parent, "leftMouseClicked(int, int)");
		right_mouse_clicked = new Signal2<Integer, Integer>();
		this.moving_mouse = new Signal1<QPoint>();
		this.mouse_released = new Signal1<QPointF>();
	}
	
	private void setSelectionRect()
	{
		QPen pen = new QPen();
		pen.setColor(QColor.cyan);
		pen.setStyle(PenStyle.DashLine);
		QBrush b = new QBrush(new QColor(0, 255, 0, 10));
		
		this.selection_rect = new QGraphicsRectItem();
		scene.addItem(selection_rect);
		
		selection_rect.setPen(pen);
		selection_rect.setBrush(b);
	}
	
	@Override
	protected void mousePressEvent(QMouseEvent e)
	{
		if (e.button() == MouseButton.LeftButton)
		{
			QPoint p = mapFromScene(0, 0);
			left_mouse_clicked.emit((int)((e.x() - p.x())) , (int)((e.y() - p.y())));
			start_point = e.pos().subtract(p);
			if (scene.itemAt(new QPointF(start_point)) == null)
				left_pressed = true;
		}
		else if (e.button() == MouseButton.RightButton)
		{
			right_mouse_clicked.emit(e.globalX(), e.globalY());
		}
		super.mousePressEvent(e);
	}
	
	@Override
	protected void mouseReleaseEvent(QMouseEvent event)
	{
		mouse_released.emit(event.posF());
		left_pressed = false;
		selection_rect.setVisible(false);
		super.mouseReleaseEvent(event);
	}
	
	@Override
	protected void resizeEvent(QResizeEvent event)
	{
		super.resizeEvent(event);
		scene.resizeScene(rect());
	}
	
	@Override
	protected void mouseMoveEvent(QMouseEvent event)
	{
		mouse_x = event.x();
		mouse_y = event.y();
		window.statusBar().showMessage(mouse_x + ", " + mouse_y);
		moving_mouse.emit(event.pos());
		
		if (add_object_cursor != null)
		{
			setCursor(add_object_cursor);
		}
		else if (!cursor_shape_setted)
		{
			setCursor(arrow_cursor);
		}
		if (left_pressed)
		{
			QPoint p = event.pos().subtract(start_point);
			
			selection_rect.setRect(start_point.x(), start_point.y(), p.x(), p.y());
			selection_rect.setVisible(true);
			selected_rect.emit(selection_rect);
			scene.update();
		}
		super.mouseMoveEvent(event);
	}
	
	public void setArrowCursor()
	{
		setCursor(arrow_cursor);
	}
	
	
	public void setAddObjectCursor(QCursor cursor)
	{
		add_object_cursor = cursor;
	}
	
	public GraphicScene getScene()
	{
		return scene;
	}
	
	public QPoint getMousePos()
	{
		return new QPoint(mouse_x, mouse_y);
	}
	
	public QUndoStack getUndoStack()
	{
		return undo_stack;
	}
	
	public void usingCursorShape(boolean b)
	{
		cursor_shape_setted = b;
	}
	
	public void multiplyScaleFactor(double scale)
	{
		scale_factor *= scale;
	}
	
	public void setScaleFactor(double scale_factor)
	{
		this.scale_factor = scale_factor;
	}
	
	public void zoomOut()
	{
		scale(0.8, 0.8);
	}
	
	public void zoomIn()
	{
		scale(1.25, 1.25);
	}
	@Override
	protected void keyPressEvent(QKeyEvent k)
	{
		Iterator<QGraphicsItemInterface> it = scene.selectedItems().iterator();
		
		while (it.hasNext())
		{
			it.next().keyPressEvent(k);
		}
		super.keyPressEvent(k);
	}
}
