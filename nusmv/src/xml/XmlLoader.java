package xml;

import item.LinePosition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Counter;
import model.FormulaType;
import model.FrameModule;
import model.FrameModuleInstance;
import model.FsmModule;
import model.FsmModuleInstance;
import model.InputVariable;
import model.LocalVariable;
import model.Module;
import model.ModuleInstance;
import model.ModulesList;
import model.OutputVariable;
import model.Specification;
import model.State;
import model.Transition;
import model.Type;
import view.FrameModuleInstanceGraphicView;
import view.FrameModuleInstanceTreeView;
import view.FrameModuleTreeView;
import view.FrameModuleWindowView;
import view.FsmModuleInstanceGraphicView;
import view.FsmModuleInstanceTreeView;
import view.FsmModuleTreeView;
import view.FsmModuleWindowView;
import view.InputVariableDockView;
import view.InputVariableGraphicView;
import view.InputVariableHookView;
import view.InputVariableTreeView;
import view.LocalVariableDockView;
import view.LocalVariableGraphicView;
import view.LocalVariableTreeView;
import view.ModuleInstanceGraphicView;
import view.ModuleWindowView;
import view.OutputVariableDockView;
import view.OutputVariableGraphicView;
import view.OutputVariableHookView;
import view.OutputVariableTreeView;
import view.StateGraphicView;
import view.StateTreeView;
import view.TransitionGraphicView;
import widget.TreeWidget;

import com.trolltech.qt.core.QFile;
import com.trolltech.qt.xml.QDomDocument;
import com.trolltech.qt.xml.QDomElement;
import com.trolltech.qt.xml.QDomNodeList;

public class XmlLoader
{
	private QDomDocument dom;
	private FrameModuleWindowView main_win;
	private List<Module> created_modules;
	private List<InputConnectInfo> input_to_connect;
	
	public XmlLoader(String path, FrameModuleWindowView f)
	{
		this.main_win = f; 
		this.created_modules = new ArrayList<Module>(0);
		this.input_to_connect = new ArrayList<InputConnectInfo>(0);
		createDomFromFile(path);
		parseDom(path);
	}
	
	private void createDomFromFile(String path)
	{
		dom = new QDomDocument();
		QFile file = new QFile(path);
		QFile.OpenMode mode = new QFile.OpenMode();
	    mode.set(QFile.OpenModeFlag.ReadOnly);

	    if (!file.open(mode))
	        return;
	    if (!dom.setContent(file).success) {
	        file.close();
	        return;
	    }
	    file.close();
	}
	
	private void parseDom(String path)
	{
		QDomNodeList modules = dom.elementsByTagName("Module");
		
		for (int i = 0; i < modules.length(); i++)
		{
			QDomElement main;
			if ((main = modules.at(i).toElement()).attribute("name").compareTo("main") == 0)
			{
				createModuleMain(main, path);
				
				return;
			}
		}
	}
	
	private void createModuleMain(QDomElement main, String path)
	{
		FrameModule mod_main = main_win.getModule();
		main_win.setModelName(dom.firstChildElement().tagName());
		main_win.setModelPath(path);
		main_win.createProjectTree();
		Counter vars = getAvailableVarIndexList(main);
		mod_main.setVarCounter(vars);
		
		createCounter(mod_main);
		createModules(mod_main);
		
		createFrameModule(main, mod_main);
		
		createViews();
		connectInput();
	}
	
	private void createCounter(FrameModule main)
	{
		QDomElement mod_counter = dom.elementsByTagName("ModuleCounter").item(0).toElement();
		String start = mod_counter.attribute("start");
		List<Integer> available_int = new ArrayList<Integer>(0);
		QDomNodeList list = mod_counter.elementsByTagName("Count");
		
		for (int i = 0; i < list.size(); i++)
		{
			int index = Integer.valueOf(list.item(i).toElement().attribute("index"));
			available_int.add(index);
		}
		
		Counter counter = new Counter(available_int, Integer.valueOf(start));
		main.moduleList().setCounter(counter);
	}
	
	private void createModules(FrameModule main)
	{
		ModulesList ml = main.moduleList();
		
		QDomNodeList frame_modules = dom.elementsByTagName("Module");
		QDomNodeList fsm_modules = dom.elementsByTagName("FSM");
		int main_index = 0;
		for (int i = 0; i < frame_modules.size(); i++)
		{
			QDomElement e = frame_modules.item(i).toElement();
			String name = e.attribute("name");
			if (name.compareTo("main") != 0)
			{
				int index = Integer.valueOf(e.attribute("index"));
				Counter vars = getAvailableVarIndexList(e);
				Counter inputs = getAvailableInputIndexList(e);
				Counter outputs = getAvailableOutputIndexList(e);
				
				FrameModule m = new FrameModule(name, index, vars, inputs, outputs);
				ml.addFrameModule(m);
			}
			else
			{
				main_index = i;
			}
		}
		
		for (int i = 0; i < fsm_modules.size(); i++)
		{
			QDomElement e = fsm_modules.item(i).toElement();
			String name = e.attribute("name");
			int index = Integer.valueOf(e.attribute("index")).intValue();
			Counter vars = getAvailableVarIndexList(e);
			Counter inputs = getAvailableInputIndexList(e);
			Counter outputs = getAvailableOutputIndexList(e);
			
			FsmModule m = new FsmModule(name, index, vars, inputs, outputs);
			ml.addFsmModule(m);
		}
		
		int counter = 0;
		for (int i = 0; i < frame_modules.size(); i++)
		{
			if (i != main_index)
			{
				createFrameModule(frame_modules.item(i).toElement(), (FrameModule)ml.getModule(counter));
				counter++;
			}
		}
		
		for (int i = 0; i < fsm_modules.size(); i++, counter++)
		{
			createFsmModule(fsm_modules.item(i).toElement(), (FsmModule)ml.getModule(counter));
		}
	}
	
