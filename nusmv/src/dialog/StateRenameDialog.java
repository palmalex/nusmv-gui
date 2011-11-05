package dialog;

import model.State;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QUndoStack;
import com.trolltech.qt.gui.QVBoxLayout;
import command.RenameStateCommand;

public class StateRenameDialog extends QDialog
{
	private QLineEdit name;
	private State state;
	private QUndoStack stack;
	
	public StateRenameDialog(State state, QUndoStack stack)
	{
		this.state = state;
		this.stack = stack;
		
		this.name = new QLineEdit(state.getName());
		name.selectAll();
				
		setWindowTitle("State option dialog");
		createLayout();
	}
	
	private void createLayout()
	{
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("State name: "), 0, 0);
		grid.addWidget(name, 0, 1);
				
		QDialogButtonBox box = new QDialogButtonBox();
		
		QPushButton ok = new QPushButton("Ok");
		
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
		stack.push(new RenameStateCommand(state, state.getName(), name.displayText()));
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
