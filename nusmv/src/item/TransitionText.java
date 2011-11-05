/********************************************************************************
*                                                                               *
*   Module      :   TransitionText.java                                         *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package item;

import java.util.Iterator;

import view.TransitionGraphicView;

import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.gui.QFontMetrics;
import com.trolltech.qt.gui.QGraphicsItemInterface;
import com.trolltech.qt.gui.QGraphicsSceneMouseEvent;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QGraphicsItem.GraphicsItemFlag;

public class TransitionText extends QGraphicsTextItem
{
	public Signal0 pressed;
	private String text;
	private final TransitionGraphicView transition;	
	
	public TransitionText(TransitionGraphicView t, String text)
	{
		super(text, t);
		this.transition = t;
		this.text = text;
		
		setZValue(5);
		acceptedMouseButtons();
		this.pressed = new Signal0();
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
		setPlainText(text);
	}

	public TransitionGraphicView getTransition()
	{
		return transition;
	}	
	
	public void setPosition(int x, int y)
	{
		setPos(x, y);
	}
	
	@Override
	public void mousePressEvent(QGraphicsSceneMouseEvent event)
	{
		Iterator<QGraphicsItemInterface> it = scene().selectedItems().iterator();
		
		while (it.hasNext())
		{
			it.next().setSelected(false);
		}
		
		transition.setFlag(GraphicsItemFlag.ItemIsSelectable, true);
		transition.setSelected(true);
		scene().update();
	}
	
	@Override
	public void mouseReleaseEvent(QGraphicsSceneMouseEvent event)
	{
		transition.setSelected(false);
		transition.setFlag(GraphicsItemFlag.ItemIsSelectable, false);
		scene().update();
	}
	
	public int width()
	{
		QFontMetrics fm = new QFontMetrics(font());
		return fm.width(text);
	}
	
	public int height()
	{
		QFontMetrics fm = new QFontMetrics(font());
		
		return fm.height();
	}
	
	public void parentMoving(int gapx, int gapy)
	{
		QPointF p1 = transition.getEndPoint().subtract(transition.getStartPoint());
		QPointF p2 = pos().subtract(transition.getStartPoint());

		gapx *= (int)(p2.x() / p1.x());
		gapy *= (int)(p2.y() / p1.y());
		setPos(x() + gapx, y() + gapy);
	}
}
