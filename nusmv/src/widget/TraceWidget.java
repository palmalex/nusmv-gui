package widget;

import java.util.Iterator;

import model.result.MainModuleResult;
import model.result.ModuleIfc;
import model.result.ModuleType;
import model.result.StateResult;
import model.result.VariableModuleResult;
import view.FrameModuleWindowView;

import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QIODevice;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTextBrowser;
import com.trolltech.qt.gui.QWidget;
import com.trolltech.qt.xml.QDomDocument;
import com.trolltech.qt.xml.QDomElement;
import com.trolltech.qt.xml.QDomNode;
import com.trolltech.qt.xml.QDomNodeList;

public class TraceWidget extends QWidget {
	
	private ResultStateView stateBrowser;
	private int currentState=0;
	private MainModuleResult frame;
	private QDomDocument doc;
	private FrameModuleWindowView main;
	private ResultVarTreeWidget tree;
	private String filename;
	
	private void print(ModuleIfc root, int level) {
		String tab="";
		for(int i=0;i<level;i++) {
			tab+="\t";
		}
		System.out.println(tab+"name " + root.getName() + " " + root.getType());
		
		if (root.getType().equals(ModuleType.VARIABLE)) {
			System.out.println(((VariableModuleResult) root).getValue());
		}
		Iterator<ModuleIfc> iterator=root.getChilds();
		++level;
		while(iterator.hasNext()) {
			print(iterator.next(),level);
		}
	}
	
		
	
	public TraceWidget(FrameModuleWindowView main, String filename) {
		super(main);
		this.filename=filename;
		int loop;
		System.out.println("filename " + filename);
		
		frame = new MainModuleResult(main.getModule());
		print(frame,0);
		
		
		doc = new QDomDocument();
		
		
		String result="";
		QFile file = new QFile(filename);
		if (file.open(QIODevice.OpenModeFlag.ReadOnly)) {
			result = file.readAll().toString().replaceAll("--.*\\r\\n", "");
		} 
		
		if(doc.setContent(result).success) {
			file.close();
		}
		
		QDomNodeList loopNL = doc.elementsByTagName("loops");
	    QDomElement  loopEl = loopNL.at(0).toElement();	
		
	    System.out.println("loops  : >" + loopEl.text()  + "<>" + loopEl.nodeName());
		
	    if (loopEl.text().trim().length()==0) {
	    	loop = 0;
	    } else {
	    	loop = Integer.valueOf(loopEl.text().trim());
	    }
		QGridLayout layout = new QGridLayout(this);
		
		QDomNodeList stateNL = doc.elementsByTagName("state");
		int state = stateNL.count();
		
		stateBrowser = new ResultStateView(state,loop);
		stateBrowser.setToolTip(tr("State diagram"));
		stateBrowser.STATECHANGE.connect(this, "goToState()");
			
	
		layout.addWidget(stateBrowser,1,1);
		
		tree = new ResultVarTreeWidget(this, frame);
	
		layout.addWidget(tree,1,2);
		tree.show();
	
		
		print(frame,0);
		QGridLayout stateMover = new QGridLayout();
		tree.refresh();
		QPushButton next = new QPushButton("Next");
		next.clicked.connect(stateBrowser, "next()");
		QPushButton prev = new QPushButton("Prev");
		prev.clicked.connect(stateBrowser,"prev()");
		stateMover.addWidget(prev,1,1);
		stateMover.addWidget(next,1,2);
		layout.addLayout(stateMover,2,1,1,2);
		
		
		goToState();
		
	}
	
	public void goToState(){
		currentState = stateBrowser.getCurrentNode();
		
		QDomNodeList nodeL = doc.elementsByTagName("node");
		QDomElement node = nodeL.at(currentState-1).toElement();
		QDomElement state = node.firstChildElement("state");
		QDomNodeList variableL = state.elementsByTagName("value");
		int varCount = variableL.count();
		
		for(int i=0; i<varCount; i++) {
			String levels = variableL.at(i).toElement().attribute("variable");
			String value = variableL.at(i).toElement().text();
			
			String[] path = levels.split("\\.");
			
			ModuleIfc root = frame;
			int j=0;
			for(;j<path.length-1;j++){
				root = root.getChild(path[j]);
				if (root!=null) {
					System.out.println("j " + j);
					System.out.println(path[j]);
					System.out.println(root.getName());
				} 
			}
			// 
			root = root.getChild(path[j]);

				if (root!=null) {
					System.out.println("j " + j);
					System.out.println(path[j]);
					System.out.println(root.getName());
					switch (root.getType()) {
					case VARIABLE:
						((VariableModuleResult) root).setValue(value);
						break;
					case STATE:
						((StateResult) root).setValue(value);
						break;
					default:
						break;
					}
					
				} else {
					System.out.println("j " + j);
					System.out.println(path[j]);
				}
			}
		
		tree.refresh();
			
		}
		
		
		
		
		
	}


