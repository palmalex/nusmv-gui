/********************************************************************************
*                                                                               *
*   Module      :   Module.java                                                 *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import view.ModuleInstanceGraphicView;
import view.ModuleTreeView;
import view.ModuleWindowView;
import widget.TreeWidget;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.QObject;

/**
 * Classe astratta relativa al modello di modulo generico.
 * @author Silvia Lorenzini
 *
 */
public abstract class Module extends QSignalEmitter 
{
	protected static ModulesList module_list = new ModulesList();
	
	public Signal1<String> 			mod_name_changed;
	public Signal0					mod_index_available;
	public Signal0					model_changed;
	public Signal1<LocalVariable> 	local_var_added;
	public Signal0					show_window;
	public Signal0 					removed;
	public Signal0 					added;
	public Signal1<Module>			copied;
	public Signal1<InputVariable>	input_added;
	public Signal1<OutputVariable>	output_added;
	public Signal2<Object, ModuleInstanceGraphicView> in_hook_added;
	public Signal2<Object, ModuleInstanceGraphicView> out_hook_added;
	public Signal0 to_save;
	public Signal0 to_save_as;
 	public Signal0 to_generate_smv;
	public Signal0 to_run;
	public Signal0 to_close;
	public Signal0 to_print;
	public Signal0 to_print_all;
	public Signal0 need_print_info;
	public Signal1<String> fairness_added;
	public Signal2<Integer, String> fairness_modified;
	public Signal1<Integer> fairness_removed;
	public Signal0 close_win;
	
	protected List<LocalVariable> local_variables;
	protected List<InputVariable> input_variables;
	protected List<OutputVariable> output_variables;
	protected List<String> fairness_list;
	protected String name;
	
	private Counter var_count;
	private Counter input_counter;
	private Counter output_counter;
	protected Counter copy_counter;
	private int module_index;
	
	protected QObject temp;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public Module()
	{
		this.var_count = new Counter();
		this.input_counter = new Counter();
		this.output_counter = new Counter();
		this.fairness_list = new ArrayList<String>(0);
		this.copy_counter = new Counter();
		this.temp = null;
		this.module_index = module_list.getModulIndex();
		this.name = "Module_" + module_index;
		
		local_variables = new ArrayList<LocalVariable>(0);
		input_variables = new ArrayList<InputVariable>(0);
		output_variables = new ArrayList<OutputVariable>(0);
		
		setSignals();
	}
	
	/**
	 * Costruttore.
	 * @param name nome del modulo.
	 */
	public Module(String name)
	{
		this.name = name;
		this.var_count = new Counter();
		this.input_counter = new Counter();
		this.output_counter = new Counter();
		this.fairness_list = new ArrayList<String>(0);
		this.copy_counter = new Counter();
		this.temp = null;
		this.module_index = -1;
		
		local_variables = new ArrayList<LocalVariable>(0);
		input_variables = new ArrayList<InputVariable>(0);
		output_variables = new ArrayList<OutputVariable>(0);
		
		setSignals();		
		
		NuSmvKeywords.checkName(name);
	}
	
	/**
	 * Costruttore.
	 * @param name nome del modulo.
	 * @param index indice del modulo (-1 se il nome non è quello di default)
	 * @param var_count contatore delle variabili locali e di istanza contenute nel modulo.
	 * @param in_count contatore dele variabili di ingresso al modulo.
	 * @param out_count contatore delle variabili di uscita al modulo.
	 */
	public Module(String name, int index, Counter var_count, Counter in_count, Counter out_count)
	{
		this.name = name;
		this.module_index = index;
		this.var_count = var_count;
		this.input_counter = in_count;
		this.output_counter = out_count;
		this.copy_counter = new Counter();
		this.temp = null;
		this.fairness_list = new ArrayList<String>(0);
		local_variables = new ArrayList<LocalVariable>(0);
		input_variables = new ArrayList<InputVariable>(0);
		output_variables = new ArrayList<OutputVariable>(0);
		
		setSignals();	
	}
	
	/**
	 * 
	 * @return la lista di variabili locali.
	 */
	public List<LocalVariable> getLocal_variables()
	{
		return local_variables;
	}
	
