package item;


import view.ModuleInstanceGraphicView;
import view.OutputVariableHookView;

import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.PenCapStyle;
import com.trolltech.qt.core.Qt.PenJoinStyle;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QStyleOptionGraphicsItem;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.gui.QPainter.RenderHint;

public abstract class TriangleOut extends Triangle
{

	public TriangleOut(GraphicItem parent, int x, int y, int w, int h, GraphicView view)
	{
		super(parent, x, y, w, h, view);
	}
	
	public TriangleOut(GraphicItem parent, int x, int y, int w, int h, GraphicView view, String orientation)
	{
		super(parent, x, y, w, h, view, orientation);
	}
	
	public TriangleOut(ModuleInstanceGraphicView moduleInstanceGraphicView,
			OutputVariableHookView out2)
	{
		super(moduleInstanceGraphicView, out2);
	}

	public QRectF boundingRect()
	{
		return new QRectF(x, y, width, height);
	}
	
	@Override
	public void paint(QPainter painter, QStyleOptionGraphicsItem arg1, QWidget arg2)
	{
		painter.setRenderHint(RenderHint.Antialiasing);
		
		QPen pen = new QPen();
		pen.setCapStyle(PenCapStyle.RoundCap);
		pen.setWidthF(2);
		pen.setJoinStyle(PenJoinStyle.RoundJoin);
		painter.setPen(pen);
		painter.setBrush(QColor.black);
		
		if (to_up)
			drawDown(painter);	
		else if (to_down)
			drawUp(painter);
		else if (to_left)
			drawRight(painter);
		else if (to_right)
			drawLeft(painter);
	}

	protected void changeOrientation(String new_orientation)
	{
		String old = "";
		if (to_left)
		{
			old = "to_right";
		}
		else if (to_right)
		{
			old = "to_left";
		}
		else if (to_up)
		{
			old = "to_down";
		}
		else if (to_down)
		{
			old = "to_up";
		}
		if (new_orientation.compareTo("to_up") == 0)
		{
			to_up = true;
			to_down = to_left = to_right = false;			
		}
		else if (new_orientation.compareTo("to_down") == 0)
		{
			to_down = true;
			to_up = to_left = to_right = false;
		}
		else if (new_orientation.compareTo("to_left") == 0)
		{
			to_left = true;
			to_up = to_down = to_right = false;
		}
		else if (new_orientation.compareTo("to_right") == 0)
		{
			to_right = true;
			to_up = to_left = to_down= false;
		}
		orientation_changed.emit(old);
	}
}