	private FrameModule createFrameModule(QDomElement module, FrameModule parent)
	{		
		QDomNodeList frame_modules = module.elementsByTagName("FrameInstanceVariable");
		QDomNodeList fsm_modules = module.elementsByTagName("FsmInstanceVariable");
		QDomNodeList local_vars = module.elementsByTagName("LocalVariable");
		QDomNodeList input_vars = module.elementsByTagName("InputVariable");
		QDomNodeList output_vars = module.elementsByTagName("OutputVariable");
		QDomNodeList specifications = module.elementsByTagName("Specification");
		QDomNodeList fairness = module.elementsByTagName("Fairness");
		
		addFrameModuleInstance(parent, frame_modules);
		addFsmModuleInstance(parent, fsm_modules);
		addLocalVariable(parent, local_vars);
		addInputVariable(parent, input_vars);
		addOutputVariable(parent,  output_vars);
		addSpecifications(parent, specifications);
		addFairness(parent, fairness);
		
		return parent;
	}
	
	private FsmModule createFsmModule(QDomElement module, FsmModule parent)
	{
		QDomNodeList local_vars = module.elementsByTagName("LocalVariable");
		QDomNodeList input_vars = module.elementsByTagName("InputVariable");
		QDomNodeList output_vars = module.elementsByTagName("OutputVariable");
		QDomNodeList states = module.elementsByTagName("State");
		QDomNodeList transitions = module.elementsByTagName("Transition");
		QDomNodeList fairness = module.elementsByTagName("Fairness");
		
		addLocalVariable(parent, local_vars);
		addInputVariable(parent, input_vars);
		addOutputVariable(parent,  output_vars);
		addState(parent, states);
		addTransition(parent, transitions);
		addFairness(parent, fairness);
				
		return parent;
	}
	
	private void addFrameModuleInstance(FrameModule parent, QDomNodeList frame_instances)
	{
		for (int i = 0; i < frame_instances.length(); i++)
		{
			QDomElement instance = frame_instances.at(i).toElement();
			FrameModuleInstance f_ist = createFrameModuleInstance(instance, parent);
			parent.addFrameModuleInstance(f_ist);
		}
	}
	
	private void addFsmModuleInstance(FrameModule parent, QDomNodeList fsm_instances)
	{
		for (int i = 0; i < fsm_instances.length(); i++)
		{
			QDomElement instance = fsm_instances.at(i).toElement();
			FsmModuleInstance f_ist = createFsmModuleInstance(instance, parent);
			parent.addFsmModuleInstance(f_ist);
		}
	}
	
	private void addLocalVariable(Module parent, QDomNodeList local_vars)
	{
		for (int i = 0; i < local_vars.length(); i++)
		{
			QDomElement var = local_vars.at(i).toElement();
			LocalVariable lv = createLocalVariable(var, parent);
			parent.addLocalVariable(lv);
		}
	}
	
	private void addInputVariable(Module parent, QDomNodeList input_vars)
	{
		for (int i = 0; i < input_vars.length(); i++)
		{
			QDomElement var = input_vars.at(i).toElement();
			InputVariable iv = createInputVariable(var, parent);
			parent.addInputVariable(iv);
		}
	}
	
	private void addOutputVariable(Module parent, QDomNodeList output_vars)
	{
		for (int i = 0; i < output_vars.length(); i++)
		{
			QDomElement var = output_vars.at(i).toElement();
			OutputVariable ov = createOutputVariable(var, parent);
			parent.addOutputVariable(ov);
		}
	}
	
	private void addState(FsmModule parent, QDomNodeList states)
	{
		for (int i = 0; i < states.length(); i++)
		{
			State s = createState(states.at(i).toElement(), parent);
			parent.addState(s);
		}
	}
	
	private void addTransition(FsmModule parent, QDomNodeList transitions)
	{
		for (int i = 0; i < transitions.length(); i++)
		{
			createTransition(transitions.at(i).toElement(), parent);
		}
	}
	
	private void addSpecifications(FrameModule parent, QDomNodeList specifications)
	{
		for (int i = 0; i < specifications.length(); i++)
		{
			QDomElement spec = specifications.item(i).toElement();
			String type = spec.attribute("type");
			String formula = spec.attribute("formula");
			
			Specification s = new Specification(formula, FormulaType.valueOf(type), parent);
			
			parent.addSpecification(s);
		}
	}
	