	/**
	 * Aggiunge na variabile locale alla lista.
	 * @param local_var variabile locale da aggiungere.
	 */
	public void addLocalVariable(LocalVariable local_var)
	{
		local_variables.add(local_var);
	}
	
	/**
	 * Rimuove una variabile locale dalla lista.
	 * @param local_var variabile locale da rimuovere.
	 */
	public void removeLocalVariable(LocalVariable local_var)
	{
		local_variables.remove(local_var);
	}
	
	public List<InputVariable> getInput_variables()
	{
		return input_variables;
	}
	
	public void addInputVariable(InputVariable input_var)
	{
		input_variables.add(input_var);
	}
	
	public void removeInputVariable(InputVariable input_var)
	{
		input_variables.remove(input_var);
	}
	
	public List<OutputVariable> getOutput_variables()
	{
		return output_variables;
	}
	
	public void addOutputVariable(OutputVariable output_var)
	{
		output_variables.add(output_var);
	}
	
	public void removeOutputVariable(OutputVariable output_var)
	{
		output_variables.remove(output_var);
	}
	
	/**
	 * 
	 * @return la lista dei vincoli fairness.
	 */
	public List<String> getFairnessFormula()
	{
		return fairness_list;
	}
	
	/**
	 * Aggiunge un vincolo fairness alla lista.
	 * @param constraint vincolo da aggiungere.
	 */
	public void addFairnessConstraint(String constraint)
	{
		fairness_list.add(constraint);
	}
	
	/**
	 * Modifica il vincolo fairness alla posizione index.
	 * @param index posizione del vincolo da modficare.
	 * @param constraint nuovo vincolo.
	 */
	public void modifyFairnessConstraint(int index, String constraint)
	{
		fairness_list.set(index, constraint);
	}
	
	/**
	 * Rimuove il vincolo di indice index
	 * @param index indice del vindcolo da rimuovere.
	 */
	public void removeFairnessConstraint(int index)
	{
		fairness_list.remove(index);
	}
	
	public String getName()
	{
		return name;
	}

	/**
	 * Imposta un nuovo nome e notifica le viste.
	 * @param name nuovo nome.
	 */
	public void setName(String name)
	{
		this.name = name;
		mod_name_changed.emit(name);
		module_index = -1;
	}

	/**
	 * Restituisce il primo indice disponibile per le variabili locali o di istanza.
	 * @return l'indice utilizzabile.
	 */
	public int getVar_count()
	{
		return var_count.next();
	}
	
	/**
	 * Aggiunge l'indice number a quelli utilizzabili per il conteggio delle variabili locali
	 * @param number nuovo numero utilizzabile
	 */
	public void setVarCountAvailableInt(int number)
	{
		var_count.addAvailableInt(number);
	}
	
	public void setInputCountAvailableInt(int number)
	{
		input_counter.addAvailableInt(number);
	}
	
	public void setOutputCountAvailableInt(int number)
	{
		output_counter.addAvailableInt(number);
	}
	
	public void removeVarCountAvailableInt(int number)
	{
		var_count.removeAvailableInt(number);
	}
	
	/**
	 * Restituisce una copia di sé.
	 * @param tree albero di progetto.
	 * @param m_view vista grafica relativa al modulo da cui e partito il comando di copia. 
	 * @return il modulo copiato.
	 */
	public abstract Module copy(TreeWidget tree, ModuleInstanceGraphicView m_view);

	public int getInputCount()
	{
		return input_counter.next();
	}
	
	public int getOutputCount()
	{
		return output_counter.next();
	}
	
	public List<Integer> getVarAvailableIndex()
	{
		return var_count.getAvailableInt();
	}
	
	public void setVarAvailableIndex(List<Integer> index_list)
	{
		var_count.setAvailableInt(index_list);
	}
	
	public int getNextVarIndex()
	{
		return var_count.getCount();
	}
	
	public void setStartVarIndex(int index)
	{
		var_count.setStartCount(index);
	}
	
	public List<Integer> getInputAvailableIndex()
	{
		return input_counter.getAvailableInt();
	}
	
