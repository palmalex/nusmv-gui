package dialog;

import java.util.Iterator;

import model.Module;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class RemoveFairnessDialog extends QDialog
{
	private QComboBox formula_list;
	private QCheckBox remove_all;
	private Module module;
	
	public RemoveFairnessDialog(Module module)
	{
		this.module = module;
		this.remove_all = new QCheckBox();
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
		
		remove_all.toggled.connect(formula_list, "setDisabled(boolean)");
		remove_all.setText("Remove all constraints");
	}
	
	private void createLayout()
	{
		setWindowTitle("Remove fairness constraint");
		
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("constraint to remove: "), 0, 0);
		grid.addWidget(formula_list, 0, 1);
		
		grid.addWidget(remove_all, 1, 0);
		
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
		if (remove_all.isChecked())
		{
			module.getFairnessFormula().removeAll(module.getFairnessFormula());
			module.fairness_removed.emit(-1);
		}
		else
		{
			module.getFairnessFormula().remove(formula_list.currentIndex());
			module.fairness_removed.emit(formula_list.currentIndex());
		}
		
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
