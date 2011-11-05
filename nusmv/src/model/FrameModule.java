/********************************************************************************
*                                                                               *
*   Module      :   FrameModule.java                                            *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import item.GraphicView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import view.FrameModuleTreeView;
import view.FrameModuleWindowView;
import view.ModuleInstanceGraphicView;
import widget.TreeWidget;

/**
 * Classe relativa al modello di un modulo Frame.
 * @author Silvia Lorenzini
 *
 */
public class FrameModule extends Module
{
	private List<FrameModuleInstance> frame_module_instances;
	private List<FsmModuleInstance> fsm_module_instances;
	private List<Specification> specifications;
	public Signal2<FrameModuleInstance, FrameModule> frame_instance_copied;
	public Signal2<FsmModuleInstance, FsmModule> fsm_instance_copied;
	public Signal1<Specification> specification_added;
	public Signal0 to_load;
	public Signal0 to_convert;
	public Signal0 to_create_new;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public FrameModule()
	{
		super();
		this.frame_module_instances = new ArrayList<FrameModuleInstance>(0);
		this.fsm_module_instances = new ArrayList<FsmModuleInstance>(0);
		this.specifications = new ArrayList<Specification>(0);
		this.frame_instance_copied = new Signal2<FrameModuleInstance, FrameModule>();
		this.fsm_instance_copied = new Signal2<FsmModuleInstance, FsmModule>();
		this.specification_added = new Signal1<Specification>();
		to_load = new Signal0();
		to_convert = new Signal0();
		to_create_new = new Signal0();
	}
	
	/**
	 * Costruttore.
	 * @param name nome del modulo.
	 */
	public FrameModule(String name)
	{
		super(name);
		
		this.frame_module_instances = new ArrayList<FrameModuleInstance>(0);
		this.fsm_module_instances = new ArrayList<FsmModuleInstance>(0);
		this.specifications = new ArrayList<Specification>(0);
		this.frame_instance_copied = new Signal2<FrameModuleInstance, FrameModule>();
		this.fsm_instance_copied = new Signal2<FsmModuleInstance, FsmModule>();
		this.specification_added = new Signal1<Specification>();
		to_load = new Signal0();
		to_convert = new Signal0();
		to_create_new = new Signal0();
	}
	
	/**
	 * Costruttore.
	 * @param name nome del modulo.
	 * @param index indice del modulo.
	 * @param var_count contatore delle variabili locali contenute nel modulo.
	 * @param in_count contatore delle variabili di ingresso.
	 * @param out_count contatore delle variabili d'uscita.
	 */
	public FrameModule(String name, int index, Counter var_count, Counter in_count, Counter out_count)
	{
		super(name, index, var_count, in_count, out_count);
		
		this.frame_module_instances = new ArrayList<FrameModuleInstance>(0);
		this.fsm_module_instances = new ArrayList<FsmModuleInstance>(0);
		this.specifications = new ArrayList<Specification>(0);
		this.frame_instance_copied = new Signal2<FrameModuleInstance, FrameModule>();
		this.fsm_instance_copied = new Signal2<FsmModuleInstance, FsmModule>();
		this.specification_added = new Signal1<Specification>();
		to_load = new Signal0();
		to_convert = new Signal0();
		to_create_new = new Signal0();
	}

	/**
	 * 
	 * @return lista dei FrameModuleInstance preseti nel modulo.
	 */
	public List<FrameModuleInstance> getFrameModuleInstances()
	{
		return frame_module_instances;
	}

	/**
	 * Aggiunge un'istanza di un modulo Frame alla lista.
	 * @param module_instance istanza di un modulo Frame.
	 */
	public void addFrameModuleInstance(FrameModuleInstance module_instance)
	{
		frame_module_instances.add(module_instance);
	}
	
	/**
	 * Rimuove un'istanza di un modulo Frame dalla lista.
	 * @param fm istanza del modulo frame da rimuovere.
	 */
	public void removeFrameModuleInstance(FrameModuleInstance fm)
	{
		boolean removed = frame_module_instances.remove(fm);
		
		assert removed;
	}
	
	/**
	 * 
	 * @return lista dei FsmModuleInstance preseti nel modulo.
	 */
	public List<FsmModuleInstance> getFsmModuleInstances()
	{
		return fsm_module_instances;
	}

