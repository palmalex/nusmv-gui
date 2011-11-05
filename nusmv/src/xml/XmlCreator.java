package xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.FrameModule;
import model.FrameModuleInstance;
import model.FsmModule;
import model.FsmModuleInstance;
import model.InputVariable;
import model.LocalVariable;
import model.Module;
import model.ModuleInstance;
import model.OutputVariable;
import model.Specification;
import model.State;
import model.Transition;
import view.InputVariableGraphicView;
import view.InputVariableHookView;
import view.LocalVariableGraphicView;
import view.ModuleInstanceGraphicView;
import view.OutputVariableGraphicView;
import view.OutputVariableHookView;
import view.StateGraphicView;
import view.TransitionGraphicView;

import com.trolltech.qt.xml.QDomDocument;
import com.trolltech.qt.xml.QDomElement;

public class XmlCreator
{
	private FrameModule main;
	private String model_path;
	private QDomElement root;
	private QDomDocument dom;
	private List<Object> view_objects;
	
	public XmlCreator(FrameModule main, String model_path, String model_name)
	{
		this.main = main;
		this.model_path = model_path;
		this.root = new QDomElement();
		this.dom = new QDomDocument();
		this.view_objects = new ArrayList<Object>(0);
		
		createDomTree(model_name);
		printXmlFile();
	}
	
	private void createDomTree(String model_name)
	{
		root = dom.createElement(model_name);
		dom.appendChild(root);

		appendFrameModule(main);
		
		for (int i = 0; i < main.moduleList().size(); i++)
		{
			Module m = main.moduleList().getModule(i);
			
			if (m.getClass().getName().compareTo("model.FrameModule") == 0)
			{
				appendFrameModule((FrameModule)m);
			}
			else
			{
				appendFsmModule((FsmModule)m);
			}
		}
		QDomElement mod_counter = dom.createElement("ModuleCounter");
		mod_counter.setAttribute("start", main.moduleList().getStartModuleIndex());
		Iterator<Integer> it = main.moduleList().getAvailbleIndexList().iterator();
		
		while (it.hasNext())
		{
			QDomElement counter = dom.createElement("Count");
			counter.setAttribute("index", it.next().toString());
			mod_counter.appendChild(counter);
		}
		root.appendChild(mod_counter);
	}
	
	private void appendFrameModule(FrameModule m)
	{
		QDomElement mod_element = dom.createElement("Module");
		mod_element.setAttribute("name", m.getName());
		mod_element.setAttribute("index", m.getIndex());
		
		Iterator<String> it = m.getFairnessFormula().iterator();
		while (it.hasNext())
		{
			QDomElement fairness = dom.createElement("Fairness");
			fairness.setAttribute("constraint", it.next());
			mod_element.appendChild(fairness);
		}
		
		//appende tutti i frame module contenuti
		Iterator<FrameModuleInstance> it1 = m.getFrameModuleInstances().iterator();
		while (it1.hasNext())
		{
			FrameModuleInstance ist = it1.next();
			appendInstanceVariable(ist, mod_element, "FrameInstanceVariable");
		}
		
		//appende tutti i fsm module contenuti
		Iterator<FsmModuleInstance> it2 = m.getFsmModuleInstances().iterator();
		while (it2.hasNext())
		{
			FsmModuleInstance ist = it2.next();
			appendInstanceVariable(ist, mod_element, "FsmInstanceVariable");
		}
		
		//appende tutte le variabili locali
		Iterator<LocalVariable> it3 = m.getLocal_variables().iterator();
		while (it3.hasNext())
		{
			LocalVariable v = it3.next();
			appendLocalVariable(v, mod_element);
		}
					
		//appende tutte le variabili di ingresso
		Iterator<InputVariable> it4 = m.getInput_variables().iterator();
		while (it4.hasNext())
		{
			appendInputVariable(it4.next(), mod_element);
		}
		
		//appende tutte le variabili di uscita
		Iterator<OutputVariable> it5 = m.getOutput_variables().iterator();
		while (it5.hasNext())
		{
			appendOutputVariable(it5.next(), mod_element);
		}
			
		Iterator<Specification> it6 = m.getFormulas().iterator();
		while (it6.hasNext())
		{
			appendSpecification(it6.next(), mod_element);
		}
		
		appendVariableIndexInfo(mod_element, m);
		appendInputIndexInfo(mod_element, m);
		appendOutputIndexInfo(mod_element, m);
		
		root.appendChild(mod_element);
	}
	
