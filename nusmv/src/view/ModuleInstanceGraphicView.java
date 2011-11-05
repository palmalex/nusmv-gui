/********************************************************************************
*                                                                               *
*   Module      :   ModuleInstanceGraphicView.java                              *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicItem;
import item.GraphicView;
import model.InputVariable;
import model.Module;
import model.ModuleInstance;
import model.OutputVariable;
import widget.TreeWidget;
import xml.XmlCreator;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.Key;
import com.trolltech.qt.core.Qt.KeyboardModifier;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QGraphicsPixmapItem;
import com.trolltech.qt.gui.QGraphicsSceneMouseEvent;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QKeyEvent;
import com.trolltech.qt.gui.QKeySequence;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QKeySequence.StandardKey;
import command.AddInputVariableCommand;

import dialog.CreateOutputDialog;
import dialog.ModuleInstanceRenameDialog;

/**
 * Classe relativa alla vista grafica di una variabile di istanza generica.
 * @author Silvia Lorenzini
 *
 */
public abstract class ModuleInstanceGraphicView extends GraphicItem
{
	public Signal1<String> 			var_name_changed;
	public Signal1<String> 			module_name_changed;
	public Signal2<InputVariableHookView, ModuleInstanceGraphicView> 	input_var_added;
	public Signal1<InputVariable> 	input_var_removed;
	public Signal1<OutputVariable> 	output_var_added;
	public Signal1<OutputVariable> 	output_var_removed;
	public Signal1<Boolean> 		process_activated;
	public Signal0					double_click;
	public Signal0					select;
	public Signal3<Integer, Integer, GraphicView>	duplicate;
	public Signal0					scaled;
	
	protected ModuleInstance instance;
	protected String mod_name;
	protected String var_name;
	protected QGraphicsPixmapItem fairness_pixmap;
	protected boolean process;
	protected QMenu menu;
	protected TreeWidget project_tree;
	private QAction process_action;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 * @param instance variaible di istanza.
	 * @param x coordinata x della posizione della vista.
	 * @param y coordinata y della posizione della vista.
	 * @param w larghezza del rettangolo.
	 * @param h altezza del rettangolo.
	 * @param view vista grafica dell'intera scena.
	 * @param tree albero di progetto.
	 */
	public ModuleInstanceGraphicView(ModuleInstance instance, int x, int y, int w, int h, GraphicView view, TreeWidget tree)
	{
		super(x, y, w, h, view);
		
		this.project_tree = tree;
		this.instance = instance;
		this.process = false;
		this.fairness_pixmap = new QGraphicsPixmapItem(new QPixmap("src/pixmap/infinito_small.png"));
		fairness_pixmap.setPos(x + width - 30, y + height - 20);
		fairness_pixmap.setParentItem(this);
		fairness_pixmap.setVisible(false);
		
		this.var_name_changed 		= new Signal1<String>();
		this.module_name_changed 	= new Signal1<String>();
		this.input_var_added 		= new Signal2<InputVariableHookView, ModuleInstanceGraphicView>();
		this.input_var_removed 		= new Signal1<InputVariable>();
		this.output_var_added 		= new Signal1<OutputVariable>();
		this.output_var_removed 	= new Signal1<OutputVariable>();
		this.process_activated 		= new Signal1<Boolean>();	
		this.double_click 			= new Signal0();
		this.duplicate 				= new Signal3<Integer, Integer, GraphicView>();
		this.select					= new Signal0();
		this.scaled 				= new Signal0(); 
	}

	public GraphicView getView()
	{
		return view;
	}
	
	public TreeWidget getProjectTree()
	{
		return project_tree;
	}
	
	public String getInstanceName()
	{
		return var_name;
	}
	
	public String getModuleName()
	{
		return mod_name;
	}
	
	/**
	 * Aggiorna la vista se il nome del modulo istanziato è stato modificato.
	 * @param name nuovo nome del modulo istanziato.
	 */
	public void modNameChanged(String name)
	{
		mod_name = name;
		text.setText(var_name + ": <<" + mod_name + ">>");
		update(boundingRect());
	}
	
	/**
	 * Aggiorna la vista se il nome della variaible di istanza è stato modificato.
	 * @param name nuovo nome della variabile.
	 */
	public void varNameChanged(String name)
	{
		var_name = name;
		text.setText(var_name + ": <<" + mod_name + ">>");
		update(boundingRect());
	}
	
