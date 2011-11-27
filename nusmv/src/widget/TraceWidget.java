package widget;

import javax.swing.SwingWorker.StateValue;

import view.StateGraphicView;

import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QIODevice;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QTextBrowser;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.svg.QGraphicsSvgItem;
import com.trolltech.qt.svg.QSvgWidget;
import com.trolltech.qt.xmlpatterns.QXmlQuery;

public class TraceWidget extends QWidget {
	
	private ResultStateView stateBrowser;
	private QTextBrowser varBrowser;
	private QLineEdit queryLine;
	private String filename;
	private int currentState=0;
	
	public TraceWidget(QWidget parent, String filename) {
		super(parent);
		this.filename=filename;
		
		QGridLayout layout = new QGridLayout(this);
		stateBrowser = new ResultStateView("pippo",29,7);
		stateBrowser.setToolTip(tr("State diagram"));
		varBrowser = new QTextBrowser(this);
		varBrowser.setToolTip("variables");
		queryLine = new QLineEdit(this);
		queryLine.setText(tr(String.valueOf(currentState)));
		queryLine.returnPressed.connect(this,"goToState()");
	
	
		layout.addWidget(stateBrowser,1,1);
		layout.addWidget(varBrowser,1,2);
		layout.addWidget(queryLine,2,1,1,2);
		QFile file = new QFile(filename);
		if (file.open(QIODevice.OpenModeFlag.ReadOnly)) {
			varBrowser.setPlainText(file.readAll().toString());
		} else {
			varBrowser.setPlainText(tr("Error opening file : ") + filename);
		}
		
		goToState();
		
	}
	
	public void goToState(){
		//varBrowser.clear();
		
		
	}

}
