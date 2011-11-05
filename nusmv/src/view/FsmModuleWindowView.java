/********************************************************************************
*                                                                               *
*   Module      :   FsmModuleWindowView.java                                    *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.LinePosition;
import model.FsmModule;
import model.InputVariable;
import model.LocalVariable;
import model.OutputVariable;
import model.State;
import model.Transition;
import widget.TreeWidget;

import com.trolltech.qt.core.Qt.CursorShape;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QToolButton;
import com.trolltech.qt.gui.QDialog.DialogCode;
import command.AddInputVariableCommand;
import command.AddOutputVariableCommand;
import command.AddStateCommand;

import dialog.ModifyLocalVarDialog;
import dialog.RemoveLocalVarDialog;
import dialog.VariableOptionsDialog;

/**
 * Vista della finestra relativa ad un modulo Frame.
 * @author Silvia Loerenzini
 *
 */
public class FsmModuleWindowView extends ModuleWindowView
{
	private QToolButton add_state;
	private QToolButton add_transition;
	
	private QMenu local_var_menu;
	private QMenu input_var_menu;
	private QMenu output_var_menu;
	
	public Signal1<State> state_added;
	public Signal1<Transition> transition_added;
	public Signal1<LocalVariable> local_var_added;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public FsmModuleWindowView(FsmModule module)
	{
		super(module);
		
		createActions();
		createToolbars();
		createMenu();
		createMenuBar();
		connectSignals();
		
		new_model_created.emit(true);
	}

	/**
	 * Costruttore.
	 * @param module modulo Fsm relativo alla finestra
	 * @param tree albero di progetto.
	 */
	public FsmModuleWindowView(FsmModule module, TreeWidget tree)
	{
		super(module, tree);
		
		createActions();
		createToolbars();
		createMenu();
		createMenuBar();		
		connectSignals();
		
		new_model_created.emit(true);
	}

	/**
	 * Aggiunge uno stato se è premuto il pulsante relativo oppure disabilita l'aggiunta di una
	 * transizione se è premuto il mouse sullo sfondo anziché su uno stato.
	 */
	@Override
	public void leftMouseClicked(int x, int y)
	{
		if (add_state.isChecked())
		{
			scene_view.getUndoStack().push(new AddStateCommand(this, x, y));
			scene_view.setAddObjectCursor(null);
			scene_view.setCursor(new QCursor(CursorShape.ArrowCursor));
			add_state.setChecked(false);
		}
		else if (add_transition.isChecked() && scene_view.itemAt(x, y) == null)
		{
			add_transition.toggle();
		}
	}
	
	/**
	 * Gestisce l'aggiunta di una transizione quando si clicca su uno stato ed è remuto il relativo pulsante.
	 * @param state_view la vista grafica dello stato su cui si è cliccato.
	 * @param x la posizione x del mouse.
	 * @param y la posizione y del mouse.
	 * @param start_pos la posizione di partenza (Top, Left, Bottom o Right) della transizione.
	 */
	public void stateClicked(StateGraphicView state_view, int x, int y, LinePosition start_pos)
	{
		if (add_transition.isChecked())
		{
			if (state_view != null)
			{
				Transition t = new Transition(state_view.getState());
				TransitionGraphicView tgv = new TransitionGraphicView(t, x, y, start_pos, scene_view);
				tgv.transition_drawing.connect(add_transition, "setChecked(boolean)");
				tgv.transition_lost.connect(t, "transitionLost()");
			}
//			Transition t = new Transition(state_view.getState());
//			TransitionGraphicView tgv = new TransitionGraphicView(t, x, y, state_view, scene_view);
//			tgv.transition_released.connect(add_transition, "setChecked(boolean)");
//			tgv.removed.connect(state_view, "transitionDeleted(TransitionGraphicView, StateGraphicView )");
//			tgv.added.connect(state_view, "transitionAdded(TransitionGraphicView, StateGraphicView)");
		}
	}
	
	/**
	 * @return il modulo Fsm relativo alla finestra.
	 */
	@Override
	public FsmModule getModule()
	{
		return (FsmModule)module;
	}
	