	/**
	 * Imposta il bordo tratteggiato (unito) se la keyword process è attivata (disattivata).
	 * @param active true se è process.
	 */
	public void processActivated(Boolean active)
	{
		dash_line = active;
		scene().update();
		process_action.setChecked(active);
	}
	
	public void showMenu()
	{
		menu.exec(new QPoint(QCursor.pos().x(), QCursor.pos().y()));
	}
	
	public QMenu getMenu()
	{
		return menu;
	}
	
	@Override
	public void keyPressEvent(QKeyEvent k)
	{
		if (k.key() == Qt.Key.Key_Delete.value())
		{
			removeInstanceVar();
		}
		else if (k.modifiers().isSet(KeyboardModifier.ControlModifier))
		{
			switch (Key.resolve(k.key()))
			{
			case Key_C:
				copy();
				break;
			case Key_D:
				duplicate();
				break;
			}
		}
		super.keyPressEvent(k);
	}
	
	@Override
	public void mouseDoubleClickEvent(QGraphicsSceneMouseEvent event)
	{
		double_click.emit();
		super.mouseDoubleClickEvent(event);
	}
	
	@Override
	public void mouseMoveEvent(QGraphicsSceneMouseEvent e)
	{		
		view.getScene().adjustSceneRect(view.rect());
		
		super.mouseMoveEvent(e);		
	}
	
	@Override
	public void mousePressEvent(QGraphicsSceneMouseEvent e)
	{
		select.emit();
		
		super.mousePressEvent(e);
	}
	
