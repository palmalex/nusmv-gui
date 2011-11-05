package dialog;

import java.util.Iterator;

import model.Module;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class ModifyFairnessDialog extends QDialog
{
	private QComboBox formula_list;
	private QLineEdit formula;
	private Module module;
	
	public ModifyFairnessDialog(Module module)
	{
		this.module = module;
		this.formula = new QLineEdit();
		createComboBox();
		createLayout();
	}
	
	private void createComboBox()
	{
		this.formula_list = new QComboBox();
		
		Iterator<String> it = module.getFairnessFormula().iterator();
		
		while (it.hasNext())
		{
			formula_list.addItem(it.next());
		}
		formula_list.currentStringChanged.connect(this, "formulaChanged()");
		formula.setText(formula_list.currentText());
	}
	
	protected void formulaChanged()
	{
		formula.setText(formula_list.currentText());
		formula.selectAll();
		formula.setFocus();
	}
	
	private void createLayout()
	{
		setWindowTitle("Modify fairness constraint");
		
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("Constraints: "), 0, 0);
		grid.addWidget(formula_list, 0, 1);
		
		grid.addWidget(new QLabel("New constraint: "), 1, 0);
		grid.addWidget(formula, 1, 1);
		
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
		module.modifyFairnessConstraint(formula_list.currentIndex(), formula.displayText());
		module.fairness_modified.emit(formula_list.currentIndex(), formula.displayText());
		
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