	private void addFairness(Module parent, QDomNodeList fairness_list)
	{
		for (int i = 0; i < fairness_list.length(); i++)
		{
			QDomElement spec = fairness_list.item(i).toElement();
			String constraint = spec.attribute("constraint");
			
			parent.addFairnessConstraint(constraint);
			parent.fairness_added.emit(constraint);
		}
	}
	
	private FrameModuleInstance createFrameModuleInstance(QDomElement instance, FrameModule parent)
	{
		QDomElement prop = instance.firstChildElement("Properties");
		String mod_name = prop.attribute("module_name");
		String instance_name = prop.attribute("instance_name");
		boolean process = Boolean.parseBoolean(prop.attribute("process"));
	
		FrameModule instanced_module = (FrameModule)parent.moduleList().getModule(mod_name);
		
		FrameModuleInstance mod = new FrameModuleInstance(parent, instanced_module, main_win.getProjectTree()); // c'era -1
		mod.setName(instance_name);
		mod.setProcess(process);
		
		return mod;
	}
	
	private FsmModuleInstance createFsmModuleInstance(QDomElement instance, FrameModule parent)
	{
		QDomElement prop = instance.firstChildElement("Properties");
		String mod_name = prop.attribute("module_name");
		String instance_name = prop.attribute("instance_name");
		boolean process = Boolean.parseBoolean(prop.attribute("process"));
		
				
		FsmModule fsm_mod = (FsmModule)parent.moduleList().getModule(mod_name);
		FsmModuleInstance mod = new FsmModuleInstance(parent, fsm_mod, main_win.getProjectTree()); // c'era -1
		mod.setName(instance_name);
		mod.setProcess(process);
		
		return mod;
	}
	
	private LocalVariable createLocalVariable(QDomElement var, Module parent)
	{
		QDomElement prop = var.firstChildElement("Properties");
		String name = prop.attribute("name");
		String values = prop.attribute("values");
		String init = prop.attribute("initial");
		String type_text = prop.attribute("type");
		Type type = null;
		
		if (type_text.compareTo("null") != 0)
		{
			type = Type.valueOf(type_text);
		}
		
		LocalVariable lv = new LocalVariable(name, type, values, init, parent);
				
		return lv;
	}
	
	private InputVariable createInputVariable(QDomElement var, Module parent)
	{
		QDomElement prop = var.firstChildElement("Properties");
		String name = prop.attribute("name");
		String values = prop.attribute("values");
		String init = prop.attribute("initial");
		String type_text = prop.attribute("type");
		Type type = null;
		
		if (type_text.compareTo("null") != 0)
		{
			type = Type.valueOf(type_text);
		}
		
		InputVariable iv = new InputVariable(name, type, values, init, parent);
				
		return iv;
	}
	
	private OutputVariable createOutputVariable(QDomElement var, Module parent)
	{
		QDomElement prop = var.firstChildElement("Properties");
		String name = prop.attribute("name");
		String values = prop.attribute("values");
		String init = prop.attribute("initial");
		String type_text = prop.attribute("type");
		Type type = null;
		
		if (type_text.compareTo("null") != 0)
		{
			type = Type.valueOf(type_text);
		}
		
		OutputVariable ov = new OutputVariable(name, type, values, init, parent);
		
		return ov;
	}
	
	private State createState(QDomElement state, FsmModule parent)
	{
		QDomElement prop = state.firstChildElement("Properties");
		String name = prop.attribute("name");
		boolean initial = Boolean.valueOf(prop.attribute("initial"));
		
		State s = new State(parent, name);
		s.setInitial(initial);
		
		QDomNodeList onentry_list = prop.elementsByTagName("Onentry");
		for (int i = 0; i < onentry_list.length(); i++)
		{
			String onentry = onentry_list.item(i).toElement().attribute("action");
			s.addOnentry(onentry);
		}
		
		QDomNodeList during_list = prop.elementsByTagName("During");
		for (int i = 0; i < during_list.length(); i++)
		{
			String during = during_list.item(i).toElement().attribute("action");
			s.addDuring(during);
		}
		
		QDomNodeList onexit_list = prop.elementsByTagName("Onexit");
		for (int i = 0; i < onexit_list.length(); i++)
		{
			String onexit = onexit_list.item(i).toElement().attribute("action");
			s.addOnexit(onexit);
		}
		
		return s;
	}
	
	private Transition createTransition(QDomElement transition, FsmModule parent)
	{
		QDomElement prop = transition.firstChildElement("Properties");
		
		String condition = prop.attribute("condition");		
		State start_state = findState(parent, prop.attribute("start_state"));
		State end_state = findState(parent, prop.attribute("end_state"));
		
		assert (start_state != null && end_state != null);
		Transition t = new Transition(start_state);
		t.setCondition(condition);
		t.setEnd_state(end_state);		
		return t;
	}
	
	private State findState(FsmModule parent, String name)
	{
		Iterator<State> it = parent.getStates().iterator();
		
		while (it.hasNext())
		{
			State s = it.next();
			
			if (s.getName().compareTo(name) == 0)
				
				return s;
		}
		return null;
	}
	
