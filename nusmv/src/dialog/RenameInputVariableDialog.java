package dialog;

import model.InputVariable;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QUndoStack;
import com.trolltech.qt.gui.QVBoxLayout;
import command.RenameInputVarCommand;

public class RenameInputVariableDialog extends QDialog
{
	private QLineEdit var_name;
	private QPushButton ok;
	
	private InputVariable var;
	private QUndoStack stack;
	
	public RenameInputVariableDialog(InputVariable var, QUndoStack stack)
	{
		this.var = var;
		this.stack = stack;
		
		var_name = new QLineEdit(var.getName());
		var_name.selectAll();

		setWindowTitle("Rename input variable dialog");
		createLayout();
	}
	
	private void createLayout()
	{
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("Variable name: "), 0, 0);
		grid.addWidget(var_name, 0, 1);
		
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
		stack.push(new RenameInputVarCommand(var, var.getName(), var_name.displayText()));
		
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
