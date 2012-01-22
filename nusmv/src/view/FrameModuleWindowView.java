/********************************************************************************
*                                                                               *
*   Module      :   FrameModuleWindowView.java                                  *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicView;
import model.Counter;
import model.FrameModule;
import model.FrameModuleInstance;
import model.FsmModule;
import model.FsmModuleInstance;
import model.InputVariable;
import model.LocalVariable;
import model.Module;
import model.OutputVariable;
import model.Specification;
import widget.DockWidget;
import widget.TableWidget;
import widget.TreeWidget;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.Qt.DockWidgetArea;
import com.trolltech.qt.core.Qt.Key;
import com.trolltech.qt.core.Qt.KeyboardModifier;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QKeyEvent;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QTableWidgetItem;
import com.trolltech.qt.gui.QToolBar;
import com.trolltech.qt.gui.QToolButton;
import com.trolltech.qt.gui.QTreeWidgetItem;
import com.trolltech.qt.gui.QDialog.DialogCode;
import com.trolltech.qt.gui.QKeySequence.StandardKey;
import command.AddFrameModuleInstanceCommand;
import command.AddFsmModuleInstanceCommand;
import command.AddInputVariableCommand;
import command.AddLocalVariableCommand;
import command.AddOutputVariableCommand;
import command.CopyFrameModuleInstanceCommand;
import command.CopyFsmModuleInstanceCommand;

import dialog.AddFormulaDialog;
import dialog.ExitConformDialog;
import dialog.ModifyFormulaDialog;
import dialog.RemoveFormulaDialog;
import dialog.VariableOptionsDialog;
import dialog.VerificationCommandsDialog;

/**
 * Vista della finestra relativa ad un modulo Frame.
 * @author Silvia Lorenzini
 *
 */
public class FrameModuleWindowView extends ModuleWindowView
{	
	private String model_path;
	
	private QToolButton add_local_var;
	private QToolButton add_input_var;
	private QToolButton add_output_var;
	private QToolButton add_frame_module;
	private QToolButton add_fsm_module;
	
	public Signal1<LocalVariable> local_var_added;
	public Signal1<InputVariable> input_var_added;
	public Signal1<OutputVariable> output_var_added;
	public Signal1<FrameModuleInstance> frame_module_added;
	public Signal1<FsmModuleInstance> fsm_module_added;
	public Signal0 close_all;
	
	private boolean adding_local_var;
	private boolean adding_input_var;
	private boolean adding_output_var;
	private boolean adding_frame_module;
	private boolean adding_fsm_module;
	
	private DockWidget tree_dock;
	private DockWidget module_dock;
	private TableWidget formula_table;
	private ModuleListView modules_list;
	private QAction show_formula;
	private QAction show_module_list;
	
	private FrameModule original_frame_copied;
	private FrameModuleInstance copied_frame_module_instance;
	private FsmModule original_fsm_copied;
	private FsmModuleInstance copied_fsm_module_instance;
	private boolean add_duplicate;
	private Module module_to_instance;
	
	private QAction paste;
	private QAction new_model;
	private QAction open_model;
	private QAction convert_model;
	private QAction add_formula;
	private QAction modify_formula;
	private QAction remove_formula;
	
	private QAction preferences;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public FrameModuleWindowView(FrameModule module)
	{
		super(module);
		
		this.adding_local_var = false;
		this.adding_input_var = false;
		this.adding_output_var = false;
		this.adding_frame_module = false;
		this.adding_fsm_module = false;
		this.copied_frame_module_instance = null;
		this.copied_fsm_module_instance = null;
		this.original_frame_copied = null;
		this.original_fsm_copied = null;
		this.scene_view.setEnabled(false);	
		this.add_duplicate = false;
		module_to_instance = null;
		
		tree_dock = new DockWidget(this, "Model tree", DockWidgetArea.LeftDockWidgetArea);
		project_tree = new TreeWidget(tree_dock, scene_view.getUndoStack());
		tree_dock.setWidget(project_tree);
		
		createActions();
		createMenuBar();	
		createToolbars();
		createFormulaDock();
		createModulesDock();
		createMenu();
		connectSignals();
	}
	
