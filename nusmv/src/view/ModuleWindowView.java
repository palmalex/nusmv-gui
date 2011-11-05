/********************************************************************************
*                                                                               *
*   Module      :   ModuleWindowView.java                                       *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicView;
import item.PixmapItem;

import java.util.Iterator;

import model.InputVariable;
import model.Module;
import model.OutputVariable;
import widget.DockWidget;
import widget.TableWidget;
import widget.TreeWidget;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.Qt.DockWidgetArea;
import com.trolltech.qt.core.Qt.ItemFlag;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QDialog;
import com.trolltech.qt.gui.QGraphicsItemInterface;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMainWindow;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPrintDialog;
import com.trolltech.qt.gui.QPrinter;
import com.trolltech.qt.gui.QPrinterInfo;
import com.trolltech.qt.gui.QStatusBar;
import com.trolltech.qt.gui.QTabWidget;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QToolBar;
import com.trolltech.qt.gui.QToolBox;
import com.trolltech.qt.gui.QKeySequence.StandardKey;

import dialog.AddFairnessDialog;
import dialog.ModifyFairnessDialog;
import dialog.RemoveFairnessDialog;

/**
 * Vista della finestra relativa ad un modulo generico.
 * @author Silvia Lorenzini
 *
 */
public abstract class ModuleWindowView extends QMainWindow
{
	protected String model_name;
	protected GraphicView scene_view;
	protected DockWidget var_dock;
	protected DockWidget info_widget;
	protected TreeWidget project_tree;
	protected TreeWidget local_tree;
	protected TreeWidget input_tree;
	protected TreeWidget output_tree;
	protected QTabWidget module_info_tab;
	protected TableWidget fairness_table;
	
	protected QMenu menu;
	protected Module module;
	protected PixmapItem fairness_pixmap;

	protected boolean scene_changed;
	
	protected QMenu file_menu;
	protected QMenu edit_menu;
	protected QMenu win_menu;
	protected QMenu model_menu;
	
	protected QToolBar file_bar;
	protected QToolBar draw_bar;
	protected QToolBar edit_bar;
	protected QToolBar run_bar;
//	protected QToolBar zoom_bar;
	
	protected QAction save;
	protected QAction save_as;
	protected QAction print;
	protected QAction print_all;
	protected QAction exit;
	protected QAction undo;
	protected QAction redo;
	protected QAction show_vars;
	protected QAction show_info;
	protected QAction show_model_tree;
	protected QAction zoom_in;
	protected QAction zoom_out;
	protected QAction zoom_fit;
	protected QAction generate_smv;
	protected QAction verify;
	protected QAction add_constraint;
	protected QAction mod_constraint;
	protected QAction remove_constraint;

	public Signal1<Boolean> new_model_created;
	
	protected double scale_factor;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 * @param module modulo relativo a questa finestra.
	 */
	public ModuleWindowView(Module module)
	{
		this.module = module;
		this.scale_factor = 1;
		
		scene_view = new GraphicView(this);
		this.scene_changed = false;
		
		local_tree = new TreeWidget(null, scene_view.getUndoStack());
		input_tree = new TreeWidget(null, scene_view.getUndoStack());
		output_tree = new TreeWidget(null, scene_view.getUndoStack());
		
		file_menu = new QMenu(tr("&File"), this);
		edit_menu = new QMenu(tr("&Edit"), this);
		model_menu = new QMenu(tr("&Model"), this);
		win_menu = new QMenu(tr("&Window"), this);
		
		file_bar = new QToolBar("File");
		edit_bar = new QToolBar("Edit");
		draw_bar = new QToolBar("Draw");
		run_bar = new QToolBar("Run");
//		zoom_bar = new QToolBar("Zoom");
		
		new_model_created = new Signal1<Boolean>();
		
		setFairnessPixmap();				
		setCentralWidget(scene_view);
		setMinimumSize(700, 432);
		setWindowTitle("NuSMV_GUI");
		setWindowIcon(new QIcon("src/pixmap/nusmv.gif"));
		setStatusBar(new QStatusBar());
		createWidget();
	}
	