	private void appendFsmModule(FsmModule m)
	{
		QDomElement fsm_element = dom.createElement("FSM");
		fsm_element.setAttribute("name", m.getName());
		fsm_element.setAttribute("index", m.getIndex());
		
		Iterator<String> it = m.getFairnessFormula().iterator();
		while (it.hasNext())
		{
			QDomElement fairness = dom.createElement("Fairness");
			fairness.setAttribute("constraint", it.next());
			fsm_element.appendChild(fairness);
		}
		
		//appende gli stati
		Iterator<State> it1 = m.getStates().iterator();
		while (it1.hasNext())
		{
			State s = it1.next();
			appendState(s, fsm_element);
			
			//appende le transizioni
			Iterator<Transition> it2 = s.getExiting_transitions().iterator();
			while (it2.hasNext())
			{
				Transition t = it2.next();
				appendTransition(s, t, fsm_element);
			}
			appendNonDetTransitions(s, fsm_element);
		}
					//appende le variabili locali
		Iterator<LocalVariable> it3 = m.getLocal_variables().iterator();
		while (it3.hasNext())
		{
			appendLocalVariable(it3.next(), fsm_element);
		}
					
		//appende tutte le variabili di ingresso
		Iterator<InputVariable> it4 = m.getInput_variables().iterator();
		while (it4.hasNext())
		{
			appendInputVariable(it4.next(), fsm_element);
		}
		
		//appende tutte le variabili di uscita
		Iterator<OutputVariable> it5 = m.getOutput_variables().iterator();
		while (it5.hasNext())
		{
			appendOutputVariable(it5.next(), fsm_element);
		}
		
		appendVariableIndexInfo(fsm_element, m);
		appendInputIndexInfo(fsm_element, m);
		appendOutputIndexInfo(fsm_element, m);
			
		root.appendChild(fsm_element);
	}
	
	private void appendInstanceVariable(ModuleInstance instance, QDomElement parent, String tag_name)
	{
		QDomElement var = dom.createElement(tag_name);
		
		QDomElement properties = dom.createElement("Properties");
		
		properties.setAttribute("instance_name", instance.getName());
		properties.setAttribute("module_name", instance.getInstancedModule().getName());
		properties.setAttribute("process", ""+instance.isProcess());
		
		appendInputToInstance(instance, properties);
		appendOutputToInstance(instance, properties);
		
		instance.need_view_info.emit(this);
		ModuleInstanceGraphicView instance_view = (ModuleInstanceGraphicView)view_objects.get(0);
		view_objects.removeAll(view_objects);
		
		QDomElement layout = dom.createElement("Layout");
		layout.setAttribute("x", instance_view.getX() + instance_view.x());
		layout.setAttribute("y", instance_view.getY() + instance_view.y());
		layout.setAttribute("width", instance_view.width());
		layout.setAttribute("height", instance_view.height());
		
		var.appendChild(properties);
		var.appendChild(layout);
		
		parent.appendChild(var);
	}
	
	private void appendLocalVariable(LocalVariable lv, QDomElement parent)
	{
		QDomElement var = dom.createElement("LocalVariable");
		
		QDomElement prop = dom.createElement("Properties");
		prop.setAttribute("name", lv.getName());
		prop.setAttribute("type", lv.getType().toString());
		prop.setAttribute("values", lv.getValues());
		prop.setAttribute("initial", lv.getInitial_value());
		
		if (parent.tagName().compareTo("Module") == 0)
		{
			lv.need_view_info.emit(this);
			LocalVariableGraphicView lv_view = (LocalVariableGraphicView)view_objects.get(0);
			view_objects.removeAll(view_objects);
			
			QDomElement layout = dom.createElement("Layout");
			layout.setAttribute("x", lv_view.getX() + lv_view.x());
			layout.setAttribute("y", lv_view.getY() + lv_view.y());
			layout.setAttribute("width", lv_view.width());
			layout.setAttribute("height", lv_view.height());

			var.appendChild(layout);			
		}
		var.appendChild(prop);
		
		parent.appendChild(var);
	}
	
