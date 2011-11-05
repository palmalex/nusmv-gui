package dialog;

import item.GraphicView;
import model.Module;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;
import command.RenameModuleCommand;

public class ModuleRenameDialog extends QDialog
{
	private QLineEdit module_name;
	private QPushButton ok;
	private Module module;
	private GraphicView view;
	
	public ModuleRenameDialog(GraphicView view, Module module)
	{
		this.module = module;
		this.view = view;
		module_name = new QLineEdit(module.getName());

		setWindowTitle("Rename module");
		createLayout();
	}
	
	private void createLayout()
	{
		QGridLayout grid = new QGridLayout();
		
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
		view.getUndoStack().push(new RenameModuleCommand(module, module.getName(), module_name.displayText()));
		
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
