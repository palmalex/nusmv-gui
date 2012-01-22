package widget;

import java.util.Iterator;

import model.result.MainModuleResult;
import model.result.ModuleIfc;
import model.result.ModuleType;
import model.result.StateResult;
import model.result.VariableModuleResult;

import apps.Icon;

import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QTreeWidget;
import com.trolltech.qt.gui.QTreeWidgetItem;

public class ResultTreeItem extends QTreeWidgetItem {
	ModuleIfc module;
	
	public ResultTreeItem(QTreeWidgetItem parent, ModuleIfc module, boolean leaf) {
		super(parent);
		this.module = module;
		setInfo(module,leaf);
	}

	public ResultTreeItem(QTreeWidget parent, ModuleIfc module, boolean leaf) {
		super(parent);
		this.module = module;
		setInfo(module, leaf);
	}
	
	public void populate(){
		Iterator<ModuleIfc> childs= module.getChilds();
		while (childs.hasNext()) {
			ModuleIfc moduleIfc = (ModuleIfc) childs.next();
			new ResultTreeItem(this, moduleIfc, false);
		}
		this.setExpanded(true);
	}
	
	private void setInfo(ModuleIfc module, boolean leaf) {
		
		setText(0, module.getName());
		switch (module.getType()) {
		case MAIN:
			setIcon(0, Icon.nusmv());
			setText(1, "<<Main Module>>");
			break;
		case VARIABLE:
			setText(2, ((VariableModuleResult) module).getValue());
			setText(1, "<<Variable>>");
			break;
		case STATE:
			setText(2, ((StateResult)module).getValue());
			setText(1, "<<State>>");
			break;
		case FSM:
			setIcon(0, Icon.fsmModule());
			setText(1, "<<FSM Module>>");
			break;
		case FRAME:
			setIcon(0, Icon.frameModule());
			setText(1, "<<FrameModule>>");
		default:
					break;
		}
		populate();
	}
	
}