	private void appendInputVariable(InputVariable iv, QDomElement parent)
	{
		QDomElement var = dom.createElement("InputVariable");
		
		QDomElement prop = dom.createElement("Properties");
		prop.setAttribute("name", iv.getName());
		prop.setAttribute("type", ""+iv.getType());
		
		if (parent.tagName().compareTo("Module") == 0)
		{
			iv.need_view_info.emit(this);
			InputVariableGraphicView iv_view = (InputVariableGraphicView)view_objects.get(0);
			view_objects.removeAll(view_objects);
			
			QDomElement layout = dom.createElement("Layout");
			layout.setAttribute("x", iv_view.getX() + iv_view.x());
			layout.setAttribute("y", iv_view.getY() + iv_view.y());
			layout.setAttribute("width", iv_view.width());
			layout.setAttribute("height", iv_view.height());
		
			var.appendChild(layout);
		}
		var.appendChild(prop);
		
		parent.appendChild(var);
	}
	
	private void appendOutputVariable(OutputVariable ov, QDomElement parent)
	{
		QDomElement var = dom.createElement("OutputVariable");
		
		QDomElement prop = dom.createElement("Properties");
		prop.setAttribute("name", ov.getName());
		prop.setAttribute("type", ov.getType().toString());
		prop.setAttribute("values", ov.getValues());
		prop.setAttribute("initial", ov.getInitial_value());
		
		if (parent.tagName().compareTo("Module") == 0)
		{
			ov.need_view_info.emit(this);
			OutputVariableGraphicView ov_view = (OutputVariableGraphicView)view_objects.get(0);
			view_objects.removeAll(view_objects);
			
			QDomElement layout = dom.createElement("Layout");
			layout.setAttribute("x", ov_view.getX() + ov_view.x());
			layout.setAttribute("y", ov_view.getY() + ov_view.y());
			layout.setAttribute("width", ov_view.width());
			layout.setAttribute("height", ov_view.height());

			var.appendChild(layout);
		}		
		var.appendChild(prop);
		
		parent.appendChild(var);
	}
	
	private void appendInputToInstance(ModuleInstance m, QDomElement parent)
	{
		Iterator<InputVariable> it = m.getInstancedModule().getInput_variables().iterator();
		while (it.hasNext())
		{
			InputVariable in = it.next();
			QDomElement input = dom.createElement("Input");
			
			QDomElement input_var = dom.createElement("Variable");
			input_var.setAttribute("name", in.getName());
			input_var.setAttribute("type", ""+in.getType());
			input_var.setAttribute("values", in.getValues());
			input_var.setAttribute("initial", in.getInitial_value());
			
			in.need_hook_view_info.emit(this);
			
			Iterator<Object> it2 = view_objects.iterator();
			
			while (it2.hasNext())
			{
				InputVariableHookView in_view = (InputVariableHookView)it2.next();
				
				ModuleInstanceGraphicView in_parent = (ModuleInstanceGraphicView)in_view.getParent();
				
				if (in_parent.getInstanceName().compareTo(m.getName()) == 0)
				{
					QDomElement input_layout = dom.createElement("Layout");
					input_layout.setAttribute("x", in_view.getX() + in_parent.x());
					input_layout.setAttribute("y", in_view.getY() + in_parent.y());
					input_layout.setAttribute("width", in_view.width());
					input_layout.setAttribute("height", in_view.height());
					input_layout.setAttribute("orientation", in_view.getOrientation());
					
					QDomElement input_from = dom.createElement("OutputInfo");
					
					appendOutputInfo(in_view, input_from);
					
					input.appendChild(input_var);
					input.appendChild(input_layout);
					input.appendChild(input_from);
					
					parent.appendChild(input);
					
					break;
				}				
			}
			view_objects.removeAll(view_objects);
		}
	}
	