	public ModuleInstance getInstance()
	{
		return instance;
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected void createMenu(ModuleInstance mi)
	{
		menu = new QMenu();
		
		QAction rename = new QAction(view);
		rename.setText("Rename");
		rename.triggered.connect(this, "rename()");
		
		QAction copy = new QAction(view);
		copy.setText("Copy");
		copy.setShortcut(QKeySequence.StandardKey.Copy);
		copy.triggered.connect(this, "copy()");
		
		QAction duplicate = new QAction(view);
		duplicate.setText("Duplicate");
		duplicate.triggered.connect(this, "duplicate()");
		
		QAction remove = new QAction(view);
		remove.setText("Delete");
		remove.setShortcut(StandardKey.Delete);
		remove.setIcon(new QIcon("src/pixmap/delete.png"));
		remove.triggered.connect(this, "removeInstanceVar()");
		
		menu.addAction(rename);	
		menu.addAction(copy);
		menu.addAction(duplicate);
		menu.addAction(remove);
		
		menu.addSeparator();
		
		QAction add_input = new QAction(tr("Add input"), view);
		add_input.setIcon(new QIcon("src/pixmap/input.png"));
		add_input.triggered.connect(this, "addInput()");
		
		menu.addAction(add_input);
		
		QAction add_output = new QAction(tr("Add output"), view);
		add_output.setIcon(new QIcon("src/pixmap/output.png"));
		add_output.triggered.connect(this, "addOutput()");
		
		menu.addAction(add_output);
		
		menu.addSeparator();
		
		menu.addSeparator();
		
		process_action = new QAction("Process", view);
		process_action.setCheckable(true);
		process_action.triggered.connect(mi, "setProcess(Boolean)");
		
		menu.addAction(process_action);
	}
	
	protected void connectSignals(ModuleInstance mi)
	{
		var_name_changed.connect(mi, "setName(String)");
		module_name_changed.connect(mi.getInstancedModule(), "setName(String)");
		process_activated.connect(mi, "setProcess(Boolean)");
		double_click.connect(mi, "edit()");
		duplicate.connect(mi, "duplicate(Integer, Integer, GraphicView)");
		select.connect(mi, "select()");
		
		mi.var_name_changed.connect(this, "varNameChanged(String)");
		mi.getInstancedModule().mod_name_changed.connect(this, "modNameChanged(String)");
		mi.process_activated.connect(this, "processActivated(Boolean)");
		mi.removed.connect(this, "removeFromScene()");
		mi.added.connect(this, "addToScene()");
		mi.to_copy.connect(this, "copy(ModuleInstance, GraphicView, FrameModuleTreeView)");
		mi.copy_instance.connect(this, "copy(Module)");
		mi.selected.connect(this, "selected()");
		mi.getInstancedModule().in_hook_added.connect(this, "inputVarAdded(Object, ModuleInstanceGraphicView)");
		mi.getInstancedModule().out_hook_added.connect(this, "outputVarAdded(Object, ModuleInstanceGraphicView)");
		mi.need_view_info.connect(this, "getViewInfo(XmlCreator)");
	}
	
	protected void selected()
	{
		if (scene() != null)
			
			scene().clearSelection();
		
		setSelected(true);
	}
	
	/**
	 * Esegue il dialog per rinominare variaible emodulo istanziato.
	 */
	protected void rename()
	{
		new ModuleInstanceRenameDialog(this).exec();
	}
	
	/**
	 * Aggiunge un input al modulo istanziato. Il comando si aggiunge allo stack undo/redo.
	 */
	protected void addInput()
	{
		view.getUndoStack().push(new AddInputVariableCommand(instance.getInstancedModule(), 100, 100, this, project_tree, null));
	}
	
	/**
	 * Aggiunge un output al modulo istanziato. Il comando si aggiunge allo stack undo/redo.
	 */
	protected void addOutput()
	{
		new CreateOutputDialog(new OutputVariable(instance.getInstancedModule()), this, instance.getInstancedModule()).show();
	}
	
	/**
	 * Emette il segnale per la duplicazione della variabile 
	 * (per creare una nuova istanza dello stesso modulo istanziato).	
	 */
	protected void duplicate()
	{
		duplicate.emit(x + 200, y + 150, view);
	}
	
	/**
	 * Copia la variabile di istanza a seguito della selezione del comando da menu
	 * (per creare un'istanza di un nuovo modulo a partire da quello già istanziato relativo a questa vista).	
	 */
	protected void copy()
	{
		instance.copyInstance(instance.getInstancedModule(), this);
	}
	
	/**
	 * Copia la variabile di istanza a seguito di una richiesta di copia di un modulo.
	 * @param instanced_module modulo da copiare.
	 */
	protected void copy(Module instanced_module)
	{
		instance.copyInstance(instanced_module, this);
	}
	
	/**
	 * Quando è aggiunta una variabile di ingresso dall'interno del modulo o un ingresso da un duplicato
	 * arriva una notifica a questa vista che provvede ad aggiornarsi.
	 * @param in variabile di ingresso aggiunta o vista gancio.
	 * @param g_view vista grafica della variaible di istanza che ha aggiunto un ingresso.
	 */
	protected void inputVarAdded(Object in, ModuleInstanceGraphicView g_view)
	{
		if (g_view == null && in.getClass().getName().compareTo("model.InputVariable") == 0)
		{
			new InputVariableHookView(this, (InputVariable)in, (int)(x+R), (int)(y-8), view, "to_down");
		}
		else if (!this.equals(g_view) && in.getClass().getName().compareTo("view.InputVariableHookView") == 0)
		{
			new InputVariableHookView((InputVariable)((InputVariableHookView)in).getInputVariable(), this, (InputVariableHookView)in);
		}
	}
	
	/**
	 * Quando è aggiunta una variabile di uscita dall'interno del modulo o un uscita da un duplicato
	 * arriva una notifica a questa vista che provvede ad aggiornarsi.
	 * @param out variabile di uscita aggiunta o vista gancio.
	 * @param g_view vista grafica della variaible di istanza che ha aggiunto un output.
	 */
	protected void outputVarAdded(Object out, ModuleInstanceGraphicView g_view)
	{
		if (g_view == null  && out.getClass().getName().compareTo("model.OutputVariable") == 0)
		{
			new OutputVariableHookView(this, (OutputVariable)out, (int)(x+R), (int)(y+height), view, "to_up");
		}
		else if (!this.equals(g_view) && out.getClass().getName().compareTo("view.OutputVariableHookView") == 0)
		{
			new OutputVariableHookView((OutputVariable)((OutputVariableHookView)out).getOutputVariable(), this, (OutputVariableHookView)out);
		}
	}
	
	/**
	 * Fornisce le informazioni di layout al momento del salvataggio.
	 * @param xml l'oggetto utilizzato per la creazione dell'albero xml nel salvataggio.
	 */
	protected void getViewInfo(XmlCreator xml)
	{
		xml.setViewObject(this);
	}
	
	protected abstract void copy(ModuleInstance mi, GraphicView view, FrameModuleTreeView fm_tree);
	
	protected abstract void removeInstanceVar();
}