	/**
	 * Costruttore.
	 * @param module modulo
	 * @param tree albero di progetto.
	 */
	public ModuleWindowView(Module module, TreeWidget tree)
	{
		this.module = module;
		this.scale_factor = 1;
		this.project_tree = tree;
		this.model_name = tree.topLevelItem(0).text(0);
		scene_view = new GraphicView(this);
		this.scene_changed = false;
		
		local_tree = new TreeWidget(null, scene_view.getUndoStack());
		input_tree = new TreeWidget(null, scene_view.getUndoStack());
		output_tree = new TreeWidget(null, scene_view.getUndoStack());
		
		file_menu = new QMenu(tr("&File"), this);
		edit_menu = new QMenu(tr("&Edit"), this);
		model_menu = new QMenu(tr("&Model"), this);
		win_menu = new QMenu(tr("&Window"), this);
		
		file_bar = new QToolBar("File");
		edit_bar = new QToolBar("Edit");
		draw_bar = new QToolBar("Draw");
		run_bar = new QToolBar("Run");
//		zoom_bar = new QToolBar("Zoom");
		
		new_model_created = new Signal1<Boolean>();
		
		setFairnessPixmap();		
		setCentralWidget(scene_view);
		setMinimumSize(700, 432);
		setWindowTitle("NuSMV_GUI");
		setWindowIcon(new QIcon("src/pixmap/nusmv.gif"));
		setStatusBar(new QStatusBar());
		createWidget();		
	}
	
	public void setStatusBarMessage(String message)
	{
		statusBar().showMessage(message);
	}
	
	public void nameChanged(String name)
	{
		setTitle();
	}
	
	public GraphicView getView()
	{
		return scene_view;
	}
	
	public TreeWidget getLocalTree()
	{
		return local_tree;
	}
	
	public TreeWidget getInputTree()
	{
		return input_tree;
	}
	
	public TreeWidget getOutputTree()
	{
		return output_tree;
	}
	
	public TreeWidget getProjectTree()
	{
		return project_tree;
	}
	
	public abstract void leftMouseClicked(int x, int y);
	
	public abstract Module getModule();
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected void connectSignals()
	{
		module.mod_name_changed.connect(this, "nameChanged(String)");
		module.input_added.connect(this, "inputAdded(InputVariable)");
		module.output_added.connect(this, "outputAdded(OutputVariable)");
		module.fairness_added.connect(this, "fairnessAdded(String)");
		module.fairness_modified.connect(this, "fairnessModified(int, String)");
		module.fairness_removed.connect(this, "fairnessRemoved(int)");
		module.close_win.connect(this, "close()");
		
		new_model_created.connect(save, "setEnabled(boolean)");
		new_model_created.connect(save_as, "setEnabled(boolean)");
		new_model_created.connect(draw_bar, "setEnabled(boolean)");
		new_model_created.connect(edit_bar, "setEnabled(boolean)");
//		new_model_created.connect(zoom_bar, "setEnabled(boolean)");
		new_model_created.connect(run_bar, "setEnabled(boolean)");
		new_model_created.connect(print, "setEnabled(boolean)");
		new_model_created.connect(print_all, "setEnabled(boolean)");
		new_model_created.connect(this, "setTitle()");
		new_model_created.connect(model_menu, "setEnabled(boolean)");
		
		module.show_window.connect(this, "showWin()");
		module.removed.connect(this, "removed()");
		module.need_print_info.connect(this, "getPrintInfo()");
		scene_view.right_mouse_clicked.connect(this, "rightMouseClicked(int, int)");
		scene_view.view_changed.connect(module.model_changed);
	}
	