	private void appendOutputInfo(InputVariableHookView in, QDomElement input_from)
	{
		if (in.getEnteringLine() != null)
		{
			Object start = in.getEnteringLine().getStartObject();
			if (start.getClass().getName().compareTo("view.OutputVariableHookView") == 0)
			{
				input_from.setAttribute("from", "module");
				input_from.setAttribute("instance_name", ((ModuleInstanceGraphicView)((OutputVariableHookView)start).getParent()).getInstanceName());
				input_from.setAttribute("var_name", ((OutputVariableHookView)start).getOutputVariable().getName());
			}
			else if (start.getClass().getName().compareTo("view.LocalVariableGraphicView") == 0)
			{
				input_from.setAttribute("from", "local variable");
				input_from.setAttribute("var_name", ((LocalVariableGraphicView)start).getName());
			}
			else if (start.getClass().getName().compareTo("view.InputVariableGraphicView") == 0)
			{
				input_from.setAttribute("from", "input variable");
				input_from.setAttribute("var_name", ((InputVariableGraphicView)start).getInputVariable().getName());
			}
			else if (start.getClass().getName().compareTo("view.OutputVariableGraphicView") == 0)
			{
				input_from.setAttribute("from", "output variable");
				input_from.setAttribute("var_name", ((OutputVariableGraphicView)start).getOutputVariable().getName());
			}
		}
	}
	
	private void appendOutputToInstance(ModuleInstance m, QDomElement parent)
	{
		Iterator<OutputVariable> it = m.getInstancedModule().getOutput_variables().iterator();
		while (it.hasNext())
		{
			OutputVariable out = it.next();
			QDomElement output = dom.createElement("Output");
			
			QDomElement output_var = dom.createElement("Variable");
			output_var.setAttribute("name", out.getName());
			output_var.setAttribute("type", out.getType().toString());
			output_var.setAttribute("values", out.getValues());
			output_var.setAttribute("initial", out.getInitial_value());
			
			out.need_hook_view_info.emit(this);
			
			Iterator<Object> it2 = view_objects.iterator();
			
			while (it2.hasNext())
			{
				OutputVariableHookView out_view = (OutputVariableHookView)it2.next();
				
				ModuleInstanceGraphicView out_parent = (ModuleInstanceGraphicView)out_view.getParent();
				
				if (out_parent.getInstanceName().compareTo(m.getName()) == 0)
				{
					QDomElement output_layout = dom.createElement("Layout");
					output_layout.setAttribute("x", out_view.getX() + out_parent.x());
					output_layout.setAttribute("y", out_view.getY() + out_parent.y());
					output_layout.setAttribute("width", out_view.width());
					output_layout.setAttribute("height", out_view.height());
					output_layout.setAttribute("orientation", out_view.getOrientation());
					
					output.appendChild(output_var);
					output.appendChild(output_layout);
					
					parent.appendChild(output);
				}
			}
		}
		view_objects.removeAll(view_objects);
	}
	
	private void appendState(State s, QDomElement parent)
	{
		QDomElement state = dom.createElement("State");
		
		QDomElement prop = dom.createElement("Properties");
		prop.setAttribute("name", s.getName());
		prop.setAttribute("initial", ""+s.isInitial());
		appendActions(s, prop);
		
		s.need_view_info.emit(this);
		StateGraphicView s_view = (StateGraphicView)view_objects.get(0);
		view_objects.removeAll(view_objects);
		
		QDomElement layout = dom.createElement("Layout");
		layout.setAttribute("x", s_view.getX() + s_view.x());
		layout.setAttribute("y", s_view.getY() + s_view.y());
		layout.setAttribute("width", s_view.width());
		layout.setAttribute("height", s_view.height());
		
		state.appendChild(prop);
		state.appendChild(layout);
		
		parent.appendChild(state);
	}
	
	private void appendActions(State s, QDomElement parent)
	{		
		Iterator<String> it = s.getOnentry().iterator();
		while (it.hasNext())
		{
			QDomElement onentry = dom.createElement("Onentry");
			onentry.setAttribute("action", it.next());
			
			parent.appendChild(onentry);
		}
		
		it = s.getDuring().iterator();
		while (it.hasNext())
		{
			QDomElement during = dom.createElement("During");
			during.setAttribute("action", it.next());
			
			parent.appendChild(during);
		}
		
		it = s.getOnexit().iterator();
		while (it.hasNext())
		{
			QDomElement onexit = dom.createElement("Onexit");
			onexit.setAttribute("action", it.next());
			
			parent.appendChild(onexit);
		}
	}
	
