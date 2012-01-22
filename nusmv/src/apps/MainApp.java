/********************************************************************************
*                                                                               *
*   Module      :   MainApp.java                                                *
*   Author      :   Silvia Lorenzini & Daniele Sbaraccani		       		     		            *
*   Tools       :   Eclipse                                                     *
*   																			*
********************************************************************************/
package apps;

import item.GraphicView;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import model.FrameModule;
import model.ModulesList;
import translator.Error;
import translator.StateflowModelReader;
import util.WorkingTemp;
import view.FrameModuleTreeView;
import view.FrameModuleWindowView;
import xml.SmvCreator;
import xml.XmlCreator;
import xml.XmlLoader;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPrintDialog;
import com.trolltech.qt.gui.QPrinter;
import com.trolltech.qt.gui.QPrinterInfo;

import dialog.CloseModifiedDialog;
import dialog.CreateModelDialog;
import dialog.ExitConformDialog;
import dialog.ResultDialog;
import dialog.StateflowConfirmConversion;
import dialog.StateflowModelNoForm;
import dialog.VerificationCommandsDialog;
import edu.tum.cs.simulink.builder.SimulinkModelBuildingException;

public class MainApp
{
	private static FrameModuleWindowView main_view;
	private Process nusmv;
	private boolean model_changed;
	private boolean saveAs;
	private String ultimatePath;
	private  String nusmvPath;
	

	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore: crea il modello del modulo main, la finestra relativa e  connete i segnali tra le due. 
	 */
	public MainApp()
	{
		// Alessio Palmieri : change to make the preferences persistent
		Preferences prefs = Preferences.userNodeForPackage(getClass());
		nusmvPath = prefs.get("NUSMV", null);
		System.out.println("path : " + nusmvPath);
		if (nusmvPath == null || nusmvPath.length()==0) {
			preferences();
		}
		FrameModule module_main = new FrameModule("main");
		main_view = new FrameModuleWindowView(module_main);
		main_view.showMaximized();
		main_view.show();
		
		module_main.added.emit();
		
		connectSignals(module_main);
		model_changed = false;
		saveAs=false;
		ultimatePath=".";
	}
	
	/**
	 * Inizializza l'applicazione
	 * @param args
	 */
	public static void main(String args[])
	{
		QApplication.initialize(args);
		
		new MainApp();		
		
		QApplication.exec();
	}
	
	/**
	 * Salva il modello richiamando il metodo per la creazione del file xml.
	 */
	public void save()
	{
		if(saveAs){
			saveAs();
		}else{
			new XmlCreator(main_view.getModule(), main_view.getModelPath(), main_view.getModelName());
			model_changed = false;	
			ultimatePath=main_view.getModelPath().substring(0, main_view.getModelPath().lastIndexOf("/"));
		}
		
	}
	
	/**
	 * Salva il modello con un nuovo nome.
	 */
	public void saveAs()
	{
		String f = "Graphic model file (*.gmf);;Any file(*)";
		
		String dir = QFileDialog.getSaveFileName(null, "Save new model",ultimatePath+"\\" + main_view.getModelName() + ".gmf", new QFileDialog.Filter(f));
		if (dir != "")
		{
			saveAs=false;
			main_view.setModelPath(dir);
			ultimatePath=main_view.getModelPath().substring(0, main_view.getModelPath().lastIndexOf("/"));
			save();
		}
	}

	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	/**
	 * Carica un modello tra quelli salvati. Se un altro modello è aperto, viene chiuso.
	 */
	
	protected void load()
	{	
		if (main_view.getView().isEnabled() && model_changed)
		{	
			CloseModifiedDialog d = new CloseModifiedDialog(main_view);
			d.exec();
			if (d.isAccepted())
			{
				String f = "Graphic model file (*.gmf);;Any file(*)";
				String path = QFileDialog.getOpenFileName(main_view, "Load model", ultimatePath, 
						new QFileDialog.Filter(f));
				
				if (path.compareTo("") != 0)
				{
					main_view.setEmptyModel();
					new XmlLoader(path, main_view);
					model_changed = false;
					ultimatePath=path.substring(0, path.lastIndexOf("/"));
				}
			}
		}
		else
		{
			String f = "Graphic model file (*.gmf);;Any file(*)";
			String path = QFileDialog.getOpenFileName(main_view, "Load model", ".", 
					new QFileDialog.Filter(f));
			
			if (path.compareTo("") != 0)
			{
				if (main_view.getView().isEnabled())
					main_view.setEmptyModel();
				new XmlLoader(path, main_view);
				model_changed = false;
				ultimatePath=path.substring(0, path.lastIndexOf("/"));
			}
		}
	}
	
