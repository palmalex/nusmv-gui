package item;

import com.trolltech.qt.gui.QGraphicsPixmapItem;
import com.trolltech.qt.gui.QGraphicsSceneHoverEvent;
import com.trolltech.qt.gui.QPixmap;

public class PixmapItem extends QGraphicsPixmapItem
{
	public Signal0 mouse_on;
	
	public PixmapItem(String path)
	{
		super(new QPixmap(path));
		
		mouse_on = new Signal0();
		setAcceptHoverEvents(true);
	}
	
	@Override
	public void hoverEnterEvent(QGraphicsSceneHoverEvent event)
	{
		mouse_on.emit();
		super.hoverEnterEvent(event);
	}	
}
