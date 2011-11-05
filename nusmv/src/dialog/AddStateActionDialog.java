package dialog;

import model.State;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QButtonGroup;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QRadioButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class AddStateActionDialog extends QDialog
{
	private State state;
	private QButtonGroup actions;
	private QLineEdit action_string;
	
	public AddStateActionDialog(State state)
	{
		this.state = state;
		this.action_string = new QLineEdit();
		this.actions = new QButtonGroup();
		
		actions.addButton(new QRadioButton("Onentry"));
		actions.addButton(new QRadioButton("During"));
		actions.addButton(new QRadioButton("Onexit"));
		
		setWindowTitle("Add state action");
		createLayout();
	}
	
	private void createLayout()
	{
		QGridLayout grid = new QGridLayout();
		
		QRadioButton entry = new QRadioButton("Onentry");
		entry.setChecked(true);
		actions.addButton(entry, 0);
		grid.addWidget(entry, 0, 0);
		
		QRadioButton during = new QRadioButton("During");
		actions.addButton(during, 1);
		grid.addWidget(during, 1, 0);
		
		QRadioButton exit = new QRadioButton("Onexit");
		actions.addButton(exit, 2);
		grid.addWidget(exit, 2, 0);
		
		grid.addWidget(new QLabel("Action: "), 3, 0);
		grid.addWidget(action_string,3, 1);
		
		QPushButton ok = new QPushButton("Ok");
		ok.pressed.connect(this, "okClicked()");
		QPushButton cancel = new QPushButton("Cancel");
		cancel.pressed.connect(this, "cancelClicked()");
		
		QDialogButtonBox box = new QDialogButtonBox();
		box.setOrientation(Qt.Orientation.Horizontal);
		box.addButton(ok, QDialogButtonBox.ButtonRole.ActionRole);
		box.addButton(cancel, QDialogButtonBox.ButtonRole.ActionRole);
		
		QVBoxLayout vbox = new QVBoxLayout();
		
		vbox.addLayout(grid);
		vbox.addWidget(box);
		
		setLayout(vbox);	
	}
	
	protected void okClicked()
	{		
		if (actions.checkedId() == 0)
		{
			state.addOnentry(action_string.displayText());
		}
		else if (actions.checkedId() == 1)
		{
			state.addDuring(action_string.displayText());
		}
		else
		{
			state.addOnexit(action_string.displayText());
		}
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