	protected void preferences() {
		nusmvPath = QFileDialog.getOpenFileName(main_view, "select the NUSMV exe", nusmvPath);
		System.out.println("path : " + nusmvPath);
		Preferences pref = Preferences.userNodeForPackage(getClass());
		pref.put("NUSMV", nusmvPath);
		
		
		
		
	}
	
	/**
	 * Carica file *.mdl per convertirlo in modo da essere utilizzato NusmvGUI
	 * @throws SimulinkModelBuildingException 
	 * @throws FileNotFoundException 
	 */
protected void convertStateflow() throws FileNotFoundException, SimulinkModelBuildingException 
	{	
	try {
		if (main_view.getView().isEnabled() && model_changed)
		{	
			CloseModifiedDialog d = new CloseModifiedDialog(main_view);
			d.exec();
			if (d.isAccepted())
			{
				String f = "Simulink model file (*.mdl);;Any file(*)";
				String path = QFileDialog.getOpenFileName(main_view, "Import Stateflow Model", ultimatePath, 
						new QFileDialog.Filter(f));
				
				if (path.compareTo("") != 0)
				{
					main_view.setEmptyModel();
					saveAs=false;
					model_changed = false;
					ultimatePath=path.substring(0, path.lastIndexOf("/"));

					StateflowModelReader stateflowModelReader =new StateflowModelReader(path, main_view);
					if(stateflowModelReader.checkForm()){
						stateflowModelReader.convert(path);
						saveAs=true;
						model_changed = true;
					}
					else{
						StateflowModelNoForm s = new StateflowModelNoForm(stateflowModelReader.getError());
						s.exec();
						if(s.isTest()){
							StateflowConfirmConversion test = new StateflowConfirmConversion(stateflowModelReader.getError());
							test.exec();
							if(test.isAccepted())
								stateflowModelReader.convert(path);
							saveAs=true;
							model_changed = true;
						}
					}
					
				}
			}
		}
		else
		{
			String f = "Simulink model file (*.mdl);;Any file(*)";
			String path = QFileDialog.getOpenFileName(main_view, "Import Stateflow Model", ultimatePath, 
					new QFileDialog.Filter(f));
			

			
			if (path.compareTo("") != 0)
			{
				if (main_view.getView().isEnabled())
					main_view.setEmptyModel();
				saveAs=false;
				model_changed = false;
				ultimatePath=path.substring(0, path.lastIndexOf("/"));
				StateflowModelReader stateflowModelReader =new StateflowModelReader(path, main_view);
				if(stateflowModelReader.checkForm()){
					stateflowModelReader.convert(path);
					saveAs = true;
					model_changed = true;
				}
				else{
					StateflowModelNoForm s = new StateflowModelNoForm(stateflowModelReader.getError());
					s.exec();
					if(s.isTest()){
						StateflowConfirmConversion test = new StateflowConfirmConversion(stateflowModelReader.getError());
						test.exec();
						if(test.isAccepted())
							stateflowModelReader.convert(path);
						saveAs=true;
						model_changed = true;
					}
				}
			}
		}
	}
	catch (SimulinkModelBuildingException e) {
		StateflowModelNoForm s = new StateflowModelNoForm(new Error("stateflowNOform"));						
		s.exec();

	}

		
		}
			

	


	/**
	 * Crea un nuovo modello; se un altro è aperto allora viene chiuso.
	 */
	protected void createNewModel()
	{
		if (main_view.getView().isEnabled() && model_changed)
		{	
			CloseModifiedDialog d = new CloseModifiedDialog(main_view);
			d.exec();
			if (d.isAccepted() && new CreateModelDialog(main_view).exec() != 0)
			{
				new FrameModuleTreeView((FrameModule)main_view.getModule(), main_view.getProjectTree());
				main_view.getModule().added.emit();
				main_view.new_model_created.emit(true);
				save();
			}
		}
		else if (new CreateModelDialog(main_view).exec() != 0)
		{
			new FrameModuleTreeView((FrameModule)main_view.getModule(), main_view.getProjectTree());
			main_view.getModule().added.emit();
			main_view.new_model_created.emit(true);
			save();
		}
	}
	
	/**
	 * Genera il file smv richiamando l'apposito metodo e mostra il file creato.
	 */
	protected void generateSmv()
	{
		if(model_changed==true){
			save();
		}
		SmvCreator smv = new SmvCreator(main_view.getModelPath(), main_view.getModelName());
		try
		{
			 Desktop desktop = null;
			 if (Desktop.isDesktopSupported()) 
			 {
				 desktop = Desktop.getDesktop();
			 }
			 desktop.open(smv.getSmvPath());
		} 
		catch (IOException ioe) 
		{
			ioe.printStackTrace();
		}		
	}
	
	/**
	 * Richiede la conferma per terminare il programma.
	 */
	protected void close()
	{
		ExitConformDialog d = new ExitConformDialog();
		
		if (d.exec() == 0)
		{
			System.exit(0);
		}
	}
	