	protected void disconnectSignals()
	{
		module.mod_name_changed.disconnect(this, "nameChanged(String)");
		module.input_added.disconnect(this, "inputAdded(InputVariable)");
		module.output_added.disconnect(this, "outputAdded(OutputVariable)");
		module.fairness_added.disconnect(this, "fairnessAdded(String)");
		module.fairness_modified.disconnect(this, "fairnessModified(int, String)");
		module.fairness_removed.disconnect(this, "fairnessRemoved(int)");
		
		new_model_created.disconnect(save, "setEnabled(boolean)");
		new_model_created.disconnect(save_as, "setEnabled(boolean)");
		new_model_created.disconnect(draw_bar, "setEnabled(boolean)");
		new_model_created.disconnect(edit_bar, "setEnabled(boolean)");
//		new_model_created.disconnect(zoom_bar, "setEnabled(boolean)");
		new_model_created.disconnect(run_bar, "setEnabled(boolean)");
		new_model_created.disconnect(print, "setEnabled(boolean)");
		new_model_created.disconnect(print_all, "setEnabled(boolean)");
		new_model_created.disconnect(this, "setTitle()");
		new_model_created.disconnect(model_menu, "setEnabled(boolean)");
		
		module.show_window.disconnect(this, "showWin()");
		module.removed.disconnect(this, "removed()");
		module.need_print_info.disconnect(this, "getPrintInfo()");
		scene_view.right_mouse_clicked.disconnect(this, "rightMouseClicked(int, int)");
		scene_view.view_changed.disconnect(this, "sceneChanged()");
	}
		
	protected void showFairnessFormula()
	{
		Iterator<String> it = module.getFairnessFormula().iterator();
		String text = "";
		
		while (it.hasNext())
		{
			text += it.next() + "\n"; 
		}
		fairness_pixmap.setToolTip(text);
	}
	
	
	protected void createActions()
	{
		save = new QAction("Save", this);
		save.setIcon(new QIcon("src/pixmap/filesave.png"));
		save.setToolTip("Save");
		save.setEnabled(false);
		save.setShortcut(StandardKey.Save);
		save.triggered.connect(module, "save()");
		
		save_as = new QAction("Save as...", this);
		save_as.setIcon(new QIcon("src/pixmap/filesaveas.png"));
		save_as.setToolTip("Save as");
		save_as.setEnabled(false);
		save_as.triggered.connect(module, "saveAs()");
		
		print = new QAction("Print...", this);
		print.setIcon(new QIcon("src/pixmap/print.png"));
		print.setToolTip("Print current module");
		print.setShortcut(StandardKey.Print);
		print.setEnabled(false);
		print.triggered.connect(this, "print()");
		
		print_all = new QAction("Print all..", this);
		print_all.setIcon(new QIcon("src/pixmap/print_all.png"));
		print_all.setToolTip("Print complete model");
		print_all.setEnabled(false);
		print_all.triggered.connect(module, "printAll()");
		
		exit = new QAction("Exit", this);
		exit.triggered.connect(module, "close()");
		
		undo = new QAction(tr("&Undo"), this);
		undo.setShortcut("Ctrl+Z");
		undo.setEnabled(false);
		undo.setIcon(new QIcon("src/pixmap/undo.png"));
		undo.triggered.connect(scene_view.getUndoStack(), "undo()");
		scene_view.getUndoStack().canUndoChanged.connect(undo, "setEnabled(boolean)");
		
		redo = new QAction(tr("&Redo"), this);
		redo.setEnabled(false);
		redo.setIcon(new QIcon("src/pixmap/redo.png"));
		redo.triggered.connect(scene_view.getUndoStack(), "redo()");		
		scene_view.getUndoStack().canRedoChanged.connect(redo, "setEnabled(boolean)");
		
		show_vars = new QAction("Show variable view", this);
		show_vars.setCheckable(true);
		show_vars.setChecked(true);
		show_vars.changed.connect(this, "showVar()");
		
		show_info = new QAction("Show module constraints", this);
		show_info.setCheckable(true);
		show_info.setChecked(true);
		show_info.changed.connect(this, "showInfo()");
		
		show_model_tree = new QAction("Show model tree", this);
		
/*		zoom_in = new QAction("Zoom in", this);
		zoom_in.setIcon(new QIcon("src/pixmap/zoom_in.png"));
		zoom_in.triggered.connect(this, "zoomIn()");
		
		zoom_out = new QAction("Zoom out", this);
		zoom_out.setIcon(new QIcon("src/pixmap/zoom_out.png"));
		zoom_out.triggered.connect(this, "zoomOut()");
		
		zoom_fit = new QAction("Zoom fit", this);
		zoom_fit.setIcon(new QIcon("src/pixmap/zoom_fit.png"));
		zoom_fit.triggered.connect(this, "zoomFit()");*/
		
		generate_smv = new QAction("Generate smv file", this);
		generate_smv.setIcon(new QIcon("src/pixmap/smv_file.png"));
		generate_smv.triggered.connect(module, "generateSmv()");
		
		verify = new QAction("Run NuSMV", this);
		verify.setIcon(new QIcon("src/pixmap/start.png"));
		verify.triggered.connect(module, "run()");
		
		add_constraint = new QAction("Add constraint", menu);
		add_constraint.triggered.connect(this, "addFairness()");
		
		mod_constraint = new QAction("Modify constraint", menu);
		mod_constraint.triggered.connect(this, "modifyFairness()");
		
		remove_constraint = new QAction("Remove constraint", menu);
		remove_constraint.triggered.connect(this, "removeFairness()");
	}
	
