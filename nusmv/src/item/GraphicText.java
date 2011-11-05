package item;

import com.trolltech.qt.gui.QFontDialog;
import com.trolltech.qt.gui.QFontMetrics;
import com.trolltech.qt.gui.QGraphicsTextItem;

public class GraphicText extends QGraphicsTextItem
{
	public enum TextPosition {CENTER, TOP, LEFT, NONE};
	
	private GraphicItem parent;
	private TextPosition text_position;
	
	public GraphicText(GraphicItem parent, String text, TextPosition text_position)
	{
		this.parent = parent;
		this.text_position = text_position;
		
		setParentItem(parent);
		setPlainText(text);
		setTextPosition();		
	}
	
	public GraphicText(GraphicItem parent, String text, int x, int y)
	{
		this.parent = parent;
		this.text_position = TextPosition.NONE;
		
		setPos(parent.boundingRect().x() + x, parent.boundingRect().y() + y);
		setParentItem(parent);
		setPlainText(text);
	}
	
	public void setFont()
	{
		QFontDialog.Result fontResult  = QFontDialog.getFont(font());
	    if (fontResult.ok) 
	    {
	        setFont(fontResult.font);
	        setTextPosition();
	    } 
	}
	
	public void setTextPosition()
	{
		double text_w = getTextWidth();
		double text_h = getTextHeight();
		double px = parent.boundingRect().x();
		double py = parent.boundingRect().y();
		double gap_x = (parent.boundingRect().width() - text_w) / 2;	
		
		switch(text_position)
		{
		case CENTER:

			double gap_y = (parent.boundingRect().height() - text_h) / 2;	
			setPos(px + gap_x, py + gap_y);
			break;
			
		case TOP:
			setPos(px + gap_x, py);
			break;
			
		case LEFT:
			gap_y = (parent.boundingRect().height() - text_h) / 2;	
			setPos(px + 10, py + gap_y);
			break;
		}			
	}
	
	public void setText(String text)
	{
		setPlainText(text);
		parent.resize();
		setTextPosition();
	}
	
	public int width()
	{
		QFontMetrics fm = new QFontMetrics(font());
		return  (int)fm.width(toPlainText());
	}
	
	public int height()
	{
		QFontMetrics fm = new QFontMetrics(font());
		return (int)fm.height();
	}
	
	public int getTextWidth()
	{
		QFontMetrics fm = new QFontMetrics(font());
		if (toPlainText().contains("\n"))
		{
			String ss[] = toPlainText().split("\n");
			
			int max= 0;
			for (int i = 0; i < ss.length; i++)
			{
				int text_width = 0;
				if ((text_width = fm.width(ss[i])) > max)
					
					max = text_width;
			}
			return max;
		}
		return fm.width(toPlainText());
	}
	
	public int getTextHeight()
	{
		QFontMetrics fm = new QFontMetrics(font());
		if (toPlainText().contains("\n"))
		{
			String ss[] = toPlainText().split("\n");
			return fm.height() * ss.length;
		}
		return fm.height();			
	}
}
