/**
 * 
 */
package dialog;

import util.WorkingTemp;
import widget.TraceWidget;

import com.trolltech.qt.core.QSignalMapper;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QVBoxLayout;

/**
 * @author Alessio Palmieri
 *
 */
public class TraceDialog extends QDialog {
	private QSignalMapper mapper = new QSignalMapper();
	private WorkingTemp wrk = WorkingTemp.getInstance();
	private QDialog parent;
	private String filename;
	
	public TraceDialog(QDialog parent, String filename) {
		mapper = new QSignalMapper(this);
		mapper.mappedString.connect(this,"dispatcher(String)");
		this.filename = filename;
		
		createLayout();
		setVisible(true);
		setModal(true);	
		parent.setVisible(false);
	}
	
	private void createLayout() {
		QVBoxLayout layout = new QVBoxLayout(this);
		layout.addWidget(new TraceWidget(this, filename));
		this.setLayout(layout);
	}
	
	private void dispatcher(String event) {
		System.out.println("event : " + event);
	}
	
	

}