	private void createViews()
	{
		main_win.new_model_created.emit(true);
		main_win.getView().setEnabled(true);
		
		new FrameModuleTreeView(main_win.getModule(), main_win.getProjectTree());
		main_win.getModule().added.emit();
		
		createLocalVariableViews(main_win, main_win.getProjectTree());
		createFrameModuleInstanceViews(main_win);
		createFsmModuleInstanceViews(main_win);
		createSpecificationsView(main_win.getModule());
		
		createNotInstancedModulesView(main_win.getModule().moduleList());
	}
	
	private void createNotInstancedModulesView(ModulesList module_list)
	{
		for (int i = 0; i < module_list.size(); i++)
		{
			Module m = module_list.getModule(i);
			
			if (m.getClass().getName().compareTo("model.FrameModule") == 0)
			{
				createFrameModuleViews((FrameModule)m, null, main_win.getProjectTree());
			}
			else
			{
				createFsmModuleViews((FsmModule)m, null, main_win.getProjectTree());
			}
		}
	}
	
	private void createFrameModuleViews(FrameModule module, FrameModuleInstanceGraphicView instance_view, TreeWidget project_tree)
	{
		if (!created_modules.contains(module))
		{
			FrameModuleWindowView m_view = new FrameModuleWindowView(module, project_tree);
			new FrameModuleTreeView(module, project_tree);
			
			created_modules.add(module);
			module.added.emit();
			
			createLocalVariableViews(m_view, project_tree);
			createOutputVariableViews(m_view, instance_view, project_tree);
			createInputVariableViews(m_view, instance_view, project_tree);
			createFrameModuleInstanceViews(m_view);
			createFsmModuleInstanceViews(m_view);
			createSpecificationsView(module);
		}
		else if (instance_view != null)
		{
			createOnlyHookOutView(instance_view, project_tree);
			createOnlyHookInView(instance_view, project_tree);
		}
	}
	
	private void createFsmModuleViews(FsmModule module, FsmModuleInstanceGraphicView instance_view, TreeWidget project_tree)
	{
		if (!created_modules.contains(module))
		{
			FsmModuleWindowView m_view = new FsmModuleWindowView(module, project_tree);
			new FsmModuleTreeView(module, project_tree);
			
			created_modules.add(module);
			module.added.emit();
			
			createStateViews(m_view, project_tree);
			createTransitionViews(m_view);
			createLocalVariableViews(m_view, project_tree);
			createOutputVariableViews(m_view, instance_view, project_tree);
			createInputVariableViews(m_view, instance_view, project_tree);
		}
		else if (instance_view != null)
		{
			createOnlyHookOutView(instance_view, project_tree);
			createOnlyHookInView(instance_view, project_tree);
		}
	}
	
	private void createFrameModuleInstanceViews(FrameModuleWindowView parent_view)
	{
		Iterator<FrameModuleInstance> it = parent_view.getModule().getFrameModuleInstances().iterator();
		
		while (it.hasNext())
		{
			FrameModuleInstance instance = it.next();
			QDomNodeList modules = dom.elementsByTagName("FrameInstanceVariable");
			
			QDomElement e = null;
			
			for (int i = 0; i < modules.length(); i++)
			{
			    QDomElement m = modules.at(i).toElement().firstChildElement("Properties").toElement();
			    if (m.attribute("instance_name").compareTo(instance.getName()) == 0 && m.attribute("module_name").compareTo(instance.getInstancedModule().getName()) == 0)
				{
				    e = modules.at(i).toElement();
				    i = modules.length();
				}
			}
			assert (e != null);
			
			QDomElement layout = e.firstChildElement("Layout");
			int x = Integer.valueOf(layout.attribute("x"));
			int y = Integer.valueOf(layout.attribute("y"));
			int width = Integer.valueOf(layout.attribute("width"));
			int height = Integer.valueOf(layout.attribute("height"));
			
			FrameModuleInstanceGraphicView instance_gview = new FrameModuleInstanceGraphicView(instance, x, y, parent_view.getView(), parent_view.getProjectTree());
			instance_gview.setWidth(width);
			instance_gview.setHeight(height);
			instance_gview.resized.emit();
			
			if (instance.isProcess())
			{
				instance_gview.processActivated(true);
			}
			
			new FrameModuleInstanceTreeView(instance, parent_view.getProjectTree(), parent_view.getModule().getName(), instance_gview.getMenu());
			
			createFrameModuleViews((FrameModule)instance.getInstancedModule(), instance_gview, parent_view.getProjectTree());
			
			instance.added.emit();
		}
	}
	