	/**
	 * Costruttore.
	 * @param module modulo frame relativo alla finestra.
	 * @param tree albero di progetto.
	 */
	public FrameModuleWindowView(FrameModule module, TreeWidget tree)
	{
		super(module, tree);
		
		this.adding_local_var = false;
		this.adding_input_var = false;
		this.adding_output_var = false;
		this.adding_frame_module = false;
		this.adding_fsm_module = false;
		this.scene_changed = false;
		this.copied_frame_module_instance = null;
		this.copied_fsm_module_instance = null;
		this.original_frame_copied = null;
		this.original_fsm_copied = null;
		this.add_duplicate = false;
		module_to_instance = null;
		
		createActions();
		createToolbars();
		createMenuBar();
		createFormulaDock();
		createModulesDock();
		createMenu();
		connectSignals();		
		
		new_model_created.emit(true);
	}
	
	/**
	 * Imposto il flag relativo all'aggiunta di un duplicato di una variabile di istanza
	 * di un modulo.
	 */
	public void addDuplicate()
	{
		add_duplicate = true;
	}
	
	/**
	 * Imposta il modulo da istanziare con il modulo m.
	 * @param m modulo da istanziare.
	 */
	public void setModuleToInstance(Module m)
	{
		module_to_instance = m;
	}
	
	/**
	 * Imposta a true il flag relativo all'aggiunta di un'istanza di modulo frame.
	 */
	public void addingFrameModule()
	{
		adding_frame_module = true;
	}
	
	/**
	 * Imposta a true il flag relativo all'aggiunta di un'istanza di modulo fsm.
	 */
	public void addingFsmModule()
	{
		adding_fsm_module = true;
	}
	
	/**
	 * 
	 * @return true se la scena ha subito modifiche dall'ultimo salvataggio, false altrimenti.
	 */
	public boolean isSceneChanged()
	{
		return scene_changed;
	}
	
	/**
	 * Imposta il cambiamento della scena
	 * @param b true se la scena è cambiata, false altrimenti.
	 */
	public void setSceneChanged(boolean b)
	{
		scene_changed = b;
	}
	
	/**
	 * Crea l'albero di progetto.
	 */
	public void createProjectTree()
	{
		tree_dock.closed.connect(this, "closeTreeDock()");
		project_tree.headerItem().setHidden(true);
		project_tree.setColumnCount(2);
		QTreeWidgetItem root = new QTreeWidgetItem();
		root.setText(0, model_name);
		root.setIcon(0, new QIcon("src/pixmap/nusmv.gif"));
		project_tree.insertTopLevelItem(0, root);
		
		show_model_tree.setEnabled(true);
		show_model_tree.setCheckable(true);
		show_model_tree.setChecked(tree_dock.isVisible());		
		
		show_model_tree.changed.connect(this, "showTreeModel()");
	}
	
	/**
	 * @return l'albero di progetto.
	 */
	public TreeWidget getProjectTree()
	{
		return project_tree;
	}
	
	/**
	 * Se premuto il relativo pulsante, aggiunge un oggetto al modello.
	 */
	public void leftMouseClicked(int x, int y)
	{
		if (adding_local_var)
		{
			addingLocalVar(x,y);
		}
		else if (adding_input_var)
		{
			addingInputVar(x, y);
		}
		else if (adding_output_var)
		{
			addingOutputVar(x, y);
		}
		else if (adding_frame_module)
		{
			addingFrameModule(x, y);
		}
		else if (adding_fsm_module)
		{
			addingFsmModule(x, y);
		}			
	}
	
	/**
	 * Azzera il modello e tutti i widget.
	 */
	public void setEmptyModel()
	{
		if (module.getName().compareTo("main") == 0)
		{
			close_all.emit();
			disconnectSignals();
			
			scene_view = new GraphicView(this);
			project_tree = new TreeWidget(tree_dock, scene_view.getUndoStack());
			local_tree = new TreeWidget(null, scene_view.getUndoStack());
			var_dock.setWidget(local_tree);
			formula_table.clearAll();
			fairness_table.clearAll();
			module.moduleList().removeAll();
			Counter c = new Counter();
			module.moduleList().setCounter(c);
			tree_dock.setWidget(project_tree);
			module = new FrameModule("main");
			setCentralWidget(scene_view);
			centralWidget().showMaximized();
			
			connectSignals();
			
			scene_changed = false;
		}
	}
	