	private void appendActivatedActions(QDomElement transition, State start_state, State end_state)
	{
		Iterator<String> it = start_state.getOnexit().iterator();
		
		while (it.hasNext())
		{
			QDomElement action = dom.createElement("Action");
			action.setAttribute("string", it.next());
			transition.appendChild(action);		
		}
		
		it = end_state.getOnentry().iterator();
		
		while (it.hasNext())
		{
			QDomElement action = dom.createElement("Action");
			action.setAttribute("string", it.next());
			transition.appendChild(action);	
		}
	}
	
	private void appendTransition(State s, Transition t, QDomElement parent)
	{
		QDomElement transition = dom.createElement("Transition");
		
		QDomElement prop = dom.createElement("Properties");
		prop.setAttribute("start_state", t.getStart_state().getName());
		prop.setAttribute("end_state", t.getEnd_state().getName());
		prop.setAttribute("condition", t.getCondition());
		
		t.need_view_info.emit(this);
		TransitionGraphicView t_view = (TransitionGraphicView)view_objects.get(0);
		view_objects.removeAll(view_objects);
		
		QDomElement layout = dom.createElement("Layout");
		layout.setAttribute("sx", t_view.getStartPoint().x());
		layout.setAttribute("sy", t_view.getStartPoint().y());
		layout.setAttribute("ex", t_view.getEndPoint().x());
		layout.setAttribute("ey", t_view.getEndPoint().y());
		layout.setAttribute("c1x", t_view.getCtrl1().x());
		layout.setAttribute("c1y", t_view.getCtrl1().y());
		layout.setAttribute("c2x", t_view.getCtrl2().x());
		layout.setAttribute("c2y", t_view.getCtrl2().y());
		layout.setAttribute("start_pos", t_view.getStartPosition());
		layout.setAttribute("end_pos", t_view.getEndPosintion());
		
		appendActivatedActions(transition, t.getStart_state(), t.getEnd_state());
		
		transition.appendChild(prop);
		transition.appendChild(layout);
		
		parent.appendChild(transition);
	}
	
	private void appendNonDetTransitions(State s, QDomElement parent)
	{
		Iterator<Transition> it = s.getExiting_transitions().iterator();
		List<State> end_states = new ArrayList<State>(0);
		
		while (it.hasNext())
		{
			Transition t = it.next();
			if (t.getCondition().compareTo("") == 0)
			{
				end_states.add(t.getEnd_state());
			}
		}
		if (end_states.size() == 1)
		{
			QDomElement non_det_t = dom.createElement("NonDetTransition");
			non_det_t.setAttribute("start_state", s.getName());
			non_det_t.setAttribute("condition", "1");
			non_det_t.setAttribute("end_state", end_states.get(0).getName());
			
			appendActivatedActions(non_det_t, s, end_states.get(0));
			
			parent.appendChild(non_det_t);
		}
		else if (!end_states.isEmpty())
		{
			final String var_name = "non_det_choice_";
			
			Iterator<State> ee = end_states.iterator();
			String negate_cond = getDetNegateConditions(s);
			while (ee.hasNext())
			{
				State end = ee.next();
				QDomElement non_det_t = dom.createElement("NonDetTransition");
				non_det_t.setAttribute("start_state", s.getName());
				non_det_t.setAttribute("condition", var_name + s.getName() + " = " + end.getName() + negate_cond);
				non_det_t.setAttribute("end_state", end.getName());
				
				appendActivatedActions(non_det_t, s, end);
				
				parent.appendChild(non_det_t);
			}
			appendNonDetChoiceVars(parent, s, end_states, negate_cond);
		}
	}
	
	private void appendNonDetChoiceVars(QDomElement parent, State s, List<State> end_states, String negate_cond)
	{
		QDomElement choice_var = dom.createElement("ChoiceVar");
		
		choice_var.setAttribute("name", "non_det_choice_" + s.getName());
		
		String values = "{" + end_states.get(0).getName();
		
		for (int i = 1; i < end_states.size(); i++)
		{
			values += ", " + end_states.get(i).getName();
		}
		values += "}";
		
		choice_var.setAttribute("values", values);
		
		if (values.contains(s.getName()))
		
			choice_var.setAttribute("initial", s.getName());
		
		else
		
			choice_var.setAttribute("initial", "");
		
		appendNoDetChoiceVarsNext(choice_var, s, negate_cond);
		parent.appendChild(choice_var);
	}
	