	private void createFsmModuleInstanceViews(FrameModuleWindowView parent_view)
	{
		Iterator<FsmModuleInstance> it = parent_view.getModule().getFsmModuleInstances().iterator();
		
		while (it.hasNext())
		{
			FsmModuleInstance instance = it.next();
			QDomNodeList modules = dom.elementsByTagName("FsmInstanceVariable");
			
			QDomElement e = null;
			for (int i = 0; i < modules.length(); i++)
			{
			    QDomElement m = modules.at(i).toElement().firstChildElement("Properties").toElement();
			    if (m.attribute("instance_name").compareTo(instance.getName()) == 0 && m.attribute("module_name").compareTo(instance.getInstancedModule().getName()) == 0)
				{
				    e = modules.at(i).toElement();
				    i = modules.length();
				}
			}
			assert (e != null);
			
			QDomElement layout = e.firstChildElement("Layout");
			int x = Integer.valueOf(layout.attribute("x"));
			int y = Integer.valueOf(layout.attribute("y"));
			int width = Integer.valueOf(layout.attribute("width"));
			int height = Integer.valueOf(layout.attribute("height"));
			
			FsmModuleInstanceGraphicView instance_gview = new FsmModuleInstanceGraphicView(instance, x, y, parent_view.getView(), parent_view.getProjectTree());
			instance_gview.setWidth(width);
			instance_gview.setHeight(height);
			instance_gview.resized.emit();
			
			if (instance.isProcess())
			{
				instance_gview.processActivated(true);
			}
			
			new FsmModuleInstanceTreeView(instance, parent_view.getProjectTree(), parent_view.getModule().getName(), instance_gview.getMenu());
			
			createFsmModuleViews((FsmModule)instance.getInstancedModule(), instance_gview, parent_view.getProjectTree());
			
			instance.added.emit();
		}
	}
	
	private void createLocalVariableViews(ModuleWindowView parent_view, TreeWidget project_tree)
	{
		Iterator<LocalVariable> it = parent_view.getModule().getLocal_variables().iterator();
		
		while (it.hasNext())
		{
			LocalVariable lv = it.next();
			
			if (parent_view.getClass().getName().compareTo("view.FsmModuleWindowView") != 0)
			{
				QDomNodeList modules = dom.firstChildElement().childNodes();
				QDomElement local_var = null;
				
				for (int i = 0; i < modules.length(); i++)
				{
					if (modules.at(i).toElement().attribute("name").compareTo(parent_view.getModule().getName()) == 0)
					{
						QDomNodeList vars = modules.at(i).toElement().elementsByTagName("LocalVariable");
						
						for (int j = 0; j < vars.length(); j++)
						{
							if (vars.at(j).toElement().firstChildElement("Properties").toElement().attribute("name").compareTo(lv.getName()) == 0)
							{
								local_var = vars.at(j).toElement();
								j = vars.length();
								i = modules.length();
							}
						}
					}
				}
				assert (local_var != null);
				
				QDomElement layout = local_var.firstChildElement("Layout");
				int x = Integer.valueOf(layout.attribute("x"));
				int y = Integer.valueOf(layout.attribute("y"));
				int width = Integer.valueOf(layout.attribute("width"));
				int height = Integer.valueOf(layout.attribute("height"));
				
				LocalVariableGraphicView lvgv = new LocalVariableGraphicView(lv, x, y, parent_view.getView());
				lvgv.setWidth(width);
				lvgv.setHeight(height);
				lvgv.resized.emit();
			}
			
			new LocalVariableTreeView(project_tree, lv);
			new LocalVariableDockView(parent_view.getLocalTree(), lv, null);
			
			lv.added.emit();
		}
	}
	
	private void createInputVariableViews(ModuleWindowView parent_view, ModuleInstanceGraphicView instance_parent_view, TreeWidget project_tree)
	{
		Iterator<InputVariable> it = parent_view.getModule().getInput_variables().iterator();
		
		while (it.hasNext())
		{
			InputVariable in = it.next();
			
			if (parent_view.getClass().getName().compareTo("view.FsmModuleWindowView") != 0)
			{
				QDomNodeList modules = dom.firstChildElement().childNodes();
				QDomElement input_var = null;
				
				for (int i = 0; i < modules.length(); i++)
				{
					if (modules.at(i).toElement().attribute("name").compareTo(parent_view.getModule().getName()) == 0)
					{
						QDomNodeList vars = modules.at(i).toElement().elementsByTagName("InputVariable");
						
						for (int j = 0; j < vars.length(); j++)
						{
							if (vars.at(j).toElement().firstChildElement("Properties").toElement().attribute("name").compareTo(in.getName()) == 0)
							{
								input_var = vars.at(j).toElement();
								j = vars.length();
								i = modules.length();
							}
						}
					}
				}
				assert (input_var != null);
				
				QDomElement layout = input_var.firstChildElement("Layout");
				int x = Integer.valueOf(layout.attribute("x"));
				int y = Integer.valueOf(layout.attribute("y"));
				int width = Integer.valueOf(layout.attribute("width"));
				int height = Integer.valueOf(layout.attribute("height"));
				
				InputVariableGraphicView ivgv = new InputVariableGraphicView(in, x, y, parent_view.getView());
				ivgv.setWidth(width);
				ivgv.setHeight(height);
				ivgv.resized.emit();
			}
			
			new InputVariableDockView(in, parent_view.getInputTree());
			
			if (instance_parent_view != null)
			{
				QDomNodeList inputs = dom.elementsByTagName("Input");
				QDomElement input = null;
				
				for (int i = 0; i < inputs.length(); i++)
				{
					QDomElement in_parent = inputs.at(i).parentNode().toElement();
					
					if (in_parent.attribute("instance_name").compareTo(instance_parent_view.getInstanceName()) == 0 && 
						inputs.item(i).toElement().firstChildElement("Variable").attribute("name").compareTo(in.getName()) == 0)
					{
						input = inputs.at(i).toElement();
						break;
					}
				}
				assert (input != null);
				
				QDomElement layout = input.firstChildElement("Layout");
				int x = Integer.valueOf(layout.attribute("x"));
				int y = Integer.valueOf(layout.attribute("y"));
				String orientation = layout.attribute("orientation");
				
				InputVariableHookView ivhv = new InputVariableHookView(instance_parent_view, in, x, y, instance_parent_view.getView(), orientation);
				input_to_connect.add(new InputConnectInfo(ivhv, input.firstChildElement("OutputInfo").toElement()));
			}
			
			new InputVariableTreeView(in, parent_view.getModule(), project_tree);
			in.added.emit();
		}
	}
	
