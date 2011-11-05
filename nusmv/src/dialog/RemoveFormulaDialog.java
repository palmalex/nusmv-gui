package dialog;

import model.FrameModule;
import widget.TableWidget;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class RemoveFormulaDialog extends QDialog
{
	private QComboBox formula_list;
	private TableWidget table;
	private FrameModule module;
	
	public RemoveFormulaDialog(TableWidget table, FrameModule module)
	{
		this.module = module;
		this.table = table;
		createComboBox();
		createLayout();
	}
	
	private void createComboBox()
	{
		this.formula_list = new QComboBox();
		for (int i = 0; i < table.rowCount(); i++)
		{
			formula_list.addItem(table.item(i, 0).text() + "; " + table.item(i, 1).text());
		}
	}
	
	private void createLayout()
	{
		setWindowTitle("Remove formula");
		
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("Formula to remove: "), 0, 0);
		grid.addWidget(formula_list, 0, 1);
		
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
		int row = formula_list.currentIndex();
		table.removeRow(row);
		module.getFormulas().remove(row);
		
		setVisible(false);
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