	protected void createToolbar()
	{
		file_bar.setMovable(false);
		file_bar.addAction(save);
		file_bar.addAction(print);
		
		draw_bar.setMovable(false);
		draw_bar.setEnabled(false);
		
		edit_bar.setMovable(false);
		edit_bar.setEnabled(false);
		edit_bar.addAction(undo);
		edit_bar.addAction(redo);
		
		run_bar.setMovable(false);
		run_bar.setEnabled(false);
		run_bar.addAction(generate_smv);
		run_bar.addAction(verify);
		
//		zoom_bar.setMovable(false);
//		zoom_bar.setEnabled(false);
//		zoom_bar.addAction(zoom_out);
//		zoom_bar.addAction(zoom_in);
//		zoom_bar.addAction(zoom_fit);
		
		addToolBar(file_bar);
		addToolBar(draw_bar);
		addToolBar(edit_bar);
		addToolBar(run_bar);
//		addToolBar(zoom_bar);
	}
	
	protected void createMenuBar()
	{
		file_menu.addAction(save);
		file_menu.addAction(save_as);
		file_menu.addSeparator();
		file_menu.addAction(print);
		file_menu.addAction(print_all);
		file_menu.addSeparator();
		file_menu.addAction(exit);
		
		edit_menu.addAction(undo);
		edit_menu.addAction(redo);
	
		win_menu.addAction(show_vars);
		win_menu.addAction(show_info);
		
		model_menu.setEnabled(false);
		
		menuBar().addMenu(file_menu);
		menuBar().addMenu(edit_menu);
		menuBar().addMenu(model_menu);		
		menuBar().addMenu(win_menu);		
	}
	
	protected void createMenu()
	{
		this.menu = new QMenu();
	}
	
	protected void showWin()
	{
		setVisible(false);
		setVisible(true);
	}
	
	protected void showVar()
	{
		if (show_vars.isChecked())
		{
			var_dock.show();
		}
		else
		{
			var_dock.close();
		}
	}
	
	protected void showInfo()
	{
		if (show_info.isChecked())
		{
			info_widget.show();
		}
		else
		{
			info_widget.close();
		}
	}
	
	protected void closeVarDock()
	{
		show_vars.setChecked(false);
	}
	
	protected void closeInfoDock()
	{
		show_info.setChecked(false);
	}
	
	protected void newModel()
	{
		
	}
	
	protected void removed()
	{
		close();
	}
	
	protected void rightMouseClicked(int px, int py)
	{
		Iterator<QGraphicsItemInterface> it = scene_view.getScene().items().iterator();
		
		while (it.hasNext())
		{
			if (it.next().isUnderMouse())
				
				return;
		}
		
		menu.exec(new QPoint(px, py));
	}
	
	protected void addFairness()
	{
		new AddFairnessDialog(module).exec();
	}
	
	protected void modifyFairness()
	{
		new ModifyFairnessDialog(module).exec();		
	}
	
	protected void removeFairness()
	{
		new RemoveFairnessDialog(module).exec();
	}
	
	protected void fairnessAdded(String constraint)
	{
		int row = fairness_table.rowCount();
		
		fairness_table.setRowCount(row + 1);
		
		QTableWidgetItem t = new QTableWidgetItem(constraint);
		t.setFlags(ItemFlag.ItemIsEnabled);
		fairness_table.setItem(row, 0, t);
		fairness_table.setColumnWidth(row);
		scene_view.view_changed.emit();
	}
	