	private void createOnlyHookInView(ModuleInstanceGraphicView instance_view, TreeWidget tree)
	{
		Iterator<InputVariable> it = instance_view.getInstance().getInstancedModule().getInput_variables().iterator();
		
		while (it.hasNext())
		{
			InputVariable in = it.next();
			
			QDomNodeList inputs = dom.elementsByTagName("Input");
			QDomElement input = null;
			
			for (int i = 0; i < inputs.length(); i++)
			{
				QDomElement in_parent = inputs.at(i).parentNode().toElement();
				
				if (in_parent.attribute("instance_name").compareTo(instance_view.getInstanceName()) == 0 && 
					inputs.item(i).toElement().firstChildElement("Variable").attribute("name").compareTo(in.getName()) == 0)
				{
					input = inputs.at(i).toElement();
					break;
				}
			}
			assert (input != null);
			
			QDomElement layout = input.firstChildElement("Layout");
			int x = Integer.valueOf(layout.attribute("x"));
			int y = Integer.valueOf(layout.attribute("y"));
			String orientation = layout.attribute("orientation");
			
			InputVariableHookView ivhv = new InputVariableHookView(instance_view, in, x, y, instance_view.getView(), orientation);
			input_to_connect.add(new InputConnectInfo(ivhv, input.firstChildElement("OutputInfo").toElement()));
		}
		
		
	}
	
	private void createOnlyHookOutView(ModuleInstanceGraphicView instance_view, TreeWidget tree)
	{
		Iterator<OutputVariable> it = instance_view.getInstance().getInstancedModule().getOutput_variables().iterator();
		
		while (it.hasNext())
		{
			OutputVariable out = it.next();
			
			QDomNodeList outputs = dom.elementsByTagName("Output");
			QDomElement output = null;
			
			for (int i = 0; i < outputs.length(); i++)
			{
				QDomElement out_parent = outputs.at(i).parentNode().toElement();
				
				if (out_parent.attribute("instance_name").compareTo(instance_view.getInstanceName()) == 0 && 
						outputs.item(i).toElement().firstChildElement("Variable").attribute("name").compareTo(out.getName()) == 0)
				{
					output = outputs.at(i).toElement();
					break;
				}
			}
			assert (output != null);
			
			QDomElement layout = output.firstChildElement("Layout");
			int x = Integer.valueOf(layout.attribute("x"));
			int y = Integer.valueOf(layout.attribute("y"));
			String orientation = layout.attribute("orientation");
			
			new OutputVariableHookView(instance_view, out, x, y, instance_view.getView(), orientation);
		}
		
		
	}
	
	private void createOutputVariableViews(ModuleWindowView parent_view, ModuleInstanceGraphicView instance_parent_view, TreeWidget project_tree)
	{
		Iterator<OutputVariable> it = parent_view.getModule().getOutput_variables().iterator();
		
		while (it.hasNext())
		{
			OutputVariable out = it.next();
			
			if (parent_view.getClass().getName().compareTo("view.FsmModuleWindowView") != 0)
			{
				QDomNodeList modules = dom.firstChildElement().childNodes();
				QDomElement output_var = null;
				
				for (int i = 0; i < modules.length(); i++)
				{
					if (modules.at(i).toElement().attribute("name").compareTo(parent_view.getModule().getName()) == 0)
					{
						QDomNodeList vars = modules.at(i).toElement().elementsByTagName("OutputVariable");
						
						for (int j = 0; j < vars.length(); j++)
						{
							if (vars.at(j).toElement().firstChildElement("Properties").toElement().attribute("name").compareTo(out.getName()) == 0)
							{
								output_var = vars.at(j).toElement();
								j = vars.length();
								i = modules.length();
							}
						}
					}
				}
				assert (output_var != null);
				
				QDomElement layout = output_var.firstChildElement("Layout");
				int x = Integer.valueOf(layout.attribute("x"));
				int y = Integer.valueOf(layout.attribute("y"));
				int width = Integer.valueOf(layout.attribute("width"));
				int height = Integer.valueOf(layout.attribute("height"));
				
				OutputVariableGraphicView ivgv = new OutputVariableGraphicView(out, x, y, parent_view.getView());
				ivgv.setWidth(width);
				ivgv.setHeight(height);
				ivgv.resized.emit();
			}
			
			new OutputVariableDockView(out, parent_view.getOutputTree());
			
			if (instance_parent_view != null)
			{
				QDomNodeList outputs = dom.elementsByTagName("Output");
				QDomElement output = null;
				
				for (int i = 0; i < outputs.length(); i++)
				{
					QDomElement out_parent = outputs.at(i).parentNode().toElement();
					
					if (out_parent.attribute("instance_name").compareTo(instance_parent_view.getInstanceName()) == 0 &&
						outputs.item(i).toElement().firstChildElement("Variable").attribute("name").compareTo(out.getName()) == 0)
					{
						output = outputs.at(i).toElement();
						break;
					}
				}
				assert (output != null);
				
				QDomElement layout = output.firstChildElement("Layout");
				int x = Integer.valueOf(layout.attribute("x"));
				int y = Integer.valueOf(layout.attribute("y"));
				String orientation = layout.attribute("orientation");
				
				new OutputVariableHookView(instance_parent_view, out, x, y, instance_parent_view.getView(), orientation);
			}
			
			new OutputVariableTreeView(out, parent_view.getModule(), project_tree);
			out.added.emit();
		}
	}
	
