package dialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.FormulaType;
import model.FrameModule;
import model.FrameModuleInstance;
import model.Specification;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QDialogButtonBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QGroupBox;
import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLayout;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QRadioButton;
import com.trolltech.qt.gui.QVBoxLayout;

public class VerificationCommandsDialog extends QDialog
{
	private List<List<Specification>> formula_table;
	private QGroupBox ctl_spec;
	private QRadioButton ctl_all;
	private QRadioButton ctl_select;
	private QComboBox ctl_list;
	
	private QGroupBox ltl_spec;
	private QRadioButton ltl_all;
	private QRadioButton ltl_select;
	private QComboBox ltl_list;
	
	private QCheckBox check_fsm;
	private QCheckBox reachable_states;
	
	private QGroupBox show_vars;
	private QRadioButton show_all;
	private QRadioButton show_state;
	private QRadioButton show_input;
	
	private QCheckBox print_state;
	private QCheckBox pick_state_contstraint;
	private QLineEdit constraint;
	
	private QLineEdit simulate_steps;
	private String model_name;
	private String model_path;
	
	private File command_file;
	
	public VerificationCommandsDialog(FrameModule main_module, String model_name, String model_path)
	{
		this.model_name = model_name;
		this.model_path = model_path;
		this.command_file = null;
		formula_table = new ArrayList<List<Specification>>(0);
		createFormulaTable(main_module);		
		createLayout();
	}
	
	public File getCommandFile()
	{
		return command_file;
	}
	
	private void createFormulaTable(FrameModule module)
	{
		formula_table.add(module.getFormulas());
		Iterator<FrameModuleInstance> it = module.getFrameModuleInstances().iterator();
		
		while (it.hasNext())
		{
			createFormulaTable((FrameModule)it.next().getInstancedModule());
		}	
	}
	
	private void createLayout()
	{
		setWindowTitle("Verification command window");
		QGroupBox box1 = createCheckingSpecificationsCommand();
		QGroupBox box2 = createSimulateCommands();
		
		QHBoxLayout hbox = new QHBoxLayout();
		
		hbox.addWidget(box1);
		hbox.addWidget(box2);
		
		QDialogButtonBox button_box = new QDialogButtonBox();
		
		QPushButton ok = new QPushButton("Ok");
		QPushButton cancel = new QPushButton("Cancel");
		
		button_box.setOrientation(Qt.Orientation.Horizontal);
		button_box.addButton(ok, QDialogButtonBox.ButtonRole.ActionRole);
		button_box.addButton(cancel, QDialogButtonBox.ButtonRole.ActionRole);
		
		ok.clicked.connect(this, "okClicked()");
		cancel.clicked.connect(this, "cancelClicked()");
		
		QVBoxLayout vbox = new QVBoxLayout();
		
		vbox.addLayout(hbox);
		vbox.addWidget(button_box);
		
		setLayout(vbox);
	}
	
	private QGroupBox createCheckingSpecificationsCommand()
	{
		this.check_fsm			= new QCheckBox("Check FSM");
		this.reachable_states 	= new QCheckBox("Print reachable states");
		
		QGroupBox box = new QGroupBox("Checking Specifications");
		QVBoxLayout vbox = new QVBoxLayout();
		
		createCtlSpec();
		createLtlSpec();
		createSpecificationLists();
		
		vbox.addWidget(ctl_spec);
		vbox.addWidget(ltl_spec);
		vbox.addWidget(check_fsm);
		vbox.addWidget(reachable_states);
		
		box.setLayout(vbox);
		
		return box;
	}
	
	private void createCtlSpec()
	{
		ctl_spec = new QGroupBox("Check CTL specifications");
		
		ctl_spec.setCheckable(true);
		ctl_spec.setChecked(false);
		ctl_spec.setFlat(true);
		
		ctl_all = new QRadioButton("All");
		ctl_all.setChecked(true);
		
		ctl_list = new QComboBox();
		
		ctl_select = new QRadioButton("Select formula: ");
		ctl_select.toggled.connect(ctl_list, "setEnabled(boolean)");
		
		QGridLayout ctl_grid = new QGridLayout();
		
		ctl_grid.addWidget(ctl_all, 0, 0);
		ctl_grid.addWidget(ctl_select, 1, 0);
		ctl_grid.addWidget(ctl_list, 1, 1);
		
		ctl_spec.setLayout(ctl_grid);
	}
	
	private void createLtlSpec()
	{
		ltl_spec = new QGroupBox("Check LTL specifications");
		
		ltl_spec.setCheckable(true);
		ltl_spec.setChecked(false);
		ltl_spec.setFlat(true);
		
		ltl_all = new QRadioButton("All");
		ltl_all.setChecked(true);
		
		ltl_list = new QComboBox();
		ltl_list.setEnabled(false);
		
		ltl_select = new QRadioButton("Select formula: ");
		ltl_select.toggled.connect(ltl_list, "setEnabled(boolean)");
		
		QGridLayout ltl_grid = new QGridLayout();
		
		ltl_grid.addWidget(ltl_all, 0, 0);
		ltl_grid.addWidget(ltl_select, 1, 0);
		ltl_grid.addWidget(ltl_list, 1, 1);
		
		ltl_spec.setLayout(ltl_grid);
	}
	
