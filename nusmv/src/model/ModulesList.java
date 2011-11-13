/********************************************************************************
*                                                                               *
*   Module      :   ModuleList.java                                             *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.trolltech.qt.QSignalEmitter;

/**
 * Classe che tiene conto di tutti i moduli definiti all'interno del modello.
 * @author Silvia Lorenzini
 *
 */
public class ModulesList extends QSignalEmitter
{
	private List<Module> modules;
	private Counter counter;
	
	public Signal1<FrameModule>	frame_module_added;
	public Signal1<FsmModule>	fsm_module_added;
	public Signal1<Integer>		module_removed;
	public Signal2<Integer, String> module_name_changed;
	public Signal0				remove_all;
	public Signal0				to_save;
	public Signal0				to_save_as;
	public Signal0				to_print_all;
	public Signal0				to_load;
	public Signal0				to_convert;
	public Signal0				to_create_new;
	public Signal0				to_run;
	public Signal0				to_generate_smv;
	public Signal0				model_changed;
	public Signal0				to_close_all;
	public Signal0				to_preferences;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public ModulesList()
	{
		this.modules = new ArrayList<Module>(0);
		this.counter = new Counter();
		
		this.frame_module_added = new Signal1<FrameModule>();
		this.fsm_module_added = new Signal1<FsmModule>();
		this.module_removed = new Signal1<Integer>();
		this.module_name_changed = new Signal2<Integer, String>();
		this.remove_all = new Signal0();
		this.to_save = new Signal0();
		this.to_save_as = new Signal0();
		this.to_print_all = new Signal0();
		this.to_load = new Signal0();
		this.to_convert = new Signal0();
		this.to_create_new = new Signal0();
		this.to_run = new Signal0();
		this.to_generate_smv = new Signal0();
		this.model_changed = new Signal0();
		this.to_close_all = new Signal0();
		this.to_preferences = new Signal0();
	}
	
	/**
	 * Agginge un modulo frame alla lista dei moduli (se non lo contiene già) e connette i segnali.
	 * @param m il modulo frame da aggiungere.
	 */ 
	public void addFrameModule(FrameModule m)
	{
		if (!modules.contains(m))
		{
			modules.add(m);
			connectModuleSignals(m);
			frame_module_added.emit(m);
			if (m.getIndex() != -1)
			{
				counter.removeAvailableInt(m.getIndex());
			}
		}
	}
	
	/**
	 * Agginge un modulo fsm alla lista dei moduli (se non lo contiene già) e connette i segnali.
	 * @param m il modulo fsm da aggiungere.
	 */ 
	public void addFsmModule(FsmModule m)
	{
		if (!modules.contains(m))
		{
			modules.add(m);
			connectModuleSignals(m);
			fsm_module_added.emit(m);
			if (m.getIndex() != -1)
			{
				counter.removeAvailableInt(m.getIndex());
			}
		}
	}
	
	/**
	 * Rimuove un modulo dalla lista e disconnette i segnali.
	 * @param m
	 */
	public void removeModule(Module m)
	{
		int index = modules.indexOf(m);
		modules.remove(m);
		disconectModuleSignals(m);
		module_removed.emit(index);
		if (m.getIndex() != -1)
		{
			counter.addAvailableInt(m.getIndex());
		}
	}
	
	/**
	 * Indica se il modulo m è contenuto nella lista.
	 * @param m modulo di cui verificare la presenza.
	 * @return true se m è contenuto nella lista, false altrimenti.
	 */
	public boolean contains(Module m)
	{
		return modules.contains(m);
	}
	
	/**
	 * Restituisce il modulo ricercandolo per nome.
	 * @param name nome del modulo da cercare.
	 * @return il modulo se appartiene alla lista, null altrimenti.
	 */
	public Module getModule(String name)
	{
		Iterator<Module> it = modules.iterator();
		
		while (it.hasNext())
		{
			Module m = it.next();
			
			if (m.getName().compareTo(name) == 0)
			{
				return m;
			}
		}
		return null;
	}
	
	/**
	 * Rimuove tutti i moduli presenti nella lista.
	 */
	public void removeAll()
	{
		modules.removeAll(modules);
		counter.removeAllAvailableInt();
		remove_all.emit();
	}
	
	public Module getModule(int index)
	{
		if (index < modules.size())
		{
			return modules.get(index);
		}
		return  null;
	}
	
	/**
	 * 
	 * @return la dimensione della lisrta dei moduli.
	 */
	public int size()
	{
		return modules.size();
	}
	
	public List<Integer> getAvailbleIndexList()
	{
		return counter.getAvailableInt();
	}
	
	public int getStartModuleIndex()
	{
		return counter.getCount();
	}
	
	public int getModulIndex()
	{
		return counter.next();
	}
	
	public void setCounter(Counter c)
	{
		this.counter = c;
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected void moduleNameChanged(String name)
	{
		Module m = getModule(name);
		
		if (m != null)
		{
			if (m.getIndex() != -1)
			{
				counter.addAvailableInt(m.getIndex());
			}
			module_name_changed.emit(modules.indexOf(m), name);
		}
	}
	
	/********************************************************************************
	*                                                                               *
	*  					       PRIVATE FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	private void connectModuleSignals(Module m)
	{
		m.mod_name_changed.connect(this, "moduleNameChanged(String)");
		m.to_save.connect(this.to_save);
		m.to_save_as.connect(this.to_save_as);
		m.to_print_all.connect(this.to_print_all);
		m.model_changed.connect(this.model_changed);
		
		if (m.getClass().getName().compareTo("model.FrameModule") == 0)
		{
			((FrameModule)m).to_load.connect(this.to_load);
			((FrameModule)m).to_convert.connect(this.to_convert);
			((FrameModule)m).to_create_new.connect(this.to_create_new);
			((FrameModule)m).to_preferences.connect(this.to_preferences);
			
		}
		to_close_all.connect(m.close_win);
	}
	
	private void disconectModuleSignals(Module m)
	{
		m.mod_name_changed.disconnect(this, "moduleNameChanged(String)");
		m.to_save.disconnect(this.to_save);
		m.to_save_as.disconnect(this.to_save_as);
		m.to_print_all.disconnect(this.to_print_all);
		m.model_changed.connect(this.model_changed);
		
		if (m.getClass().getName().compareTo("model.FrameModule") == 0)
		{
			((FrameModule)m).to_load.disconnect(this.to_load);
			((FrameModule)m).to_convert.disconnect(this.to_convert);
			((FrameModule)m).to_create_new.disconnect(this.to_create_new);
			((FrameModule)m).to_run.disconnect(this.to_run);
			((FrameModule)m).to_generate_smv.disconnect(this.to_generate_smv);
			((FrameModule)m).to_preferences.disconnect(this.to_preferences);
		}
		to_close_all.disconnect(m.close_win);
	}
}
