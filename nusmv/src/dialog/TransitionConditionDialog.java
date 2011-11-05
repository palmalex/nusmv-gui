package dialog;

import model.Transition;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class TransitionConditionDialog extends QDialog
{
	private QLineEdit condition;
	private Transition transition; 
	
	public TransitionConditionDialog(Transition t)
	{
		this.transition = t;
		
		this.condition = new QLineEdit(transition.getCondition());
		condition.selectAll();
				
		setWindowTitle("Set condition");
		createLayout();
	}
	
	private void createLayout()
	{
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("Condition: "), 0, 0);
		grid.addWidget(condition, 0, 1);
				
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
		transition.setCondition(condition.displayText());
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
