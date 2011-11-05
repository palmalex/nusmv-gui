package dialog;

import model.FormulaType;
import model.FrameModule;
import model.Specification;
import widget.TableWidget;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QVBoxLayout;

public class ModifyFormulaDialog extends QDialog
{
	private QComboBox type;
	private QComboBox formula_list;
	private QLineEdit formula;
	private TableWidget table;
	private int index;
	private FrameModule module;
	
	public ModifyFormulaDialog(TableWidget table, int table_index, FrameModule module)
	{
		this.module = module;
		this.table = table;
		this.formula = new QLineEdit();
		this.index = table_index;
		createComboBox();
		initialize();
		createLayout();
	}
	
	private void createComboBox()
	{
		this.type = new QComboBox();
		type.addItem("CTL");
		type.addItem("LTL");
		
		this.formula_list = new QComboBox();
		formula_list.currentIndexChanged.connect(this, "formulaChanged(int)");
		for (int i = 0; i < table.rowCount(); i++)
		{
			formula_list.addItem(table.item(i, 0).text() + "; " + table.item(i, 1).text());
		}
	}
	
	protected void formulaChanged(int row)
	{
		if (table.item(row, 0).text().compareTo("CTL") == 0)
		{
			type.setCurrentIndex(0);
		}
		else
		{	
			type.setCurrentIndex(1);
		}
		formula.setText(table.item(row, 1).text());
		formula.selectAll();
	}
	
	private void initialize()
	{
		int row;
		
		if (index > -1)
		{
			row = index;
			formula_list.setEnabled(false);
			
		}
		else
		{
			row = formula_list.currentIndex();			
		}
		formula_list.setCurrentIndex(row);
		
		if (table.item(row, 0).text().compareTo("CTL") == 0)
		{
			type.setCurrentIndex(0);
		}
		else
		{	
			type.setCurrentIndex(1);
		}
		
		formula.setText(table.item(row, 1).text());
		formula.selectAll();
		formula.setFocus();
	}
	
	private void createLayout()
	{
		setWindowTitle("Modify formula");
		
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("Formulas: "), 0, 0);
		grid.addWidget(formula_list, 0, 1);
		
		grid.addWidget(new QLabel("Specification type: "), 1, 0);
		grid.addWidget(type, 1, 1);
		
		grid.addWidget(new QLabel("New formula: "), 2, 0);
		grid.addWidget(formula, 2, 1);
		
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
		
		if (index <= -1)
		{
			index = formula_list.currentIndex();	
		}
		table.setItem(index, 0, new QTableWidgetItem(s.getType().toString()));
		table.setItem(index, 1, new QTableWidgetItem(s.getFormula()));
		
		module.getFormulas().set(index, s);
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
