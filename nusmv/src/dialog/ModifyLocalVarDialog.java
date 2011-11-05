package dialog;

import java.util.Iterator;

import model.FsmModule;
import model.LocalVariable;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class ModifyLocalVarDialog extends QDialog
{
	private QComboBox var_list;
	private FsmModule module;
	
	public ModifyLocalVarDialog(FsmModule module)
	{
		this.module = module;
		createComboBox();
		createLayout();
	}
	
	private void createComboBox()
	{
		this.var_list = new QComboBox();
		Iterator<LocalVariable> it = module.getLocal_variables().iterator();
		
		while (it.hasNext())
		{
			var_list.addItem(it.next().getName());
		}
	}
	
	private void createLayout()
	{
		setWindowTitle("Modify local variable");
		
		QGridLayout grid = new QGridLayout();
		
		grid.addWidget(new QLabel("Variable to modify: "), 0, 0);
		grid.addWidget(var_list, 0, 1);
		
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
		int index = var_list.currentIndex();
		
		LocalVariable lv = module.getLocal_variables().get(index);
		
		new VariableOptionsDialog(lv).exec();
		
		setVisible(false);
		
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
	}
}