	/**
	 * Aggiunge un'istanza di un modulo Fsm alla lista.
	 * @param module_instance istanza di un modulo Fsm.
	 */
	public void addFsmModuleInstance(FsmModuleInstance module_instance)
	{
		fsm_module_instances.add(module_instance);
		
//		if (!module_list.contains(module_instance.getInstancedModule()))
//		{
//			module_list.addFsmModule((FsmModule)module_instance.getInstancedModule());
//		}
	}
	
	/**
	 * Rimuove un'istanza di un modulo Fsm dalla lista.
	 * @param fsm istanza di un modulo Fsm.
	 */
	public void removeFsmModuleInstance(FsmModuleInstance fsm)
	{
		boolean removed =  fsm_module_instances.remove(fsm);
		
		assert removed;
	}

	/**
	 * Restituisce la lista di specifiche del sistema.
	 * @return lista di Specification
	 */
	public List<Specification> getFormulas()
	{
		return specifications;
	}
	
	/**
	 * Imposta la lista delle specifiche.
	 * @param formulas lista di Specification
	 */
	public void setFormulas(List<Specification> formulas)
	{
		this.specifications = formulas;
	}
	
	/**
	 * Aggiunge una specifica alla lista.
	 * @param s specifica di sistema da aggiungere.
	 */
	public void addSpecification(Specification s)
	{
		specifications.add(s);
	}

	/**
	 * Crea un nuovo FrameModule e vi aggiunge le copie degli elementi contenuti in questo.
	 */
	public Module copy(TreeWidget tree, ModuleInstanceGraphicView m_view)
	{
		FrameModule fm = new FrameModule(name + "." + copy_counter.next());
		FrameModuleWindowView fm_view = new FrameModuleWindowView(fm, tree);
		FrameModuleTreeView fm_tree = new FrameModuleTreeView(fm, tree);
		
		copyModules(fm, fm_view.getView(), fm_tree);		
		copyVariables(fm, fm_view, fm_tree, m_view);
		return fm;
	}
	

	/**
	 * Emette il segnale di load.
	 */
	public void load()
	{
		to_load.emit();
	}
	
	/**
	 * Emette il segnale di convert.
	 */
	
	public void convertStateflow()
	{
		to_convert.emit();
	}
	
	/**
	 * Emette il segnale di creazione di un nuovo modello.
	 */
	public void createNewModel()
	{
		to_create_new.emit();
	}
	
	/**
	 * Aggiunge tutti i sottomoduli da stampare.
	 * @param scene_list lista delle scene da stampare.
	 */
	public void printSubmodules(List<GraphicView> scene_list)
	{
		Iterator<FrameModuleInstance> it1 = frame_module_instances.iterator();
		
		while (it1.hasNext())
		{
			FrameModule m = (FrameModule)it1.next().getInstancedModule();
			m.need_print_info.emit();
			scene_list.add((GraphicView)m.getTemp());
			m.printSubmodules(scene_list);
		}
		
		Iterator<FsmModuleInstance> it2 = fsm_module_instances.iterator();
		
		while (it2.hasNext())
		{
			FsmModule m = (FsmModule)it2.next().getInstancedModule();
			m.need_print_info.emit();
			scene_list.add((GraphicView)m.getTemp());
		}
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Rimuove questo modulo dalla lista dei moduli.
	 */
	protected void removed()
	{
		module_list.removeModule(this);
	}
	
	/**
	 * Aggiunge questo modulo alla lista dei moduli.
	 */
	protected void added()
	{
		if (name.compareTo("main") != 0)
		
			module_list.addFrameModule(this);
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	private void copyModules(FrameModule fm, GraphicView view, FrameModuleTreeView fm_tree)
	{
		Iterator<FrameModuleInstance> it1 = frame_module_instances.iterator();
		
		while (it1.hasNext())
		{
			FrameModuleInstance fmi = it1.next();
			fm.addFrameModuleInstance(fmi.copy(fm, view, fm_tree));
		}
		
		Iterator<FsmModuleInstance> it2 = fsm_module_instances.iterator();
		
		while (it2.hasNext())
		{
			FsmModuleInstance fmi = it2.next();
			fm.addFsmModuleInstance(fmi.copy(fm, view, fm_tree));
		}
	}
}