	/**
	 * Stampa ogni modulo definito all'interno del modello.
	 */
	protected void printAll()
	{
		QPrinter printer = new QPrinter(QPrinterInfo.defaultPrinter());
		QPrintDialog pd = new QPrintDialog(printer);
		List<GraphicView> views = new ArrayList<GraphicView>(0);
		
		if (pd.exec() == QDialog.DialogCode.Accepted.value())
		{
			views.add(main_view.getView());
			main_view.getModule().printSubmodules(views);
			
			QPainter painter = new QPainter();
			painter.begin(pd.printer());
			
			for (int page = 0; page < views.size(); page++)
			{
				GraphicView v = views.get(page);
				v.render(painter);
				
				if (page < views.size() - 1)
					
					printer.newPage();
			}
			painter.end();
		}
	}
	
	/**
	 * Crea il file dei comandi e richiama il metodo per l'avvio di NuSMV su tale file. Mostra l'output di NuSMV.
	 */
	protected void run()
	{
		// Alessio Palmieri: change this code to manage temp directory
		WorkingTemp wrk = WorkingTemp.getInstance();
		File output_file = new File(wrk.createNewWorkingTemp(),"nusmvlog_" + main_view.getModelName() + ".txt");
 		output_file.setWritable(true);
 		
 		if(runNuSMV(output_file))
 		{
 			QDialog dialog = new ResultDialog(main_view);
 			dialog.show();
 		}
	}
	
	/**
	 * Termina il processo NuSMV in caso di necessità.
	 */
	protected void destroyNuSmvProcess()
	{
		if (nusmv != null)
		{
			nusmv.destroy();
		}
	}
	
	/**
	 * Tiene conto di eventuali modifiche avvenute al modello dall'ultimo salvataggio.
	 */
	protected void modelChanged()
	{
		model_changed = true;
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/	
	private boolean runNuSMV(File output_file)
	{
		new SmvCreator(main_view.getModelPath(), main_view.getModelName());
		VerificationCommandsDialog command = new VerificationCommandsDialog(main_view.getModule(), main_view.getModelName(), main_view.getModelPath());
		
		if (command.exec() != 0)
		{
			File command_file = command.getCommandFile();
			//String program = "NuSMV -load " + command_file;
			// Alessio Palmieri change to allow different path
			String program = nusmvPath + " -load " + command_file;
			try
	     	{	     		
	     		nusmv = Runtime.getRuntime().exec(program);
	     			     		
	     		ProcessWriter err_pw = new ProcessWriter(nusmv.getErrorStream(), output_file);
	     		ProcessWriter out_pw = new ProcessWriter(nusmv.getInputStream(), output_file);
	     		
	     		err_pw.interrupted.connect(this, "destroyNuSmvProcess()");
	     		err_pw.interrupted.connect(out_pw, "stopProcess()");
	     		out_pw.interrupted.connect(this, "destroyNuSmvProcess()");
	     		out_pw.interrupted.connect(err_pw, "stopProcess()");
	     		
	     		new Thread(err_pw).start();
	     		new Thread(out_pw).start();
	     		
	     		nusmv.waitFor();
	     		  		
	     		err_pw.stopProcess();
	     		out_pw.stopProcess();
	     		return true;
	     	}
	     	catch(Throwable t) 
	     	{
	     		t.printStackTrace();
	     	}
		}
		return false;
	}
	
	private void connectSignals(FrameModule module_main)
	{
		ModulesList ml = module_main.moduleList();
		
		ml.to_save.connect(this, "save()");
		ml.to_save_as.connect(this, "saveAs()");
		ml.to_load.connect(this, "load()");
		ml.to_convert.connect(this, "convertStateflow()");
		ml.to_create_new.connect(this, "createNewModel()");
		ml.to_generate_smv.connect(this, "generateSmv()");
		ml.to_run.connect(this, "run()");
		ml.to_print_all.connect(this, "printAll()");
		ml.model_changed.connect(this, "modelChanged()");
		ml.to_preferences.connect(this, "preferences()");
		
		module_main.to_save.connect(this, "save()");
		module_main.to_save_as.connect(this, "saveAs()");
		module_main.to_convert.connect(this, "convertStateflow()");
		module_main.to_load.connect(this, "load()");
		module_main.to_create_new.connect(this, "createNewModel()");
		module_main.to_generate_smv.connect(this, "generateSmv()");
		module_main.to_run.connect(this, "run()");
		module_main.to_close.connect(this, "close()");
		module_main.to_print_all.connect(this, "printAll()");
		module_main.model_changed.connect(this, "modelChanged()");
		module_main.to_preferences.connect(this, "preferences()");
	}
}
