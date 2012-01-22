/**
 * 
 */
package dialog;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import util.WorkingTemp;
import view.FrameModuleWindowView;
import widget.TraceWidget;

import apps.Icon;

import com.trolltech.qt.core.QSignalMapper;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QGroupBox;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QTextBrowser;
import com.trolltech.qt.gui.QVBoxLayout;

/**
 * @author Alessio Palmieri
 * 
 * Dialog to display the resume of the results
 *
 */
public class ResultDialog extends QDialog {
	private WorkingTemp wrk = WorkingTemp.getInstance();
	private QSignalMapper mapper = new QSignalMapper();
	private FrameModuleWindowView main;
	
	public ResultDialog(FrameModuleWindowView main){
		this.main = main;
		mapper = new QSignalMapper(this);
		mapper.mappedString.connect(this,"openDesktop(String)");
		createLayout();
		
	
		setVisible(true);
		//setModal(true);
	}
	
	private QGroupBox createTextView(String fileName, String title) {
		QGroupBox textViewBox = new QGroupBox(title);
		QVBoxLayout textViewlayout = new QVBoxLayout();
		textViewBox.setLayout(textViewlayout);
		
		try {
			File file = new File(wrk.getCurrentPath(),fileName);
			if (!file.exists()) {
				textViewBox = null;
			} else {
				QTextBrowser text = new QTextBrowser();
				
				// change this to use QT file functionalities
				StringBuffer fileData = new StringBuffer(1000);
				BufferedReader br = new BufferedReader(new FileReader(file));
				char[] buffer = new char[1024];
				int numRead=0;
				String readData ="";
				while ((numRead=br.read(buffer))!=-1){
					readData = String.valueOf(buffer, 0, numRead);
					fileData.append(readData);
					buffer = new char[1024];
				}
				text.setText(readData.toString());
				textViewlayout.addWidget(text);
			}
		} catch (IOException e) {
			
		}
		return textViewBox;
	}
	
	private QGroupBox createCheckFSM() {
		return createTextView("check_fms.txt", "check FSM");
	}
	
	private QGroupBox createShowVars() {
		QGroupBox ret = null;
		try {
			ret= createSpecs("Show vars", wrk.getShowVars(), "open show vars file");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	
	}
	
	private QGroupBox createReachableStatus(){
		return createTextView("reachable_states.txt", "Reachables status");
	}
	
	private QGroupBox createSpecs(String boxTitle,String[] content, String header) {
		QGroupBox groupSpecs = new QGroupBox(boxTitle);
		QGridLayout specsLayout = new QGridLayout();
		groupSpecs.setLayout(specsLayout);
		
		try {
			QPushButton button = null;
			int row = 0;
			for (String fileName : content) {
				System.out.println("file " + fileName);
				// read the first 2 lines of the file to get Formula and result
				File file = new File(wrk.getCurrentPath(),fileName);
				FileInputStream fstream = new FileInputStream(file);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				StringBuffer sb = new StringBuffer();
				String line = br.readLine();
				boolean buttonEnable = false;
				if (line!=null) {
					sb.append(line.substring(3, line.length()));
					line = br.readLine();
					if (line != null){
						// in this case there is a counter sample
						buttonEnable = true;
					}
				}
				in.close();
				String labelTxt = sb.toString();
				QLabel label = new QLabel(header==null?labelTxt:header);
				button = new QPushButton(Icon.magnifier(),"view");
				button.setMaximumSize(80, 40);
				button.setDisabled(!buttonEnable);
				button.clicked.connect(mapper, "map()");
				mapper.setMapping(button, fileName);
			
				specsLayout.addWidget(label, row, 0);
				specsLayout.addWidget(button, row, 1);
				++row;
			}
			if (row==0) {
				groupSpecs = null;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return groupSpecs;
	}
	
	private QGroupBox createSpecs(String boxTitle,String[] content){
		return createSpecs(boxTitle, content, null);
	}
	
	private QGroupBox createCTLSpecs(){
		QGroupBox ret = null;
		try {
			ret= createSpecs("CTL Specs", wrk.getCurrentCTL());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	
	}
	
	private QGroupBox createLTLSpecs(){
		QGroupBox ret = null;
		try {
			ret= createSpecs("LTL Specs", wrk.getCurrentLTL());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	private QGroupBox createSimulation(){
		QGroupBox ret = null;
		try {
			ret= createSpecs("Simulation", wrk.getSimulation(),"Show Simulation");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	private QGroupBox createLog(){
		return	 createTextView("nusmvlog_"+main.getModelName()+".txt", "logfile");
	}
	
	private void createLayout(){
		// check the content of the temp dir.
		// create a tabforeach file
		setWindowTitle("results");
		QVBoxLayout vbox = new QVBoxLayout();
			
		QGroupBox gb = createCTLSpecs();
		System.out.println("createCTLSpecs() : " + gb);
		if (gb!=null) {
			vbox.addWidget(gb);
		}
		gb = createLTLSpecs();
		System.out.println("createCTLSpecs() : " + gb);
		if (gb!=null) {
			vbox.addWidget(gb);
		}
		gb = createCheckFSM();
		System.out.println("createCheckFMS() : " + gb);
		if (gb!=null) {
			vbox.addWidget(gb);
		}
		gb = createReachableStatus();
		System.out.println("createReachableStatus() : " + gb);
		if (gb!=null) {
			vbox.addWidget(gb);
		}
		gb = createShowVars();
		System.out.println("createShowVars() : " + gb);
		if (gb!=null) {
			vbox.addWidget(gb);
		}
		gb = createSimulation();
		System.out.println("createSimulation() : " + gb);
		if (gb!=null) {
			vbox.addWidget(gb);
		}
		
		gb = createLog();
		if (gb!=null) {
			vbox.addWidget(gb);
		}
		
		
		setLayout(vbox);
	}
	
	private void openDesktop(String filename) throws IOException {
		System.out.println(filename);
		if (filename.startsWith("check_ctlspec") || filename.startsWith("check_ltlspec")) {
			// clicked on the verification
			System.out.println("show counter example");
			TraceDialog td = new TraceDialog(this, main, wrk.getCurrentPath()+"/"+filename);
			td.show();
		} else {
			if (filename.equals("simulation.xml")) {
				System.out.println("show simulation");
				TraceDialog td = new TraceDialog(this, main, wrk.getCurrentPath()+"/simulation.xml");
			} else {
				if (Desktop.isDesktopSupported()){
					Desktop.getDesktop().open(new File(wrk.getCurrentPath()+"/"+filename));
				}
			}
		}
	}
}
