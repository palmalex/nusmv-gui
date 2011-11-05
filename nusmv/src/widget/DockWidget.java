package widget;

import com.trolltech.qt.core.Qt.DockWidgetArea;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QDockWidget;
import com.trolltech.qt.gui.QMainWindow;

public class DockWidget extends QDockWidget
{
	public Signal0 closed;
	
	public DockWidget(QMainWindow parent, String title, DockWidgetArea area)
	{
		super(title, parent);
		
		setFeatures(DockWidgetFeature.DockWidgetClosable);
		parent.addDockWidget(area, this);
		setMinimumWidth(150);
		
		closed = new Signal0();
	}
	
	@Override
	protected void closeEvent(QCloseEvent event)
	{
		closed.emit();
		super.closeEvent(event);
	}
}
