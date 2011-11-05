package item;

import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QFontMetrics;
import com.trolltech.qt.gui.QGraphicsTextItem;

public class GraphicHookText extends QGraphicsTextItem
{
	private Triangle parent;
	
	public GraphicHookText(Triangle parent, String text)
	{
		this.parent = parent;
		
		setParentItem(parent);
		setPlainText(text);
		setPosition();
	}
	
	private void setPosition()
	{
		setFont(new QFont(font().family(), 7));
		QFontMetrics fm = new QFontMetrics(font());
		int h = fm.height();
		
		if (parent.to_up)
		{
			int px = parent.getX() + (parent.width - h) / 2;
			int py = (int)parent.getY() - 2;
			setPos(px, py);
			rotate(-90);
		}
		else if (parent.to_down)
		{
			int px = parent.getX() + (parent.width + h) / 2;
			int py = (int)parent.getY() + parent.height + 2;
			setPos(px, py);
			rotate(90);
		}
		else if (parent.to_left)
		{
			int w = fm.width(this.toPlainText());
			int px = parent.getX() - 4 - w;
			int py = (int)parent.getY() + (parent.height - h ) / 2;
			setPos(px, py);
		}
		else if (parent.to_right)
		{
			int px = parent.getX() + parent.width + 2;
			int py = (int)parent.getY() + (parent.height - h ) / 2;
			setPos(px, py);
		}
	}
	
	public void parentMoving()
	{
		QFontMetrics fm = new QFontMetrics(font());
		int h = fm.height();

		if (parent.to_up)
		{
			int px = parent.getX() + (parent.width - h) / 2;
			int py = (int)parent.getY() - 2;
			setPos(px, py);
		}
		else if (parent.to_down)
		{
			int px = parent.getX() + (parent.width + h) / 2;
			int py = (int)parent.getY() + parent.height + 2;
			setPos(px, py);
		}
		else if (parent.to_left)
		{
			int w = fm.width(this.toPlainText());
			int px = parent.getX() - 4 - w;
			int py = (int)parent.getY() + (parent.height - h ) / 2;
			setPos(px, py);
		}
		else if (parent.to_right)
		{
			int px = parent.getX() + parent.width + 2;
			int py = (int)parent.getY() + (parent.height - h ) / 2;
			setPos(px, py);
		}
	}
	
	public void orientationChanged(String old_orientation)
	{		
		if (old_orientation.compareTo("to_up") == 0)
		{
			if (parent.to_left)
			{
				rotate(-90);
			}
			else if (parent.to_right)
			{
				rotate(-90);
			}
			else
			{
				rotate(180);
			}
		}
		else if (old_orientation.compareTo("to_down") == 0)
		{
			if (parent.to_left)
			{
				rotate(90);
			}
			else if (parent.to_right)
			{
				rotate(90);
			}
			else
			{
				rotate(180);
			}
		}
		else if (old_orientation.compareTo("to_left") == 0)
		{
			if (parent.to_up)
			{
				rotate(-90);
			}
			else if (parent.to_down)
			{
				rotate(90);
			}
		}
		else if (old_orientation.compareTo("to_right") == 0)
		{
			if (parent.to_up)
			{
				rotate(-90);
			}
			else if (parent.to_down)
			{
				rotate(90);
			}
		}
	}
}
