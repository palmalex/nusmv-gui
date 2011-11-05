package dialog;

import view.FrameModuleWindowView;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class CreateModelDialog extends QDialog
{
	private QLineEdit name;
	private QLineEdit dir;
	private FrameModuleWindowView f;
	private Signal1<Boolean> ok_enabled;
	
	public CreateModelDialog(FrameModuleWindowView f)
	{
		this.f = f;
		this.name = new QLineEdit();
		name.textChanged.connect(this, "textChanged()");
		this.dir = new QLineEdit();
		dir.textChanged.connect(this, "textChanged()");
		
		ok_enabled = new Signal1<Boolean>();
		createLayout();
	}
	
	private void createLayout()
	{
		setWindowTitle("New NuSMV graphic model");
		
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("Model name: "), 0, 0);
		grid.addWidget(name, 0, 1);
		
		QPushButton browse = new QPushButton("Browse");
		browse.pressed.connect(this, "browsePressed()");
		
		grid.addWidget(new QLabel("Directory: "), 1, 0);
		grid.addWidget(dir, 1, 1);		
		grid.addWidget(browse, 1, 2);
		
		QDialogButtonBox box = new QDialogButtonBox();
		
		QPushButton ok = new QPushButton("Ok");
		QPushButton cancel = new QPushButton("Cancel");
		
		box.setOrientation(Qt.Orientation.Horizontal);
		box.addButton(ok, QDialogButtonBox.ButtonRole.ActionRole);
		box.addButton(cancel, QDialogButtonBox.ButtonRole.ActionRole);
		
		ok.clicked.connect(this, "okClicked()");
		ok.setEnabled(false);
		ok_enabled.connect(ok, "setEnabled(boolean)");
		cancel.clicked.connect(this, "cancelClicked()");
		
		QVBoxLayout vbox = new QVBoxLayout();
		
		vbox.addLayout(grid);
		vbox.addWidget(box);
		
		setLayout(vbox);
	}
	
	protected void browsePressed()
	{
		String f = "Graphic model file (*.gmf);;Any file(*)";
		
		dir.setText(QFileDialog.getSaveFileName(null, "Save new model", name.displayText() + ".gmf", new QFileDialog.Filter(f)));
	}
	
	protected void textChanged()
	{
		if (name.displayText().compareTo("") != 0 && dir.displayText().compareTo("") != 0)
			
			ok_enabled.emit(true);
		
		else
			
			ok_enabled.emit(false);
	}
	
	protected void okClicked()
	{
		f.setEmptyModel(name.displayText(), dir.displayText());

		setResult(DialogCode.Accepted.value());
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