	public void setInputAvailableIndex(List<Integer> index_list)
	{
		input_counter.setAvailableInt(index_list);
	}
	
	public int getNextInputIndex()
	{
		return input_counter.getCount();
	}
	
	public void setStartInputIndex(int index)
	{
		input_counter.setStartCount(index);
	}
	
	public List<Integer> getOutputAvailableIndex()
	{
		return output_counter.getAvailableInt();
	}
	
	public void setOutputAvailableIndex(List<Integer> index_list)
	{
		output_counter.setAvailableInt(index_list);
	}
	
	public int getNextOutputIndex()
	{
		return output_counter.getCount();
	}
	
	public void setStartOutputIndex(int index)
	{
		output_counter.setStartCount(index);
	}
	
	public void save()
	{
		to_save.emit();
	}
	
	public void generateSmv()
	{
		to_generate_smv.emit();
	}
	
	public void saveAs()
	{
		to_save_as.emit();
	}
	
	public void run()
	{
		to_run.emit();
	}
	
	public void close()
	{
		to_close.emit();
	}
	
	public void printAll()
	{
		to_print_all.emit();
	}
	
	public void setTemp(QObject obj)
	{
		temp = obj;
	}
	
	public QObject getTemp()
	{
		return temp;
	}
	
	public ModulesList moduleList()
	{
		return module_list;
	}
	
	public int getIndex()
	{
		return module_index;
	}
	
	public void setIndex(int index)
	{
		module_index = index;
	}
	
	public void setVarCounter(Counter vars)
	{
		this.var_count = vars;
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected abstract void removed();
	
	protected abstract void added();
	
	/**
	 * Copia le variabili definite all'interno del modulo.
	 * @param fm la copia del modulo.
	 * @param m_view la vista finestra relativa al modulo copiato.
	 * @param m_tree la vista-modulo relativa al modulo copiato.
	 * @param m_gview la vista grafica relativa al modulo copiato.
	 */
	protected void copyVariables(Module fm, ModuleWindowView m_view, ModuleTreeView m_tree, ModuleInstanceGraphicView m_gview)
	{
		Iterator<LocalVariable> it1 = local_variables.iterator();
		
		while (it1.hasNext())
		{
			LocalVariable lv = it1.next();
			fm.addLocalVariable(lv.copy(fm, m_view, m_tree));
		}
		
		Iterator<InputVariable> it2 = input_variables.iterator();
		
		while (it2.hasNext())
		{
			InputVariable in = it2.next();
			fm.addInputVariable(in.copy(fm, m_view, m_tree, m_gview));
		}
		
		Iterator<OutputVariable> it3 = output_variables.iterator();
		
		while (it3.hasNext())
		{
			OutputVariable out = it3.next();
			fm.addOutputVariable(out.copy(fm, m_view, m_tree, m_gview));
		}
	}
	
	/********************************************************************************
	*                                                                               *
	*  					       PRIVATE FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	private void setSignals()
	{
		this.local_var_added 	= new Signal1<LocalVariable>();
		this.model_changed		= new Signal0();
		this.mod_index_available = new Signal0();
		this.mod_name_changed 	= new Signal1<String>();
		this.show_window 		= new Signal0();
		this.removed 			= new Signal0();
		this.added 				= new Signal0();
		this.copied 			= new Signal1<Module>();
		this.input_added		= new Signal1<InputVariable>();
		this.output_added		= new Signal1<OutputVariable>();
		in_hook_added 			= new Signal2<Object, ModuleInstanceGraphicView>();
		out_hook_added 			= new Signal2<Object, ModuleInstanceGraphicView>();
		to_save	 				= new Signal0();
		to_save_as 				= new Signal0();
		to_generate_smv 		= new Signal0();
		to_run 					= new Signal0();
		to_close 				= new Signal0();
		to_print 				= new Signal0();
		to_print_all 			= new Signal0();
		fairness_added 			= new Signal1<String>();
		fairness_modified 		= new Signal2<Integer, String>();
		fairness_removed 		= new Signal1<Integer>();
		need_print_info 		= new Signal0();
		close_win 				= new Signal0();
		
		removed.connect(this, "removed()");
		added.connect(this, "added()");
	}
}
