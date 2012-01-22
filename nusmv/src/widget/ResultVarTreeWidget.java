package widget;

import java.util.LinkedList;
import java.util.List;

import model.result.ModuleIfc;

import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QWidget;

public class ResultVarTreeWidget extends QWidget {
	private QTreeWidget selection;
	private ModuleIfc root;
	
	public ResultVarTreeWidget(QWidget parent, ModuleIfc root){
		//super(parent);
		this.root = root;
		setupUI();
	}

	private void setupUI(){
		selection = new QTreeWidget(this);
		selection.setMinimumSize(400, 400);
		selection.setColumnCount(2);
		selection.setSizePolicy(QSizePolicy.Policy.Maximum, QSizePolicy.Policy.Maximum);
		  List<String> labels = new LinkedList<String>();
	        labels.add("Name");
	        labels.add("type");
	        labels.add("value");
	        selection.setHeaderLabels(labels);
	    new ResultTreeItem(selection, root, false);
	    
	    QHBoxLayout layout = new QHBoxLayout();
	    
	    layout.addWidget(selection);
	    setLayout(layout);
	    selection.adjustSize();
	}
	
	
	public void refresh() {
		selection.clear();
		new ResultTreeItem(selection, root, false);
	}
	
}
