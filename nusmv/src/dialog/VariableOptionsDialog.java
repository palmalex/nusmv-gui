package dialog;

import model.Type;
import model.Variable;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class VariableOptionsDialog extends QDialog
{
	protected QLineEdit name;
	protected QComboBox type;
	protected QLineEdit initial;
	protected QLineEdit values;
	
	protected Variable var;
	
	private QPushButton ok;
	
	public VariableOptionsDialog(Variable var)
	{
		this.var = var;
		
		name = new QLineEdit(var.getName());
		name.selectAll();
		
		type = new QComboBox();
		type.currentStringChanged.connect(this, "typeChanged()");
		
		values = new QLineEdit(var.getValues());
		initial = new QLineEdit(var.getInitial_value());
		
		initialize();
		setWindowTitle("Variable properties");
		createLayout();
	}
	
	private void createLayout()
	{
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("Name: "), 0, 0);
		grid.addWidget(name, 0, 1);
		
		grid.addWidget(new QLabel("Type: "), 1, 0);
		grid.addWidget(type, 1, 1);
		
		grid.addWidget(new QLabel("Values: "), 2, 0);
		grid.addWidget(values, 2, 1);
		
		grid.addWidget(new QLabel("Initial value: "), 3, 0);
		grid.addWidget(initial, 3, 1);
		
		QDialogButtonBox box = new QDialogButtonBox();
		
		ok = new QPushButton("Ok");
//		ok.setIcon(new QIcon("icone/button_ok.png"));
		
		QPushButton cancel = new QPushButton("Cancel");
//		cancel.setIcon(new QIcon("icone/button_cancel.png"));
		
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
	
	private void initialize()
	{
		type.addItem("Boolean");
		type.addItem("Integer");
		type.addItem("Enumeration");
		
		switch(var.getType())
		{
		case Boolean:
			type.setCurrentIndex(0);
			break;
		case Int:
			type.setCurrentIndex(1);
			break;
		case Enumeration:
			type.setCurrentIndex(2);
			break;
		default:
			type.setCurrentIndex(0);
			break;
		}
		
		if (var.getType() == Type.Boolean)
		{
			values.setText("{0, 1}");
			values.setEnabled(false);
		}
	}
	
	protected void typeChanged()
	{
		if (type.currentIndex() == 0)
		{
			values.setText("{0, 1}");
			values.setEnabled(false);
		}
		else
		{
			values.setText(var.getValues());
			values.setEnabled(true);
		}
	}
	
	protected void okClicked()
	{
		switch(type.currentIndex())
		{
		case 0:
			var.edit(name.displayText(), Type.Boolean, values.displayText(), initial.displayText());
			break;
		case 1:
			var.edit(name.displayText(), Type.Int, values.displayText(), initial.displayText());
			break;
		case 2:
			var.edit(name.displayText(), Type.Enumeration, values.displayText(), initial.displayText());
			break;
		default:
			System.out.println("Error on type selection");
		}
		setVisible(false);
		var.properties_changed.emit();
		setResult(DialogCode.Accepted.value());
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
		setResult(DialogCode.Rejected.value());
	}
		
}
