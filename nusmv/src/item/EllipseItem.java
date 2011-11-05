package item;


import com.trolltech.qt.gui.QPainter;

public abstract class EllipseItem extends GraphicItem
{

	public EllipseItem(int x, int y, int w, int h, GraphicView view)
	{
		super(x, y, w, h, view);
	}

	@Override
	protected void paintItem(QPainter painter)
	{
		painter.drawEllipse(boundingRect());
	}
}
