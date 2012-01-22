package widget;

import view.ModuleInstanceTreeView;
import view.ModuleTreeView;

import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.Qt.Key;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.QKeyEvent;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QUndoStack;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QPainter.RenderHint;

public class TreeWidget extends QTreeWidget
{	
	public Signal0 show_menu;
	public Signal1<QTreeWidgetItem> delete_item;
	
	private QUndoStack undo_stack;
	private int max_column_width;
	
	public TreeWidget(QWidget parent, QUndoStack stack)
	{
		super();
		this.undo_stack = stack;
		this.max_column_width = 150;
		itemClicked.connect(this, "itemClicked(QTreeWidgetItem, Integer)");
		show_menu = new Signal0();
		delete_item = new Signal1<QTreeWidgetItem>();
		setHeaderHidden(true);
		setColumnCount(2);
		setColumnWidth(0, max_column_width); 
	}
	
	public QUndoStack getUndoStack()
	{
		return undo_stack;
	}
	
	@Override
	protected void mouseDoubleClickEvent(QMouseEvent event)
	{
		QTreeWidgetItem item;
		
		if ((item = currentItem()).getClass().getSuperclass().getName().compareTo("view.ModuleTreeView") == 0)
		{
			((ModuleTreeView)item).double_click.emit();
		}
		
		super.mouseDoubleClickEvent(event);
	}
	
	@Override
	protected void mousePressEvent(QMouseEvent e)
	{
		if (e.button() == MouseButton.RightButton)
		{
			show_menu.emit();
		}
		super.mousePressEvent(e);
	}
	
	@Override
	protected void keyPressEvent(QKeyEvent k)
	{
		if (k.key() == Key.Key_Delete.value())
		{
			delete_item.emit(currentItem());
		}
		super.keyPressEvent(k);
	}
	
	protected void itemClicked(QTreeWidgetItem item, Integer i)
	{
		if (item.getClass().getSuperclass().getName().compareTo("view.ModuleInstanceTreeView") == 0)
		{
			((ModuleInstanceTreeView)item).selected.emit();
		}
	}
	
	@Override
	protected void drawBranches(QPainter painter, QRect rect, QModelIndex index)
	{
		painter.setRenderHint(RenderHint.Antialiasing);
		QPainterPath path = new QPainterPath();
		
		if (isExpanded(index))
		{
			path.moveTo(rect.x() + rect.width(), rect.y() + rect.height()/2 - 3);
			path.lineTo(rect.x() + rect.width() - 10, rect.y() + rect.height() / 2 -3);
			path.lineTo(rect.x() + rect.width() - 5, rect.y() + rect.height() / 2 + 3);
			path.lineTo(rect.x() + rect.width(), rect.y() + rect.height()/2 - 3);
			painter.drawPath(path);			
		}
		else if (index.model().hasChildren(index))
		{
			path.moveTo(rect.x() + rect.width(), rect.y() + rect.height()/2);
			path.lineTo(rect.x() + rect.width() - 6, rect.y() + rect.height() / 2 - 5);
			path.lineTo(rect.x() + rect.width() - 6, rect.y() + rect.height() / 2 + 5);
			path.lineTo(rect.x() + rect.width(), rect.y() + rect.height()/2);
			painter.drawPath(path);
		}
	}
}
