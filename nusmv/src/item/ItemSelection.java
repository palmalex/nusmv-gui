package item;

import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.PenStyle;
import com.trolltech.qt.gui.QGraphicsItem;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;

public class ItemSelection extends QGraphicsItem
{
	private int x;
	private int y;
	private int width;
	private int height;
	
	public ItemSelection(QGraphicsItem parent, int x, int y, int w, int h)
	{
		this.x = x - 7;
		this.y = y - 7;
		this.width = w + 14;
		this.height = h + 14;
		
		setParentItem(parent);
	}

	@Override
	public QRectF boundingRect()
	{
		return new QRectF(x, y, width, height);
	}

	@Override
	public void paint(QPainter painter, QStyleOptionGraphicsItem option, QWidget arg2)
	{		
		QPen pen = new QPen(PenStyle.DashLine);
		painter.setPen(pen);
		painter.drawRect(x+3, y+3, width-7, height-7);
		
		pen.setWidth(4);
		painter.setPen(pen);
		painter.drawPoint(x+3, y+3);
		painter.drawPoint(x+width/2, y+3);
		painter.drawPoint(x+width-3, y+3);
		painter.drawPoint(x+3, y+height/2);
		painter.drawPoint(x+width-3, y+height/2);
		painter.drawPoint(x+3, y+height-3);
		painter.drawPoint(x+width/2, y+height-3);
		painter.drawPoint(x+width-3, y+height-3);
	}

}
