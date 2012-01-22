/**
 * 
 */
package dialog;

import util.WorkingTemp;
import view.FrameModuleWindowView;
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
	private FrameModuleWindowView main;
	
	public TraceDialog(QDialog parent, FrameModuleWindowView main, String filename) {
		mapper = new QSignalMapper(this);
		mapper.mappedString.connect(this,"dispatcher(String)");
		this.filename = filename;
		this.main = main;
		
		createLayout();
		setVisible(true);
		setModal(true);
		setWindowTitle("Trace");
		parent.setVisible(false);
	}
	
	private void createLayout() {
		QVBoxLayout layout = new QVBoxLayout(this);
		layout.addWidget(new TraceWidget(main, filename));
		this.setLayout(layout);
	}
	
	private void dispatcher(String event) {
		System.out.println("event : " + event);
	}
	
	

}
