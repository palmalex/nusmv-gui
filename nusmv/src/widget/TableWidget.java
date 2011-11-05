package widget;

import com.trolltech.qt.core.Qt.Key;
import com.trolltech.qt.gui.QFontMetrics;
import com.trolltech.qt.gui.QKeyEvent;
import com.trolltech.qt.gui.QTableWidget;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QWidget;

public class TableWidget extends QTableWidget
{
	public Signal1<Integer> cancelled_item;
	private int max_width;
	
	public TableWidget(QWidget parent, int col_size)
	{
		super(parent);
		max_width = 150;
		setColumnCount(col_size);
		verticalHeader().hide();
		horizontalHeader().hide();
		cancelled_item = new Signal1<Integer>();
	}
	
	@Override
	protected void keyPressEvent(QKeyEvent k)
	{
		if (k.key() == Key.Key_Delete.value())
		{
			removeRow(currentRow());
		}
		super.keyPressEvent(k);
	}
	
	public void setColumnWidth(int index)
	{
		QTableWidgetItem item = item(index, columnCount()-1);
		String text = item.text();
		QFontMetrics fm = new QFontMetrics(font());
		int text_width = fm.width(text);
		
		max_width = Math.max(max_width, text_width);
		setColumnWidth(columnCount()-1, max_width+10);
	}
	
	public void clearAll()
	{
		while (rowCount() > 0)
		{
			removeRow(0);
		}
	}
}
