/********************************************************************************
*                                                                               *
*   Module      :   FsmModuleInstance.java                                      *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import item.GraphicView;
import command.AddFsmModuleInstanceCommand;

import view.FrameModuleTreeView;
import view.FsmModuleWindowView;
import view.ModuleInstanceGraphicView;
import widget.TreeWidget;

/**
 * Classe relativa al modello della variabile di istanza di un modulo Fsm.
 * @author Silvia Lorenzini
 *
 */
public class FsmModuleInstance extends ModuleInstance
{

	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public FsmModuleInstance(FrameModule parent_module, TreeWidget tree)
	{		
		super(parent_module, tree);
		createFsmModule();
		connectSignals();
	}
	
	/**
	 * Costruttore
	 * @param parent il modulo Frame all'interno del quale è istanziato.
	 * @param child l'FsmModule istanziato
	 * @param tree l'albero di progetto.
	 */
	public FsmModuleInstance(FrameModule parent, FsmModule child, TreeWidget tree)
	{
		super(parent, tree);
		createFsmModule(child);
		connectSignals();
	}
	
	/**
	 * Rimuove sé stesso dal modulo padre e propaga il segnale. 
	 */
	public void removeInstanceVar()
	{
		parent_module.removeFsmModuleInstance(this);
		parent_module.setVarCountAvailableInt(var_index);
		instanced_module.removed.emit();
		instanced_module.mod_index_available.emit();
		removed.emit();
	}

	/**
	 * Emette il segnale per la visualizzazione della finestra relativa al modulo istanziato.
	 */
	public void edit()
	{
		instanced_module.show_window.emit();
	}

	/**
	 * Crea un duplicato di sé e lo aggiunge al padre.
	 */
	public void duplicate(Integer x, Integer y, GraphicView view)
	{
		FsmModuleInstance fmi = new FsmModuleInstance(parent_module, (FsmModule)instanced_module, tree);
		AddFsmModuleInstanceCommand c = new AddFsmModuleInstanceCommand(fmi, tree, parent_module.getName(), x, y, view, true, this);
		view.getUndoStack().push(c);
	}

	/**
	 * Copia eseguita quando si seleziona l'azione copy da menu o con ctrl+c.
	 */
	public void copyInstance(Module instanced_module, ModuleInstanceGraphicView m_view)
	{
		FsmModule mod = (FsmModule)instanced_module.copy(tree, m_view);
		copied.emit(new FsmModuleInstance(parent_module, mod, tree), instanced_module);
	}
	
	/**
	 * Chiamata da Module quando fa la copia dei moduli istanziati.
	 * @param parent il modulo padre.
	 * @param view la nuova vista grafica.
	 * @param fm_tree la vista del modulo padre.
	 * @return una copia di sé.
	 */
	public FsmModuleInstance copy(FrameModule parent, GraphicView view, FrameModuleTreeView fm_tree)
	{
		FsmModuleInstance fmi =  new FsmModuleInstance(parent, (FsmModule)instanced_module, tree);
		to_copy.emit(fmi, view, fm_tree);
		
		return fmi;		
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	private void connectSignals()
	{
		copied.connect(parent_module.fsm_instance_copied);
		instanced_module.to_generate_smv.connect(parent_module, "generateSmv()");
		instanced_module.to_run.connect(parent_module, "run()");
		instanced_module.to_close.connect(parent_module, "close()");
		instanced_module.removed.connect(this.removed);
		instanced_module.added.connect(this.added);
	}
	
	private void createFsmModule()
	{
		this.instanced_module = new FsmModule();
		new FsmModuleWindowView((FsmModule)instanced_module, tree);
	}
	
	private void createFsmModule(FsmModule child)
	{
		this.instanced_module = child;
	}
}