	public QToolButton getTransitionButton()
	{
		return add_transition;
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	protected void createActions()
	{
		add_state = new QToolButton();
		add_state.setToolTip("Add state");
		add_state.setIcon(new QIcon("src/pixmap/state.png"));
		add_state.setCheckable(true);
		
		add_transition = new QToolButton();
		add_transition.setToolTip("Add transition");
		add_transition.setIcon(new QIcon("src/pixmap/transition.png"));
		add_transition.setCheckable(true);
		
		super.createActions();
	}
	
	protected void addState()
	{
		if (add_transition.isChecked())
		{
			add_transition.setChecked(false);
		}
		if (add_state.isChecked())
		{
			setAddStateCursor(false);
		}
		else
		{
			setAddStateCursor(true);
		}
	}
	
	protected void addTransition()
	{
		if (add_state.isChecked())
		{
			add_state.setChecked(false);
			setAddStateCursor(false);
		}
	}
	
	protected void createMenu()
	{
		super.createMenu();
		
		QMenu fairness = menu.addMenu("Fairness");
		
		fairness.addAction(add_constraint);
		fairness.addAction(mod_constraint);
		fairness.addAction(remove_constraint);
		
		local_var_menu = menu.addMenu("Local variable");
		input_var_menu = menu.addMenu("Input variable");
		output_var_menu = menu.addMenu("Output variable");		
		
		QAction add_local_var = new QAction("Add", this);
		add_local_var.triggered.connect(this, "addLocalVar()");		
		QAction mod_local_var = new QAction("Modify", this);
		mod_local_var.triggered.connect(this, "modifyLocalVar()");
		QAction	remove_local_var = new QAction("Remove", this);
		remove_local_var.triggered.connect(this, "removeLocalVar()");
		
		local_var_menu.addAction(add_local_var);
		local_var_menu.addAction(mod_local_var);		
		local_var_menu.addAction(remove_local_var);
		
		QAction add_in_var = new QAction("Add", this);
		add_in_var.triggered.connect(this, "addInputVar()");
		QAction mod_in_var = new QAction("Modify", this);
		mod_in_var.triggered.connect(this, "modifyInputVar()");
		QAction	remove_in_var = new QAction("Remove", this);
		remove_in_var.triggered.connect(this, "removeInputVar()");
		
		input_var_menu.addAction(add_in_var);
		input_var_menu.addAction(mod_in_var);
		input_var_menu.addAction(remove_in_var);
		
		QAction add_out_var = new QAction("Add", this);
		add_out_var.triggered.connect(this, "addOutputVar()");
		QAction mod_out_var = new QAction("Modify", this);
		mod_out_var.triggered.connect(this, "modifyOutputVar()");
		QAction	remove_out_var = new QAction("Remove", this);
		remove_out_var.triggered.connect(this, "removeOutputVar()");
		
		output_var_menu.addAction(add_out_var);
		output_var_menu.addAction(mod_out_var);
		output_var_menu.addAction(remove_out_var);
		
		menu.addSeparator();
		menu.addMenu(fairness);
	}
	
	protected void createMenuBar()
	{
		model_menu.addAction(new QIcon("src/pixmap/state.png"), "Add state", add_state.pressed);
		model_menu.addAction(new QIcon("src/pixmap/transition.png"), "Add transition", add_transition.pressed);
		
		model_menu.addSeparator();
		
		model_menu.addMenu(local_var_menu);
		model_menu.addMenu(input_var_menu);
		model_menu.addMenu(output_var_menu);
		
		super.createMenuBar();
	}
	
	protected void setAddStateCursor(Boolean checked)
	{
		if (checked)
		{
			QCursor c = new QCursor(new QPixmap("src/pixmap/cursor_add_state.png"));
			scene_view.setAddObjectCursor(c);
		}
		else
		{
			scene_view.setAddObjectCursor(null);
			scene_view.setArrowCursor();
			scene_view.usingCursorShape(false);
		}
	}
	
	protected void connectSignals()
	{
		super.connectSignals();
		
		add_state.pressed.connect(this, "addState()");
		add_transition.pressed.connect(this, "addTransition()");
	}
	
	protected void inputAdded(InputVariable in)
	{
		new InputVariableDockView(in, input_tree);
	}
	
	protected void outputAdded(OutputVariable out)
	{
		new OutputVariableDockView(out, output_tree);
	}
	
	protected void addLocalVar()
	{
		LocalVariable lv = new LocalVariable(module);
		module.addLocalVariable(lv);
		QMenu local_var_menu = createLocalVarMenu(lv);
		VariableOptionsDialog dialog = new VariableOptionsDialog(lv);
		if (dialog.exec() != 0)
		{
			new LocalVariableDockView(local_tree, lv, local_var_menu);
			new LocalVariableTreeView(project_tree, lv);
			lv.added.emit();
		}
	}
	
	protected void modifyLocalVar()
	{
		new ModifyLocalVarDialog((FsmModule)module).exec();
	}
	
	protected void modifyLocalVarFromTree()
	{
		LocalVariable lv;
		
		if (local_tree.isActiveWindow() && local_tree.currentIndex().row()>= 0)
		{
			lv = (LocalVariable)((LocalVariableDockView)local_tree.currentItem()).getVariable();
		}
		else
		{
			lv = (LocalVariable)((LocalVariableTreeView)project_tree.currentItem()).getVariable();
		}
		new VariableOptionsDialog(lv).exec();
	}
	
	protected void removeLocalVar()
	{
		new RemoveLocalVarDialog((FsmModule)module).exec();
	}
	
	protected void addInputVar()
	{
		int index = module.getInputCount();
		InputVariable v = new InputVariable("in_"+index, null, "", "", module);
		module.in_hook_added.emit(v, null);
		
		scene_view.getUndoStack().push(new AddInputVariableCommand(module, v, project_tree));
	}
	
	protected void modifyInputVar()
	{
		return;
	}
	
	protected void removeInputVar()
	{
		return;
	}
	
	protected void addOutputVar()
	{
		OutputVariable v = new OutputVariable(module);
		VariableOptionsDialog dialog = new VariableOptionsDialog(v);
		dialog.exec();
		if (dialog.result() == DialogCode.Accepted.value())
		{
			module.out_hook_added.emit(v, null);			
			scene_view.getUndoStack().push(new AddOutputVariableCommand(module, v, project_tree));
		}
	}
	
	protected void modifyOutputVar()
	{
		return;
	}
	
	protected void removeOutputVar()
	{
		return;
	}
	
	private QMenu createLocalVarMenu(LocalVariable lv)
	{
		QMenu lv_menu = new QMenu();
		
		QAction edit = new QAction(tr("&Edit"), lv_menu);
		edit.triggered.connect(this, "modifyLocalVarFromTree()");
		
		lv_menu.addAction(edit);
		
		QAction delete = new QAction(tr("&Delete"), lv_menu);
		delete.setShortcut("Delete");
		delete.setIcon(new QIcon("src/pixmap/delete.png"));
		delete.triggered.connect(lv.removed);
		
		lv_menu.addAction(delete);
		
		return lv_menu;
	}
	
	private void createToolbars()
	{
		draw_bar.addWidget(add_state);
		draw_bar.addWidget(add_transition);
		
		super.createToolbar();
	}
}