	private void connectInput()
	{
		Iterator<InputConnectInfo> it = input_to_connect.iterator();
		
		while (it.hasNext())
		{
			InputConnectInfo info = it.next();
			QDomElement output_info = info.getOutObject();
			InputVariableHookView in = info.getInputHookView();
			
			String from = output_info.attribute("from");
			String var_name = output_info.attribute("var_name");
			
			FrameModule m = ((ModuleInstanceGraphicView)in.getParent()).getInstance().getParentModule();
			
			if (from.compareTo("module") == 0)
			{
				connectInputToOutput(output_info, in, var_name, m);
			}
			else
			{
				connectInputToVar(output_info, in, var_name, m, from);
			}
		}
	}
	
	private void connectInputToOutput(QDomElement output_info, InputVariableHookView in, String var_name, FrameModule m)
	{
		ModuleInstance module = null; 
		Iterator<FrameModuleInstance> it1 = m.getFrameModuleInstances().iterator();
		
		while  (module == null && it1.hasNext())
		{
			FrameModuleInstance mod = it1.next();
			if (mod.getName().compareTo(output_info.attribute("instance_name")) == 0)
			{
				module = mod;
			}
		}
		
		Iterator<FsmModuleInstance> it2 = m.getFsmModuleInstances().iterator();
		
		while (module == null && it2.hasNext())
		{
			FsmModuleInstance mod = it2.next();
			if (mod.getName().compareTo(output_info.attribute("instance_name")) == 0)
			{
				module = mod;
			}
		}
		assert (module != null);
		
		Iterator<OutputVariable> out_it = module.getInstancedModule().getOutput_variables().iterator();
		
		while (out_it.hasNext())
		{
			OutputVariable out = out_it.next();
			
			if (out.getName().compareTo(var_name) == 0)
			{
				out.add_input_to_instance.emit(in, module);
			}
		}
	}
	
	private void connectInputToVar(QDomElement output_info, InputVariableHookView in, String var_name, FrameModule m, String from)
	{
		if (from.compareTo("local variable") == 0)
		{
			Iterator<LocalVariable> it1 = m.getLocal_variables().iterator();
			
			while  (it1.hasNext())
			{
				LocalVariable lv = it1.next();
				if (lv.getName().compareTo(var_name) == 0)
				{
					lv.connect_to_input.emit(in);
					return;
				}
			}
		}
		else if (from.compareTo("input variable") == 0)
		{
			Iterator<InputVariable> it2 = m.getInput_variables().iterator();
			
			while  (it2.hasNext())
			{
				InputVariable iv = it2.next();
				if (iv.getName().compareTo(output_info.attribute(var_name)) == 0)
				{
					iv.connect_to_input.emit(in);
					return;
				}
			}
		}
		else
		{
			Iterator<OutputVariable> it3 = m.getOutput_variables().iterator();
			
			while  (it3.hasNext())
			{
				OutputVariable ov = it3.next();
				if (ov.getName().compareTo(output_info.attribute(var_name)) == 0)
				{
					ov.connect_to_input.emit(in);
					return;
				}
			}
		}
	}
	
	private void createStateViews(FsmModuleWindowView parent_view, TreeWidget project_tree)
	{
		QDomNodeList fsm_list = dom.elementsByTagName("FSM");
		QDomElement fsm = null;
		for (int i = 0; i < fsm_list.length(); i++)
		{
			if ((fsm = fsm_list.item(i).toElement()).attribute("name").compareTo(parent_view.getModule().getName()) == 0)
			{
				break;
			}
		}
		
		Iterator<State> it = parent_view.getModule().getStates().iterator();
		
		while (it.hasNext())
		{
			State s = it.next();
			
			QDomNodeList states = fsm.elementsByTagName("State");
			
			for (int i = 0; i < states.length(); i++)
			{
				if (states.item(i).firstChildElement("Properties").attribute("name").compareTo(s.getName()) == 0)
				{
					QDomElement layout = states.item(i).firstChildElement("Layout");
					
					int x = Integer.valueOf(layout.attribute("x"));
					int y = Integer.valueOf(layout.attribute("y"));
					int w = Integer.valueOf(layout.attribute("width"));
					int h = Integer.valueOf(layout.attribute("height"));
					
					StateGraphicView sgv = new StateGraphicView(s, x, y,w,h, parent_view.getView());
					sgv.resized.emit();
					sgv.state_clicked.connect(parent_view, "stateClicked(StateGraphicView, int, int, LinePosition)");
					parent_view.getTransitionButton().toggled.connect(sgv, "drawingTransition(boolean)");
					new StateTreeView(s, project_tree, sgv.getMenu());
					s.added.emit();
				}
			}
		}		
	}
	
