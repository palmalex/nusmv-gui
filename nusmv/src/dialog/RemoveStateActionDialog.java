package dialog;

import java.util.Iterator;

import model.State;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QButtonGroup;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QRadioButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class RemoveStateActionDialog extends QDialog
{
	private State state;
	private QButtonGroup actions_types;
	private QComboBox entry_actions;
	private QComboBox during_actions;
	private QComboBox exit_actions;
	
	public RemoveStateActionDialog(State state)
	{
		this.state = state;
		this.actions_types = new QButtonGroup();
		
		setGroupBox();
		setWindowTitle("Add state action dialog");
		createLayout();
	}
	
	private void createLayout()
	{
		QGridLayout grid = new QGridLayout();
		
		QRadioButton entry = new QRadioButton("Onentry");
		entry.setChecked(true);
		entry.toggled.connect(entry_actions, "setEnabled(boolean)");
		actions_types.addButton(entry, 0);
		grid.addWidget(entry, 0, 0);
		
		QRadioButton during = new QRadioButton("During");
		during.toggled.connect(during_actions, "setEnabled(boolean)");
		actions_types.addButton(during, 1);
		grid.addWidget(during, 1, 0);
		
		QRadioButton exit = new QRadioButton("Onexit");
		exit.toggled.connect(exit_actions, "setEnabled(boolean)");
		actions_types.addButton(exit, 2);
		grid.addWidget(exit, 2, 0);
		
		grid.addWidget(entry_actions, 0, 1);
		grid.addWidget(during_actions, 1, 1);
		grid.addWidget(exit_actions, 2, 1);
		
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
	
	private void setGroupBox()
	{
		entry_actions = new QComboBox();
		
		Iterator<String> it = state.getOnentry().iterator();
		while (it.hasNext())
		{
			entry_actions.addItem(it.next());
		}
		
		during_actions = new QComboBox();
		
		Iterator<String> it2 = state.getDuring().iterator();
		while (it2.hasNext())
		{
			during_actions.addItem(it2.next());
		}
		during_actions.setEnabled(false);
		
		exit_actions = new QComboBox();
		
		Iterator<String> it3 = state.getOnexit().iterator();
		while (it3.hasNext())
		{
			exit_actions.addItem(it3.next());
		}
		exit_actions.setEnabled(false);
	}
	
	protected void okClicked()
	{		
		if (actions_types.checkedId() == 0)
		{
			state.getOnentry().remove(entry_actions.currentText());
		}
		else if (actions_types.checkedId() == 1)
		{
			state.getDuring().remove(during_actions.currentText());
		}
		else
		{
			state.getOnexit().remove(exit_actions.currentText());
		}
		state.actions_changed.emit();
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
