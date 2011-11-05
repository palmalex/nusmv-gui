package dialog;

import model.FormulaType;
import model.FrameModule;
import model.Specification;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class AddFormulaDialog extends QDialog
{
	private QComboBox type;
	private QLineEdit formula;
	private FrameModule module;
	
	public AddFormulaDialog(FrameModule module)
	{
		this.module = module;
		this.formula = new QLineEdit();
		createComboBox();
		createLayout();
	}
	
	private void createComboBox()
	{
		this.type = new QComboBox();
		type.addItem("CTL");
		type.addItem("LTL");
	}
	
	private void createLayout()
	{
		setWindowTitle("Add formula");
		
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("Specification type: "), 0, 0);
		grid.addWidget(type, 0, 1);
		
		grid.addWidget(new QLabel("Formula: "), 1, 0);
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
		Specification s = new Specification(formula.displayText(), FormulaType.valueOf(type.currentText()), module);
		module.addSpecification(s);
		module.specification_added.emit(s);
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
