package dialog;

import view.ModuleInstanceGraphicView;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;
import command.RenameInstanceCommand;

public class ModuleInstanceRenameDialog extends QDialog
{
	private QLineEdit instance_name;
	private QLineEdit module_name;
	private QPushButton ok;
	
	private ModuleInstanceGraphicView view;
	
	public ModuleInstanceRenameDialog(ModuleInstanceGraphicView view)
	{
		this.view = view;
		instance_name = new QLineEdit(view.getInstanceName());
		instance_name.selectAll();
		module_name = new QLineEdit(view.getModuleName());

		setWindowTitle("Rename instance variable");
		createLayout();
	}
	
	private void createLayout()
	{
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("Instance name: "), 0, 0);
		grid.addWidget(instance_name, 0, 1);
		
		grid.addWidget(new QLabel("Module name: "), 1, 0);
		grid.addWidget(module_name, 1, 1);
		
		QDialogButtonBox box = new QDialogButtonBox();
		
		ok = new QPushButton("Ok");
		
		QPushButton cancel = new QPushButton("Cancel");
		
		box.setOrientation(Qt.Orientation.Horizontal);
		box.addButton(ok, QDialogButtonBox.ButtonRole.ActionRole);
		box.addButton(cancel, QDialogButtonBox.ButtonRole.ActionRole);
		
		ok.clicked.connect(this, "okClicked()");
		cancel.clicked.connect(this, "cancelClicked()");
		
		QVBoxLayout vbox = new QVBoxLayout();
		
		vbox.addLayout(grid);
		vbox.addWidget(box);
		
		setLayout(vbox);
	}
	
	protected void okClicked()
	{		
		view.getView().getUndoStack().push(new RenameInstanceCommand
				(view, view.getModuleName(), view.getInstanceName(), module_name.displayText(), instance_name.displayText()));
		
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