	protected void fairnessModified(int index, String constraint)
	{
		QTableWidgetItem t = new QTableWidgetItem(constraint);
		t.setFlags(ItemFlag.ItemIsEnabled);
		fairness_table.setItem(index, 0, t);
		scene_view.view_changed.emit();
	}
	
	protected void fairnessRemoved(int index)
	{
		if (index < 0)
		{
			while (fairness_table.rowCount() > 0)
			{
				fairness_table.removeRow(0);
			}
		}
		else
		{
			fairness_table.removeRow(index);
		}
		scene_view.view_changed.emit();
	}
	
	protected void print()
	{
		QPrinter printer = new QPrinter(QPrinterInfo.defaultPrinter());
		QPrintDialog pd = new QPrintDialog(printer);
		
		if (pd.exec() == QDialog.DialogCode.Accepted.value())
		{
			QPainter painter = new QPainter(pd.printer());
			scene_view.render(painter);
		
			painter.begin(printer);
			painter.end();
		}
	}
	
//	protected void zoomIn()
//	{
//		scene_view.multiplyScaleFactor(1.25);
//		scene_view.zoomIn();
//	}
//	
//	protected void zoomOut()
//	{
//		scene_view.multiplyScaleFactor(0.80);
//		scene_view.zoomOut();
//	}
	
//	protected void zoomFit()
//	{
////		double view_width = scene_view.rect().width();
////		double view_height = scene_view.rect().height();
////		double scene_width = scene_view.getScene().itemsBoundingRect().width();
////		double scene_height = scene_view.getScene().itemsBoundingRect().height();
////		
////		double scale_factor = Math.max(view_width / scene_width, view_height / scene_height);
////		
////		scene_view.setScaleFactor(scale_factor);
////		scene_view.scale(scale_factor, scale_factor);
////		QPoint p = scene_view.mapFromScene(0, 0); 
////		System.out.println(p);
////		scene_view.translate(p.x(), p.y());
//		
////		scene_view.fitInView(scene_view.getScene().itemsBoundingRect());
//	}
	
	protected void setTitle()
	{
		setWindowTitle("NuSMV_GUI - " + model_name + " / Module " + module.getName());
	}
	
	protected void getPrintInfo()
	{
		module.setTemp(scene_view);
	}
	
	protected abstract void inputAdded(InputVariable in);
	
	protected abstract void outputAdded(OutputVariable out);
	
	/********************************************************************************
	*                                                                               *
	*  					       PRIVATE FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	private void setFairnessPixmap()
	{
		this.fairness_pixmap = new PixmapItem("src/pixmap/fairness_large.png");
		fairness_pixmap.setPos(10, 5);
		fairness_pixmap.setZValue(5);
		scene_view.getScene().addItem(fairness_pixmap);
		fairness_pixmap.setVisible(false);
		fairness_pixmap.mouse_on.connect(this, "showFairnessFormula()");
	}
	
	private void createWidget()
	{
		createDock();
		
		fairness_table = new TableWidget(module_info_tab, 1);
		fairness_table.setColumnWidth(0, 100);
		module_info_tab = new QTabWidget(info_widget);
		module_info_tab.addTab(fairness_table, "Fariness");
	
		info_widget.setWidget(module_info_tab);
	}
	
	private void createDock()
	{
		info_widget = new DockWidget(this, "Module information", DockWidgetArea.RightDockWidgetArea);
		info_widget.closed.connect(this, "closeInfoDock()");
		
		var_dock = new DockWidget(this, "Variables", DockWidgetArea.RightDockWidgetArea);
		var_dock.closed.connect(this, "closeVarDock()");
		
		QToolBox var_toolbox = new QToolBox(var_dock);
		
		if (module.getName().compareTo("main") != 0)
		{
			var_toolbox.addItem(input_tree, "Input");
			var_toolbox.addItem(output_tree, "Output");
		}
		var_toolbox.addItem(local_tree, "Local");		
		var_toolbox.setCurrentIndex(var_toolbox.count()-1);
		
		var_dock.setWidget(var_toolbox);
	}
}