	private void createTransitionViews(FsmModuleWindowView fsm_view)
	{
		Iterator<State> it1 = fsm_view.getModule().getStates().iterator();
		
		while (it1.hasNext())
		{
			State ss = it1.next();
			
			Iterator<Transition> it2 = ss.getExiting_transitions().iterator();
			
			while (it2.hasNext())
			{
				Transition t = it2.next();
				State es = t.getEnd_state();
				
				QDomNodeList fsm_list = dom.elementsByTagName("FSM");
				QDomElement fsm = null;
				for (int i = 0; i < fsm_list.size(); i++)
				{
					if (fsm_list.item(i).toElement().attribute("name").compareTo(fsm_view.getModule().getName()) == 0)
					{
						fsm = fsm_list.item(i).toElement();
						break;
					}
				}
				assert fsm != null;
				
				QDomNodeList transitions = fsm.elementsByTagName("Transition");
				QDomElement transition = null;
				
				for (int i = 0; i < transitions.length(); i++)
				{
					QDomElement e = transitions.item(i).toElement().firstChildElement("Properties");
					if (e.attribute("start_state").compareTo(ss.getName()) == 0 &&
						e.attribute("end_state").compareTo(es.getName()) == 0)
					{
						transition = transitions.item(i).toElement();
						break;
					}
				}
				assert (transition != null);
				
				QDomElement layout = transition.firstChildElement("Layout").toElement();
				
				int sx = Integer.valueOf(layout.attribute("sx"));
				int sy = Integer.valueOf(layout.attribute("sy"));
				int ex = Integer.valueOf(layout.attribute("ex"));
				int ey = Integer.valueOf(layout.attribute("ey"));
				int c1x = Integer.valueOf(layout.attribute("c1x"));
				int c1y = Integer.valueOf(layout.attribute("c1y"));
				int c2x = Integer.valueOf(layout.attribute("c2x"));
				int c2y = Integer.valueOf(layout.attribute("c2y"));
				LinePosition start_pos = LinePosition.valueOf(layout.attribute("start_pos"));
				LinePosition end_pos = LinePosition.valueOf(layout.attribute("end_pos"));
				
				TransitionGraphicView tgv = new TransitionGraphicView(t, sx, sy, ex, ey, c1x, c1y, c2x, c2y, 
											start_pos, end_pos, fsm_view.getView());
				ss.added_exit_transition.emit(tgv);
				es.added_entry_transition.emit(tgv);
			}
		}
	}
	
	private void createSpecificationsView(FrameModule module)
	{
		Iterator<Specification> it = module.getFormulas().iterator();
		
		while (it.hasNext())
		{
			Specification s = it.next();
			
			module.specification_added.emit(s);
		}
	}
	
	private Counter getAvailableVarIndexList(QDomElement module_element)
	{
		QDomNodeList node_list = module_element.elementsByTagName("VariableIndex");
		List<Integer> index_list = new ArrayList<Integer>(0);
		
		for (int i = 0; i < node_list.length(); i++)
		{
			index_list.add(Integer.valueOf(node_list.item(i).toElement().attribute("index")));
		}
		
		QDomElement count = module_element.firstChildElement("VariableCount");
		
		return new Counter(index_list, Integer.valueOf(count.attribute("count")).intValue());
	}
	
	private Counter getAvailableOutputIndexList(QDomElement module_element)
	{
		QDomNodeList node_list = module_element.elementsByTagName("OutputIndex");
		List<Integer> index_list = new ArrayList<Integer>(0);
		
		for (int i = 0; i < node_list.length(); i++)
		{
			index_list.add(Integer.valueOf(node_list.item(i).toElement().attribute("index")));
		}
		
		QDomElement count = module_element.firstChildElement("OutputCount");
		
		return new Counter(index_list, Integer.valueOf(count.attribute("count")).intValue());
	}
	
	private Counter getAvailableInputIndexList(QDomElement module_element)
	{
		QDomNodeList node_list = module_element.elementsByTagName("inputIndex");
		List<Integer> index_list = new ArrayList<Integer>(0);
		
		for (int i = 0; i < node_list.length(); i++)
		{
			index_list.add(Integer.valueOf(node_list.item(i).toElement().attribute("index")));
		}
		
		QDomElement count = module_element.firstChildElement("InputCount");
		
		return new Counter(index_list, Integer.valueOf(count.attribute("count")).intValue());
	}
}
