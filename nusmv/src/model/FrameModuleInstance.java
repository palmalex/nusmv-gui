/********************************************************************************
*                                                                               *
*   Module      :   FrameModuleInstance.java                                    *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import item.GraphicView;
import command.AddFrameModuleInstanceCommand;

import view.FrameModuleTreeView;
import view.FrameModuleWindowView;
import view.ModuleInstanceGraphicView;
import widget.TreeWidget;

/**
 * Classe relativa al modello della variabile di istanza di un modulo Frame.
 * @author Silvia Lorenzini
 *
 */
public class FrameModuleInstance extends ModuleInstance 
{	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public FrameModuleInstance(FrameModule parent_module, TreeWidget tree)
	{
		super(parent_module, tree);
		createFrameModule();
		connectSignals();
	}
	
	/**
	 * Costruttore
	 * @param parent il modulo Frame all'interno del quale è istanziato.
	 * @param child il FrameModule istanziato
	 * @param tree l'albero di progetto.
	 */
	public FrameModuleInstance(FrameModule parent, FrameModule child, TreeWidget tree)
	{
		super(parent, tree);
		createFrameModule(child);
		connectSignals();
	}
	
	/**
	 * Emette il segnale per la visualizzazione della finestra relativa al modulo istanziato.
	 */
	public void edit()
	{
		instanced_module.show_window.emit();
	}
	
	/**
	 * Rimuove sé stesso dal modulo padre e propaga il segnale. 
	 */
	public void removeInstanceVar()
	{
		parent_module.removeFrameModuleInstance(this);
		if (var_index != -1)
		{
			parent_module.setVarCountAvailableInt(var_index);
		}
		instanced_module.removed.emit();
		instanced_module.mod_index_available.emit();
		removed.emit();
	}
	
	/**
	 * Crea un duplicato di sé e lo aggiunge al padre.
	 */
	public void duplicate(Integer x, Integer y, GraphicView view)
	{
		FrameModuleInstance fmi = new FrameModuleInstance(parent_module, (FrameModule)instanced_module, tree);
		AddFrameModuleInstanceCommand c = new AddFrameModuleInstanceCommand(fmi, tree, parent_module.getName(), x, y, view, true, this);
		view.getUndoStack().push(c);	
	}
	
	/**
	 * Copia eseguita quando si seleziona l'azione copy da menu o con ctrl+c.
	 */
	public void copyInstance(Module instanced_module, ModuleInstanceGraphicView m_view)
	{
		//copia del modulo istanziato
		FrameModule mod = (FrameModule)instanced_module.copy(tree, m_view);
		
		//nuovo frame module che ha come figlio il modulo copiato sopra
		copied.emit(new FrameModuleInstance(parent_module, mod, tree), instanced_module);
	}
	
	/**
	 * Chiamata da Module quando fa la copia dei moduli istanziati.
	 * @param parent il modulo padre.
	 * @param view la nuova vista grafica.
	 * @param fm_tree la vista del modulo padre.
	 * @return una copia di sé.
	 */
	public FrameModuleInstance copy(FrameModule parent, GraphicView view, FrameModuleTreeView fm_tree)
	{
		FrameModuleInstance fmi =  new FrameModuleInstance(parent, (FrameModule)instanced_module, tree);
		to_copy.emit(fmi, view, fm_tree); // dice alle viste di copiarsi..
		
		return fmi;		
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	private void connectSignals()
	{
		copied.connect(parent_module.frame_instance_copied);
		instanced_module.to_run.connect(parent_module, "run()");
		instanced_module.to_close.connect(parent_module, "close()");
		instanced_module.removed.connect(this.removed);
		instanced_module.added.connect(this.added);
	}

	private void createFrameModule()
	{
		this.instanced_module = new FrameModule();
		new FrameModuleWindowView((FrameModule)instanced_module, tree);
	}
	
	private void createFrameModule(FrameModule child)
	{
		this.instanced_module = child;
	}
}