	/**
	 * Azzera il modello e tutti i widget.
	 * @param model_name nome del nuovo modello.
	 * @param model_path percorso del nuovo modello.
	 */
	public void setEmptyModel(String model_name, String model_path)
	{
		close_all.emit();
		disconnectSignals();
		
		this.model_name = model_name;
		this.model_path = model_path;
	
		project_tree = new TreeWidget(tree_dock, scene_view.getUndoStack());
		local_tree = new TreeWidget(null, scene_view.getUndoStack());
		var_dock.setWidget(local_tree);
		formula_table.clearAll();
		fairness_table.clearAll();
		createProjectTree();
		module.moduleList().removeAll();
		Counter c = new Counter();
		module.moduleList().setCounter(c);
		tree_dock.setWidget(project_tree);
		module = new FrameModule("main");
		scene_view = new GraphicView(this);
		setCentralWidget(scene_view);
		centralWidget().showMaximized();
		
		connectSignals();

		scene_changed = false;
	}
	
	/**
	 * 
	 * @return la tabella relativa alleformule in logica temporale.
	 */
	public TableWidget getFormulaTable()
	{
		return formula_table;
	}
	
	/**
	 * Imposta il nome al modello.
	 * @param s nome del modello
	 */
	public void setModelName(String s)
	{
		model_name = s;
	}
	
	/**
	 * 
	 * @return il nome del modello.
	 */
	public String getModelName()
	{
		return model_name;
	}
	
	/**
	 * Imposta la stringa relativa al percorso del modello.
	 * @param s il percorso.
	 */
	public void setModelPath(String s)
	{
		model_path = s;
	}
	
	/**
	 * 
	 * @return la stringa del percorso di salvataggio del modello.
	 */
	public String getModelPath()
	{
		return model_path;
	}
	