	private void createSpecificationLists()
	{
		Iterator<List<Specification>> it1 = formula_table.iterator();
		
		while (it1.hasNext())
		{
			Iterator<Specification> it2 = it1.next().iterator();
			
			while (it2.hasNext())
			{
				Specification s = it2.next();
				
				if (s.getType().compareTo(FormulaType.CTL) == 0)
				{
					ctl_list.addItem(s.getFormula() + " IN " + s.getParentModule().getName());
				}
				else
				{
					ltl_list.addItem(s.getFormula() + " IN " + s.getParentModule().getName());
				}
			}
		}
		ctl_list.setEnabled(false);
		ltl_list.setEditable(false);
	}
	
	private QGroupBox createSimulateCommands()
	{
		createShowVarGroup();
		
		print_state = new QCheckBox("Print current states");
		
		QGroupBox box = new QGroupBox("Simulation");
		QVBoxLayout vbox = new QVBoxLayout();

		vbox.addWidget(createPickStateGroup());
		vbox.addWidget(show_vars);
		vbox.addWidget(print_state);
		vbox.addLayout(createSimulateStepLayout());
		
		box.setLayout(vbox);
		
		return box;
	}
	
	private void createShowVarGroup()
	{
		show_vars	= new QGroupBox("Show variables");
		show_vars.setCheckable(true);
		show_vars.setChecked(false);
		show_vars.setFlat(true);
		
		show_all = new QRadioButton("All variables");
		show_all.setChecked(true);
		
		show_state = new QRadioButton("State variables");
		
		show_input = new QRadioButton("Input variables");
		
		QVBoxLayout vbox = new QVBoxLayout();
		
		vbox.addWidget(show_all);
		vbox.addWidget(show_state);
		vbox.addWidget(show_input);
		
		show_vars.setLayout(vbox);	
	}
	
	private QGroupBox createPickStateGroup()
	{
		QGroupBox pick_state = new QGroupBox("Pick state options");
		pick_state.setFlat(true);
		
		constraint = new QLineEdit();
		constraint.setEnabled(false);
		
		pick_state_contstraint = new QCheckBox("Set constraint: ");
		pick_state_contstraint.setChecked(false);
		pick_state_contstraint.toggled.connect(constraint, "setEnabled(boolean)");
		
		QHBoxLayout hbox = new QHBoxLayout();
		
		hbox.addWidget(pick_state_contstraint);
		hbox.addWidget(constraint);
		
		pick_state.setLayout(hbox);
		
		return pick_state;
	}
	
	private QLayout createSimulateStepLayout()
	{
		simulate_steps = new QLineEdit("5");
		QHBoxLayout box = new QHBoxLayout();
		
		box.addWidget(new QLabel("Number of simulation steps: "));
		box.addWidget(simulate_steps);
		
		return box;
	}
	
	protected void okClicked()
	{
		printCommandFile();
		setVisible(false);
		setResult(DialogCode.Accepted.value());
	}
	
	protected void cancelClicked()
	{
		setVisible(false);
		setResult(DialogCode.Rejected.value());
	}
	
	private void printCommandFile()
	{
		try
		{
			command_file = new File("command.txt");
			FileWriter writer = new FileWriter(command_file);
			//Alessio Palmieri, insert the printer plugin to get XML output
			writer.write("set default_trace_plugin 4\n");
			printInputFile(writer);
			writer.write("go\n");
			
			printCheckingSpecifications(writer);
			printSimulateCommands(writer);
			
			
			
			writer.write("quit\n");
			
			writer.close();
		}
		catch(IOException e) {}
	}
	
	private void printInputFile(FileWriter w) throws IOException
	{
		String filename = model_name + ".smv"; 
		String file_path = "";
		String s[] = model_path.split("/");
		
		for (int i = 0; i < s.length - 1; i++)
		{
			file_path += s[i] + "/";
		}
		file_path += filename;
		
		w.write("set input_file " + file_path + "\n");
	}
	
	private void printCheckingSpecifications(FileWriter w) throws IOException
	{
		printCtlString(w);
		printLtlString(w);
		
		if (check_fsm.isChecked())
		{
			w.write("check_fsm\n");
		}
		if (reachable_states.isChecked())
		{
			w.write("print_reachable_states\n");
		}
	}
	
	private void printSimulateCommands(FileWriter w) throws IOException
	{
		printShowVarString(w);
		printPickStateString(w);
		
		if (print_state.isChecked())
		{
			w.write("print_current_state -v\n");
		}
		w.write("simulate -r " + simulate_steps.displayText() + "\n");
		w.write("show_traces -v\n");
	}
	
	private void printCtlString(FileWriter w) throws IOException
	{
		if (ctl_spec.isChecked())
		{
			w.write("check_ctlspec");
			
			if (ctl_select.isChecked())
			{
				w.write(" -p \"" + ctl_list.currentText() + "\"");
			}
			w.write("\n");
		}
	}
	
	private void printLtlString(FileWriter w) throws IOException
	{
		if (ltl_spec.isChecked())
		{
			w.write("check_ltlspec");
			
			if (ltl_select.isChecked())
			{
				w.write(" -p \"" + ltl_list.currentText() + "\"");
			}
			w.write("\n");
		}
	}
	
	private void printShowVarString(FileWriter w) throws IOException
	{
		if (show_vars.isChecked())
		{
			w.write("show_vars");
			
			if (show_state.isChecked())
			{
				w.write(" -s");
			}
			else if (show_input.isChecked())
			{
				w.write(" -i");
			}
			else
			{
				w.write(" -s -i");
			}
			w.write("\n");
		}
	}
	
	private void printPickStateString(FileWriter w) throws IOException
	{
		w.write("pick_state -r");
		
		if (pick_state_contstraint.isChecked())
		{
			w.write(" -c \"" + constraint.displayText() + "\"");
		}
		w.write("\n");
	}
}