	//devo sapere quando entro nello stato per definire il next delle variabili non deterministiche.
	//mi serve negate_cond perché vado a scegliere la variabile non deterministica solo se non si
	//verifica una condizione deterministica di uscita dallo stato.
	private void appendNoDetChoiceVarsNext(QDomElement parent, State s, String negate_cond)
	{
		Iterator<Transition> it = s.getEntering_transitions().iterator();
		while (it.hasNext())
		{
			Transition t = it.next();

			QDomElement next = dom.createElement("Next");
			
			next.setAttribute("start_state", t.getStart_state().getName());
			
			//Se da uno stato ho una transizione condizionata che mi porta qui
			if (t.getCondition().compareTo("") != 0)
			{
				
				next.setAttribute("condition", t.getCondition() + negate_cond);
			}
			//Se da uno stato ho una transizione non condizionata che mi porta qui
			else
			{
				State start = t.getStart_state();
				int non_det_t_count = 0;
				
				Iterator<Transition> it2 = start.getExiting_transitions().iterator();
				while (it2.hasNext())
				{
					if (it2.next().getCondition().compareTo("") == 0)
					{
						non_det_t_count++;
					}
				}
				assert non_det_t_count >= 1;
				
				if (non_det_t_count == 1)
				{
					next.setAttribute("condition", "1" + negate_cond);
				}
				else
				{
					next.setAttribute("condition", "non_det_choice_" + t.getStart_state().getName() + " = " + s.getName() + negate_cond);
				}
			}
			next.setAttribute("end_state", s.getName());
			
			parent.appendChild(next);
		}	
	}
	
	private String getDetNegateConditions(State s)
	{
		Iterator<Transition> it = s.getExiting_transitions().iterator();
		List<String> det_conds = new ArrayList<String>(0);
		String negate_conds = "";
		
		while (it.hasNext())
		{
			Transition t = it.next();
			
			
			if (t.getCondition().compareTo("") != 0)
				
				det_conds.add(t.getCondition());
		}
		
		if (!det_conds.isEmpty())
		{			
			for (int i = 0; i < det_conds.size(); i++)
			
				negate_conds += " & !" + det_conds.get(i);
		}
		return negate_conds; 
	}
	
	private void appendSpecification(Specification f, QDomElement parent)
	{
		QDomElement formula = dom.createElement("Specification");
		
		formula.setAttribute("type", f.getType().toString());
		formula.setAttribute("formula", f.getFormula());
		
		parent.appendChild(formula);
	}
	
	public void setViewObject(Object obj)
	{
		view_objects.add(obj);
	}
	
	private void printXmlFile()
	{
		File file = new File(model_path);
		try 
	    {
	    	FileWriter out = new FileWriter(file);
	    	out.write(dom.toString());
	    	out.close();
	    }
	    catch(IOException e) {}
	}
	
	private void appendVariableIndexInfo(QDomElement parent, Module m)
	{
		Iterator<Integer> it = m.getVarAvailableIndex().iterator();
		
		while (it.hasNext())
		{
			QDomElement index = dom.createElement("VariableIndex");
			index.setAttribute("index", it.next());
			
			parent.appendChild(index);
		}
		
		QDomElement var_count = dom.createElement("VariableCount");
		var_count.setAttribute("count", m.getNextVarIndex());
		parent.appendChild(var_count);
	}
	
	private void appendInputIndexInfo(QDomElement parent, Module m)
	{
		Iterator<Integer> it = m.getInputAvailableIndex().iterator();
		
		while (it.hasNext())
		{
			QDomElement index = dom.createElement("InputIndex");
			index.setAttribute("index", it.next());
			
			parent.appendChild(index);
		}
		
		QDomElement in_count = dom.createElement("InputCount");
		in_count.setAttribute("count", m.getNextInputIndex());
		parent.appendChild(in_count);
	}
	
	private void appendOutputIndexInfo(QDomElement parent, Module m)
	{
		Iterator<Integer> it = m.getOutputAvailableIndex().iterator();
		
		while (it.hasNext())
		{
			QDomElement index = dom.createElement("OutputIndex");
			index.setAttribute("index", it.next());
			
			parent.appendChild(index);
		}
		
		QDomElement out_count = dom.createElement("OutputCount");
		out_count.setAttribute("count", m.getNextOutputIndex());
		parent.appendChild(out_count);
	}
}