	/**
	 * @return il modulo frame relativo a questa vista.
	 */
	public FrameModule getModule()
	{
		return (FrameModule)module;
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Inizializza le azioni della finestra e connette i segnali relativi.
	 */
	protected  void createActions()
	{
		super.createActions();
		
		add_local_var = new QToolButton();
		add_local_var.setIcon(new QIcon("src/pixmap/local_var.png"));
		add_local_var.setToolTip("Add local variable");
		add_local_var.setCheckable(true);
		add_local_var.pressed.connect(this, "addLocalVar()");
		
		add_input_var = new QToolButton();
		add_input_var.setIcon(new QIcon("src/pixmap/input_var.png"));
		add_input_var.setToolTip("Add input variable");
		add_input_var.setCheckable(true);
		add_input_var.pressed.connect(this, "addInputVar()");		
		
		add_output_var = new QToolButton();
		add_output_var.setIcon(new QIcon("src/pixmap/output_var.png"));
		add_output_var.setToolTip("Add output variable");
		add_output_var.setCheckable(true);
		add_output_var.pressed.connect(this, "addOutputVar()");		
		
		add_frame_module = new QToolButton();
		add_frame_module.setIcon(new QIcon("src/pixmap/frame_module.png"));
		add_frame_module.setToolTip("Add frame module");
		add_frame_module.setCheckable(true);
		add_frame_module.pressed.connect(this, "addFrameModule()");
		
		add_fsm_module = new QToolButton();
		add_fsm_module.setIcon(new QIcon("src/pixmap/fsm_module.png"));
		add_fsm_module.setToolTip("Add FSM module");
		add_fsm_module.setCheckable(true);
		add_fsm_module.pressed.connect(this, "addFsmModule()");
		
		show_formula = new QAction(this);
		show_formula.setText("Show formulas");
		show_formula.setCheckable(true);
		show_formula.setChecked(true);
		
		new_model = new QAction("New model...", this);
		new_model.setIcon(new QIcon("src/pixmap/window_new.png"));
		new_model.setToolTip("Create new model");
		new_model.triggered.connect((FrameModule)module, "createNewModel()");
		new_model.setShortcut(StandardKey.New);		
		
		open_model = new QAction("Open model...", this);
		open_model.setIcon(new QIcon("src/pixmap/fileopen.png"));
		open_model.setToolTip("Open model");
		open_model.triggered.connect((FrameModule)module, "load()" );
		
		preferences = new QAction("Preferences", this);
		preferences.setIcon(new QIcon("src/pixmap/gear.gif"));
		preferences.setToolTip("Set up preferences");
		preferences.triggered.connect((FrameModule)module, "preferences()");
		
		convert_model = new QAction("Import Stateflow Model", this);
		convert_model.setIcon(new QIcon("src/pixmap/convert.png"));
		convert_model.setToolTip("Import Stateflow Model");
		convert_model.triggered.connect((FrameModule)module, "convertStateflow()" );
		
		add_formula = new QAction("Add specification", menu);
		add_formula.triggered.connect(this, "addFormula()");
		
		modify_formula = new QAction("Modify specification", menu);
		modify_formula.triggered.connect(this, "modifyFormulaFromMenu()");
		
		remove_formula = new QAction("Remove specification", menu);
		remove_formula.triggered.connect(this, "removeFormula()");
		
		paste = new QAction(menu);
		paste.setText("Paste");
		paste.setShortcut(StandardKey.Paste);
		paste.setEnabled(false);
		paste.triggered.connect(this, "paste()");
		
		show_module_list = new QAction("Show module list", this);
		show_module_list.setCheckable(true);
		show_module_list.setChecked(true);
		show_module_list.triggered.connect(this, "showModules()");
	}
	
	/**
	 * Crea le toolbars.
	 */
	protected void createToolbars()
	{
		file_bar.addAction(new_model);
		file_bar.addAction(open_model);
		file_bar.addAction(convert_model);
		file_bar.addAction(preferences);
		
		
		draw_bar.addWidget(add_local_var);
		if (module.getName().compareTo("main") != 0)
		{
			draw_bar.addWidget(add_input_var);
			draw_bar.addWidget(add_output_var);
		}
		draw_bar.addWidget(add_frame_module);
		draw_bar.addWidget(add_fsm_module);
		
		QToolBar undo_toolbar = new QToolBar();
		undo_toolbar.setMovable(false);
		undo_toolbar.setEnabled(false);		
		undo_toolbar.addAction(undo);
		undo_toolbar.addAction(redo);
		
		QToolButton verify = new QToolButton();
		verify.setIcon(new QIcon("src/pixmap/start.png"));
		verify.pressed.connect(this, "run()");
		
		QToolBar run = new QToolBar("Run");
		run.setMovable(false);
		run.setEnabled(false);
		new_model_created.connect(run, "setEnabled(boolean)");
		
		run.addWidget(verify);
		
		super.createToolbar();
	}
	
	/**
	 * Crea la barra dei menu.
	 */
	protected void createMenuBar()
	{		
		file_menu.addAction(new_model);
		file_menu.addAction(open_model);
		file_menu.addAction(convert_model);
		file_menu.addAction(preferences);
		
		model_menu.addAction(new QIcon("src/pixmap/local_var.png"), "Add local variable", add_local_var.pressed);
		if (module.getName().compareTo("main") != 0)
		{
			model_menu.addAction(new QIcon("src/pixmap/input_var.png"), "Add input variable", add_input_var.pressed);
			model_menu.addAction(new QIcon("src/pixmap/output_var.png"), "Add output variable", add_output_var.pressed);
		}
		else
		{
			win_menu.addAction(show_model_tree);
		}
		model_menu.addAction(new QIcon("src/pixmap/frame_module.png"), "Add Frame module", add_frame_module.pressed);
		model_menu.addAction(new QIcon("src/pixmap/fsm_module.png"), "Add FSM module", add_fsm_module.pressed);
		
		win_menu.addAction(show_module_list);
				
		super.createMenuBar();
	}
	
	/**
	 * Crea il menu a comparsa (dx del mouse) della finestra.
	 */
	protected void createMenu()
	{
		super.createMenu();
		
		QMenu specification = menu.addMenu("Specification");
		
		specification.addAction(add_formula);
		specification.addAction(modify_formula);
		specification.addAction(remove_formula);
		
		QMenu fairness = menu.addMenu("Fairness");
		
		fairness.addAction(add_constraint);
		fairness.addAction(mod_constraint);
		fairness.addAction(remove_constraint);
		
		menu.addAction(paste);	
		menu.addSeparator();
		menu.addMenu(fairness);
		menu.addMenu(specification);		
	}	
	
	/**
	 * Se ad una variabile di istanza del modulo relativo a questa finestra viene aggiunta una variabile di ingresso,
	 * allora devono essere create in questa finestra le viste grafica e testuale (nell'apposito dock) della
	 * variabile aggiunta.
	 * Connesso al segnale "input_added" del modulo relativo a questa finestra. 
	 */
	@Override
	protected void inputAdded(InputVariable in)
	{
		new InputVariableGraphicView(in, 100, 100, scene_view);
		new InputVariableDockView(in, input_tree);
	}
	
	/**
	 * Se ad una variabile di istanza del modulo relativo a questa finestra viene aggiunta una variabile di uscita,
	 * allora devono essere create in questa finestra le viste grafica e testuale (nell'apposito dock) della
	 * variabile aggiunta.
	 * Connesso al segnale "output_added" del modulo relativo a questa finestra. 
	 */
	@Override
	protected void outputAdded(OutputVariable out)
	{
		new OutputVariableGraphicView(out, scene_view.getMousePos().x(), scene_view.getMousePos().y(), scene_view);
		new OutputVariableDockView(out, output_tree);
	}
	
	/**
	 * Metodo connesso al pulsante per l'aggiunta di una variabile locale.
	 */
	protected void addLocalVar()
	{		
		if (add_frame_module.isChecked())
		{
			adding_frame_module = false;
			add_frame_module.toggle();
		}
		else if (add_fsm_module.isChecked())
		{
			adding_fsm_module = false;
			add_fsm_module.toggle();
		}
		else if (add_output_var.isChecked())
		{
			adding_output_var = false;
			add_output_var.toggle();
		}
		else if (add_input_var.isChecked())
		{
			adding_input_var = false;
			add_input_var.toggle();
		}
		else if (add_local_var.isChecked())
		{
			adding_local_var = false;
			scene_view.setAddObjectCursor(null);
			return;
		}
		adding_local_var = true;
		QCursor c = new QCursor(new QPixmap("src/pixmap/cursor_add_local_var.png"));
		scene_view.setAddObjectCursor(c);
	}
	
	/**
	 * Metodo connesso al pulsante per l'aggiunta di una variabile di ingresso.
	 */
	protected void addInputVar()
	{		
		if (add_frame_module.isChecked())
		{
			adding_frame_module = false;
			add_frame_module.toggle();
		}
		else if (add_fsm_module.isChecked())
		{
			adding_fsm_module = false;
			add_fsm_module.toggle();
		}
		else if (add_output_var.isChecked())
		{
			adding_output_var = false;
			add_output_var.toggle();
		}
		else if (add_local_var.isChecked())
		{
			adding_local_var = false;
			add_local_var.toggle();
		}
		else if (add_input_var.isChecked())
		{
			adding_input_var = false;
			scene_view.setAddObjectCursor(null);
			return;
		}
		adding_input_var = true;
		QCursor c = new QCursor(new QPixmap("src/pixmap/cursor_add_input_var.png"));
		scene_view.setAddObjectCursor(c);
	}
	
	/**
	 * Metodo connesso al pulsante per l'aggiunta di una variabile di uscita.
	 */
	protected void addOutputVar()
	{		
		if (add_frame_module.isChecked())
		{
			adding_frame_module = false;
			add_frame_module.toggle();
		}
		else if (add_fsm_module.isChecked())
		{
			adding_fsm_module = false;
			add_fsm_module.toggle();
		}
		else if (add_input_var.isChecked())
		{
			adding_input_var = false;
			add_input_var.toggle();
		}
		else if (add_local_var.isChecked())
		{
			adding_local_var = false;
			add_local_var.toggle();
		}
		else if (add_output_var.isChecked())
		{
			adding_output_var = false;
			scene_view.setAddObjectCursor(null);
			return;
		}
		adding_output_var = true;
		QCursor c = new QCursor(new QPixmap("src/pixmap/cursor_add_output_var.png"));
		scene_view.setAddObjectCursor(c);
	}
	
	/**
	 * Metodo connesso al pulsante per l'aggiunta di una variabile di istanza di un modulo frame.
	 */
	protected void addFrameModule()
	{
		if (add_frame_module.isChecked())
		{
			adding_frame_module = false;
			scene_view.setAddObjectCursor(null);
			return;
		}
		else if (add_fsm_module.isChecked())
		{
			adding_fsm_module = false;
			add_fsm_module.toggle();
		}
		else if (add_output_var.isChecked())
		{
			adding_output_var = false;
			add_output_var.toggle();
		}
		else if (add_input_var.isChecked())
		{
			adding_input_var = false;
			add_input_var.toggle();
		}
		else if (add_local_var.isChecked())
		{
			adding_local_var = false;
			add_local_var.toggle();
		}

		adding_frame_module = true;
		QCursor c = new QCursor(new QPixmap("src/pixmap/cursor_add_frame.png"));
		scene_view.setAddObjectCursor(c);
	}
	
	/**
	 * Metodo connesso al pulsante per l'aggiunta di una variabile di istanza di un modulo fsm.
	 */
	protected void addFsmModule()
	{
		if (add_frame_module.isChecked())
		{
			adding_frame_module = false;
			add_frame_module.toggle();
		}
		else if (add_output_var.isChecked())
		{
			adding_output_var = false;
			add_output_var.toggle();
		}
		else if (add_input_var.isChecked())
		{
			adding_input_var = false;
			add_input_var.toggle();
		}
		else if (add_fsm_module.isChecked())
		{
			adding_fsm_module = false;
			scene_view.setAddObjectCursor(null);
			return;
		}
		else if (add_local_var.isChecked())
		{
			adding_local_var = false;
			add_local_var.toggle();
		}

		adding_fsm_module = true;
		QCursor c = new QCursor(new QPixmap("src/pixmap/cursor_add_fsm.png"));
		scene_view.setAddObjectCursor(c);
	}
	
	/**
	 * Visualizza o nasconde l'albero di progetto.
	 */
	protected void showTreeModel()
	{
		if (show_model_tree.isChecked())
		{
			tree_dock.show();
		}
		else
		{
			tree_dock.close();
		}
	}
	
	protected void closeTreeDock()
	{
		show_model_tree.setChecked(false);
	}
	
	/**
	 * Esegue le dovute operazioni quando si preme il tasto destro del mouse sulla vista della finestra.
	 */
	@Override
	protected void rightMouseClicked(int px, int py)
	{
		adding_local_var = adding_input_var = adding_output_var =
		adding_frame_module = adding_fsm_module = add_duplicate = false;
		
		add_local_var.setChecked(false);
		add_input_var.setChecked(false);
		add_output_var.setChecked(false);
		add_fsm_module.setChecked(false);
		add_frame_module.setChecked(false);
		
		scene_view.setAddObjectCursor(null);
		scene_view.setArrowCursor();
		super.rightMouseClicked(px, py);
	}	
	
	/**
	 * Connette segnali e slot.
	 */
	protected void connectSignals()
	{
		super.connectSignals();
		
		formula_table.itemDoubleClicked.connect(this, "modifyFormula(QTableWidgetItem)");
		
		local_var_added = new Signal1<LocalVariable>();
		local_var_added.connect(module, "addLocalVariable(LocalVariable)");
		
		frame_module_added = new Signal1<FrameModuleInstance>();
		frame_module_added.connect(module, "addFrameModuleInstance(FrameModuleInstance)");
		
		fsm_module_added = new Signal1<FsmModuleInstance>();
		fsm_module_added.connect(module, "addFsmModuleInstance(FsmModuleInstance)");
		
		((FrameModule)module).frame_instance_copied.connect(this, "addFrameCopy(FrameModuleInstance, FrameModule)");
		((FrameModule)module).fsm_instance_copied.connect(this, "addFsmCopy(FsmModuleInstance, FsmModule)");
		((FrameModule)module).specification_added.connect(this, "specificationAdded(Specification)");
		
		new_model_created.connect(module_dock, "setEnabled(boolean)");		
		
		close_all = new Signal0();
		close_all.connect(module.moduleList().to_close_all);
	}
	
	/**
	 * Disconnette segnali e slot.
	 */
	protected void disconnectSignals()
	{
		super.disconnectSignals();
		
		formula_table.itemDoubleClicked.disconnect(this, "modifyFormula(QTableWidgetItem)");
		
		local_var_added.disconnect(module, "addLocalVariable(LocalVariable)");
		
		frame_module_added.disconnect(module, "addFrameModuleInstance(FrameModuleInstance)");
		
		fsm_module_added.disconnect(module, "addFsmModuleInstance(FsmModuleInstance)");
		
		((FrameModule)module).frame_instance_copied.disconnect(this, "addFrameCopy(FrameModuleInstance, FrameModule)");
		((FrameModule)module).fsm_instance_copied.disconnect(this, "addFsmCopy(FsmModuleInstance, FsmModule)");
		((FrameModule)module).specification_added.disconnect(this, "specificationAdded(Specification)");
		
		new_model_created.disconnect(module_dock, "setEnabled(boolean)");
		close_all.disconnect(module.moduleList().to_close_all);
	}
	
	/**
	 * gestisce l'evento di chiusura della finestra lanciando la richiesta per la conferma di uscita.
	 */
	@Override
	protected void closeEvent(QCloseEvent e)
	{
		if (module.getName().compareTo("main") == 0)
		{
			ExitConformDialog d = new ExitConformDialog();
			
			if (d.exec() == 0)
			{
				System.exit(0);
			}
			else
			{
				e.ignore();
			}
		}
		else
		{
			e.accept();
		}
	}
	
	/**
	 * Imposta le copie di un variabile di istanza di modulo frame in seguito al comando copy.
	 * @param fmi variabile di istanza copiata.
	 * @param fm modulo frame copiato.
	 */
	protected void addFrameCopy(FrameModuleInstance fmi, FrameModule fm)
	{
		copied_frame_module_instance = fmi;
		original_frame_copied = fm;
		copied_fsm_module_instance = null;
		original_fsm_copied = null;
		paste.setEnabled(true);
	}
	
	/**
	 * Imposta le copie di un variabile di istanza di modulo fsm in seguito al comando copy.
	 * @param fmi variabile di istanza copiata.
	 * @param fm modulo frame copiato.
	 */
	protected void addFsmCopy(FsmModuleInstance fmi, FsmModule fm)
	{
		copied_fsm_module_instance = fmi;
		original_fsm_copied = fm;
		copied_frame_module_instance = null;
		original_frame_copied = null;
		paste.setEnabled(true);
	}
	
	/**
	 * Incolla una variabile di istanza precedentemente copiata.
	 */
	protected void paste()
	{
		QPoint mouse_pos = scene_view.getMousePos();
		
		if (copied_frame_module_instance != null)
		{
			scene_view.getUndoStack().push(
					new CopyFrameModuleInstanceCommand((FrameModule)module, copied_frame_module_instance, mouse_pos.x(), mouse_pos.y(), 
							scene_view, project_tree, module.getName(), original_frame_copied));
			
			copied_frame_module_instance.copy_instance.emit(original_frame_copied);
		}
		else if (copied_fsm_module_instance != null)
		{
			scene_view.getUndoStack().push(new CopyFsmModuleInstanceCommand(copied_fsm_module_instance, mouse_pos.x(), mouse_pos.y(),
					scene_view, project_tree, module.getName(), original_fsm_copied));
			
			copied_fsm_module_instance.copy_instance.emit(original_fsm_copied);
		}
	}
	
	/**
	 * Gestisce gli eventi dovuti alla pressione di combinazioni di tasti.
	 */
	@Override
	protected void keyPressEvent(QKeyEvent k)
	{
		if (k.modifiers().isSet(KeyboardModifier.ControlModifier))
		{
			switch (Key.resolve(k.key()))
			{
			case Key_V:
				paste();
				break;
			}
		}
		super.keyPressEvent(k);
	}
	
	
	protected void addFormula()
	{
		new AddFormulaDialog((FrameModule)module).exec();
	}
	
	protected void modifyFormulaFromMenu()
	{
		new ModifyFormulaDialog(formula_table, -1, (FrameModule)module).exec();
	}
	
	protected void modifyFormula(QTableWidgetItem item)
	{
		new ModifyFormulaDialog(formula_table, formula_table.row(item), (FrameModule)module).exec();
	}
	
	protected void removeFormula()
	{
		new RemoveFormulaDialog(formula_table, (FrameModule)module).exec();
	}
	
	/**
	 * visualizza il pannello dei comandi quando si vuole avviare la verifica del modello
	 */
	protected void run()
	{
		new VerificationCommandsDialog(getModule(), model_name, model_path).exec();
	}
	

	
	
	protected void specificationAdded(Specification spec)
	{
		int r = formula_table.rowCount();
		formula_table.setRowCount(r+1);
		formula_table.setItem(r, 0, new QTableWidgetItem(spec.getType().toString()));
		formula_table.setItem(r, 1, new QTableWidgetItem(spec.getFormula()));
		formula_table.setColumnWidth(r);
	}
	
	/**
	 * Visualizza o nasconde la lista dei moduli.
	 */
	protected void showModules()
	{
		if (show_module_list.isChecked())
		{
			module_dock.show();
		}
		else
		{
			module_dock.close();
		}
	}	
	
	protected void closeModuleDock()
	{
		show_module_list.setChecked(false);
	}
	
	protected void sceneChanged()
	{
		scene_changed = true;
	}
	
	
	private void createFormulaDock()
	{
		formula_table = new TableWidget(module_info_tab, 2);		
		formula_table.setColumnWidth(0, 50);
	
		module_info_tab.addTab(formula_table, "Specifications");
	}
	
	/********************************************************************************
	*                                                                               *
	*  					       PRIVATE FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	private void createModulesDock()
	{
		module_dock = new DockWidget(this, "Modules", DockWidgetArea.LeftDockWidgetArea);
		modules_list = new ModuleListView(module.moduleList(), this);
		module_dock.setWidget(modules_list);
		if (model_name == null)
		{
			module_dock.setEnabled(false);
		}
		module_dock.closed.connect(this, "closeModuleDock()");
	}
	
	private void addingLocalVar(int x, int y)
	{
		scene_view.getUndoStack().push(new AddLocalVariableCommand(module, x, y, scene_view, project_tree, local_tree));
		if (add_local_var.isChecked())
			add_local_var.toggle();
		adding_local_var = false;
		scene_view.setArrowCursor();
		scene_view.setAddObjectCursor(null);
		scene_view.view_changed.emit();
	}
	
	private void addingInputVar(int x, int y)
	{
		int index = module.getInputCount();
		InputVariable v = new InputVariable("in_"+index, null, "", "", module);
		module.in_hook_added.emit(v, null);
		scene_view.getUndoStack().push(new AddInputVariableCommand(module, v, x, y, scene_view, project_tree, input_tree));
		if (add_input_var.isChecked())
			add_input_var.toggle();
		adding_input_var = false;
		scene_view.setArrowCursor();
		scene_view.setAddObjectCursor(null);
		scene_view.view_changed.emit();
	}
	
	private void addingOutputVar(int x, int y)
	{
		OutputVariable v = new OutputVariable(module);
		VariableOptionsDialog dialog = new VariableOptionsDialog(v);
		dialog.exec();
		if (dialog.result() == DialogCode.Accepted.value())
		{
			module.out_hook_added.emit(v, null);			
			scene_view.getUndoStack().push(new AddOutputVariableCommand(module, v, project_tree));
		}
		if (add_output_var.isChecked())
			add_output_var.toggle();
		adding_output_var = false;
		scene_view.setArrowCursor();
		scene_view.setAddObjectCursor(null);
		scene_view.view_changed.emit();
	}
	
	private void addingFrameModule(int x, int y)
	{
		if (add_duplicate)
		{
			scene_view.getUndoStack().push(new AddFrameModuleInstanceCommand(
					(FrameModule)module, (FrameModule)module_to_instance, x, y, project_tree, scene_view));
			module_to_instance = null;
			add_duplicate = false;
		}
		else
		{
			scene_view.getUndoStack().push(new AddFrameModuleInstanceCommand((FrameModule)module, 
					project_tree, module.getName(), x, y, scene_view, false));
		}
		if (add_frame_module.isChecked())
			add_frame_module.toggle();
		adding_frame_module = false;
		scene_view.setArrowCursor();
		scene_view.setAddObjectCursor(null);
		scene_view.view_changed.emit();
	}
	
	private void addingFsmModule(int x, int y)
	{
		if (add_duplicate)
		{
			scene_view.getUndoStack().push(new AddFsmModuleInstanceCommand(
					(FrameModule)module, (FsmModule)module_to_instance, x, y, project_tree, scene_view));
			module_to_instance = null;
			add_duplicate = false;
		}
		else
		{
			scene_view.getUndoStack().push(new AddFsmModuleInstanceCommand((FrameModule)module, 
					project_tree, module.getName(), x, y, scene_view, false));
		}
		if (add_fsm_module.isChecked())
			add_fsm_module.toggle();
		adding_fsm_module = false;
		scene_view.setArrowCursor();
		scene_view.setAddObjectCursor(null);
		scene_view.view_changed.emit();
	}
}