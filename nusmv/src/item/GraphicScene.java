package item;


import java.util.Iterator;

import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.ItemSelectionMode;
import com.trolltech.qt.gui.QGraphicsItemInterface;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QGraphicsScene;

public class GraphicScene extends QGraphicsScene
{
	private int x;
	private int y;
	private int w;
	private int h;
	
	public GraphicScene(GraphicView view, QRect view_rect)
	{
		super(view);
		
		setSceneRect(new QRectF(view_rect.x(), view_rect.y(), view_rect.width()-20, view.height()-20));
		this.x = (int)sceneRect().x();
		this.y = (int)sceneRect().y();
		this.w = (int)sceneRect().width();
		this.h = (int)sceneRect().height();
		
		view.selected_rect.connect(this, "selectItems(QGraphicsRectItem)");
	}
	
	public void adjustSceneRect(QRect view_rect)
	{	
		QRectF items_rect = itemsBoundingRect();
		int i_x = (int)items_rect.x();
		int i_y = (int)items_rect.y();
		int i_w = (int)items_rect.width();
		int i_h = (int)items_rect.height();
		
		if (i_x + i_w > w)
		{
			w = i_x + i_w;
			setSceneRect(x, y, w, h);
		}
		else
		{
			w = Math.max(i_x + i_w, view_rect.width()-20);
			setSceneRect(x, y, w, h);
		}
		
		if (i_y + i_h > h)
		{
			h = i_y + i_h;
			setSceneRect(x, y, w, h);
		}
		else
		{
			h = Math.max(i_y + i_h, view_rect.height()-20);
			
			setSceneRect(x, y, w, h);			
		}
	}
	
	public void resizeScene(QRect view_rect)
	{
		QRectF items_rect = itemsBoundingRect();
		
		if (view_rect.width() > items_rect.width() + items_rect.x())
		{
			w = view_rect.width()-20;
		}
		else
		{
			w = (int) (items_rect.width() + items_rect.x());
		}
		if (view_rect.height() > items_rect.height() + items_rect.y())
		{
			h = view_rect.height()-20;
		}
		else
		{
			h = (int) (items_rect.height() + items_rect.y());
		}
		setSceneRect(x, y, w, h);
	}
	
	protected void selectItems(QGraphicsRectItem rect)
	{
		Iterator<QGraphicsItemInterface> it = items().iterator();
		
		while (it.hasNext())
		{
			QGraphicsItemInterface item = it.next();
			if (item.collidesWithItem(rect, ItemSelectionMode.IntersectsItemBoundingRect))
			{
				item.setSelected(true);		
			}
			else 
			{
				item.setSelected(false);				
			}
		}
	}
}
