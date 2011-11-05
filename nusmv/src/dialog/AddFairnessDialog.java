package dialog;

import model.Module;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class AddFairnessDialog extends QDialog
{
	private QLineEdit formula;
	private Module module;
	
	public AddFairnessDialog(Module module)
	{
		this.module = module;
		this.formula = new QLineEdit("");
		this.formula.selectAll();
		createLayout();
	}
	
	private void createLayout()
	{
		setWindowTitle("Add fairness constraint");
		
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("Constraint: "), 0, 0);
		grid.addWidget(formula, 0, 1);
		
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
		module.addFairnessConstraint(formula.displayText());
		module.fairness_added.emit(formula.displayText());
		
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
