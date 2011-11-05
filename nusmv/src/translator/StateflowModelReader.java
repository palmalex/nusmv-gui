/********************************************************************************
 *                                                                               *
 *   Module      :   StateflowModelReader.java                                                *

 *   Author      :   Daniele Sbaraccani		       		     		            *
 *   Tools       :   Eclipse                                                     *
 ********************************************************************************/
package translator;

import item.LinePosition;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import model.FrameModule;
import model.FsmModule;
import model.FsmModuleInstance;
import model.InputVariable;
import model.LocalVariable;
import model.Module;
import model.ModuleInstance;
import model.ModulesList;
import model.OutputVariable;
import model.State;
import model.Transition;
import model.Type;
import view.FrameModuleTreeView;
import view.FrameModuleWindowView;
import view.FsmModuleInstanceGraphicView;
import view.FsmModuleInstanceTreeView;
import view.FsmModuleTreeView;
import view.FsmModuleWindowView;
import view.InputVariableDockView;
import view.InputVariableHookView;
import view.InputVariableTreeView;
import view.LocalVariableDockView;
import view.LocalVariableTreeView;
import view.ModuleInstanceGraphicView;
import view.ModuleWindowView;
import view.OutputVariableDockView;
import view.OutputVariableHookView;
import view.OutputVariableTreeView;
import view.StateGraphicView;
import view.StateTreeView;
import view.TransitionGraphicView;
import widget.TreeWidget;
import edu.tum.cs.commons.collections.UnmodifiableCollection;
import edu.tum.cs.commons.collections.UnmodifiableIterator;
import edu.tum.cs.commons.collections.UnmodifiableSet;
import edu.tum.cs.commons.logging.SimpleLogger;
import edu.tum.cs.simulink.builder.SimulinkModelBuilder;
import edu.tum.cs.simulink.builder.SimulinkModelBuildingException;
import edu.tum.cs.simulink.model.SimulinkBlock;
import edu.tum.cs.simulink.model.SimulinkLine;
import edu.tum.cs.simulink.model.stateflow.StateflowChart;
import edu.tum.cs.simulink.model.stateflow.StateflowData;
import edu.tum.cs.simulink.model.stateflow.StateflowEvent;
import edu.tum.cs.simulink.model.stateflow.StateflowMachine;
import edu.tum.cs.simulink.model.stateflow.StateflowNodeBase;
import edu.tum.cs.simulink.model.stateflow.StateflowState;
import edu.tum.cs.simulink.model.stateflow.StateflowTransition;

/**
 * Classe utilizzata per Conversione Stateflow-NuSMVGUI
 * 
 * @author Daniele Sbaraccani
 *
 * 
 */

public class StateflowModelReader {
	private FrameModuleWindowView main_win;
	private StateflowMachine stateflow;
	private ArrayList<FsmModified> Fsms = new ArrayList<FsmModified>();
	private List<InputVariableHookView> input_to_connect;
	private List<Module> created_modules;
	private Error error;

	/********************************************************************************
	 * * PUBLIC FUNCTIONS DEFINITION * *
	 ********************************************************************************/

	/**
	 * Costruttore. Costruisce modello stateflow dal modello mdl
	 * 
	 * @param path path del modello stateflow da caricare.
	 */
	public StateflowModelReader(String path, FrameModuleWindowView f)
			throws FileNotFoundException, SimulinkModelBuildingException {
		error = new Error();
		this.main_win = f;
		this.created_modules = new ArrayList<Module>(0);
		this.input_to_connect = new ArrayList<InputVariableHookView>(0);
		File file = new File(path);
		SimulinkModelBuilder builder = new SimulinkModelBuilder(file,new SimpleLogger());
		stateflow = builder.buildModel().getStateflowMachine(); 
		
	}

	/**
	 * Controllo modello e verifica se � ben formato.
	 * 
	 * @return true se il modello � ben formato. false se non lo �.
	 */

	public boolean checkForm() {


		if (!checkEvents(stateflow))
			error.setEvent(true);
		
		UnmodifiableCollection<StateflowChart> charts = stateflow.getCharts();
		UnmodifiableIterator<StateflowChart> chartsIterator = charts.iterator();
	
		while(chartsIterator.hasNext()) {
			StateflowChart chart = chartsIterator.next();

			UnmodifiableIterator<StateflowData> dataIterator=chart.getData().iterator();
			while(dataIterator.hasNext()){
				StateflowData data= dataIterator.next();
				
				if(!checkParameter(data,"scope","LOCAL_DATA")&&
						!checkParameter(data,"scope","INPUT_DATA")&&
						!checkParameter(data,"scope","OUTPUT_DATA")){
					error.setDataNoForm(true);
					error.addDataScopeIncorrect(data);
					
				}
				if(!checkParameter(data, "dataType","uint32")&&
						!checkParameter(data, "dataType","int32")&&
						!checkParameter(data, "dataType","boolean")){
					 error.setTypeNoform(true);
				}
				if(checkCastInteger(data)){
					error.addDataTypeConvert(data);
				}
				
			}
			if (!checkEvents(chart)) {
				error.setEvent(true);
				
			}
			if(!chart.getNodes().isEmpty())
				checkNode(chart.getNodes());
		}

		if (error.getResult()) {
			return true;
		}
		return false;
	}

	/**
	 * Restituisce La classe degli errori ottenuti
	 * 
	 */
	public Error getError() {
		return error;
	}
	
	/**
	 * Converte modello stateflow ben formato e mette a video il nuovo modello
	 * creato.
	 * 
	 */
	public void convert(String path) {
			createModuleMain(path);
	}
	

	
	/********************************************************************************
	*                                                                               *
	*  					       PRIVATE FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * 
	 */
	private boolean checkParameter(StateflowData data, String parameter, String string) {
		if (data.getParameter(parameter)==string){
			return true;		
			}
		return false;
	}
	
	private boolean checkCastInteger(StateflowData data) {
		String dataType=data.getParameter("dataType");
		if (dataType!= "boolean" && dataType!= "int32" && dataType!="uint32" || dataType=="uint16"){
			return true;		
			}
		return false;
	}

	private void checkNode(UnmodifiableSet<StateflowNodeBase> nodes){
		
	
		UnmodifiableIterator<StateflowNodeBase> nodeIterator = nodes.iterator();
		while (nodeIterator.hasNext()) {
			StateflowNodeBase node = nodeIterator.next();
			// controllo se chart contiene junction
			if (!checkType(node, "CONNECTIVE_JUNCTION")) {
				if(checkType((StateflowNodeBase) node.getParent(),"FUNC_STATE"))
					error.setJunction(true);
			}

			// controllo se chart contiene box
			if (!checkType(node, "GROUP_STATE")) {
				error.setBox(true);
			}

			// controllo se chart contiene funzioni
			if (!checkType(node, "FUNC_STATE")) {
				if(node.getParameter("simulink.isSimulinkFcn")=="1"){
					error.setSimulinkFunction(true);
					}else{
						if(node.getParameter("eml.isEML")=="1"){
							error.seteMfunction(true);
						}else{
							if(node.getParameter("truthTable.isTruthTable")=="1"){
								error.setTruthTable(true);
							}else{
								error.setFunction(true);
								error.addFunzioniIgnorate(node.getParameter("labelString"));
							}
						}
					}
			}
			
			if (node.getClass().getName().compareTo("edu.tum.cs.simulink.model.stateflow.StateflowState") == 0) {
				StateflowState state = (StateflowState) node;
				String label = state.getLabel();
				String[] sa = label.split("\\\\n");
				if(!state.getNodes().isEmpty()){
					error.addStateHierarchy(sa[0]);
				}
				for(int i=1;i<sa.length;i++){
					if (sa[i].contains("bind:") || sa[i].startsWith("on ")) {
						error.setActionNoForm(true);
					}

					if(state.getParameter("decomposition") == "SET_STATE"){
						error.setStateAnd(true);
					}
				}
				
				//controllo se state contiene transizioni a livelli diversi.

				if (!checkTransLevelIn(state.getInTransitions().iterator(), state)) {
					error.setTransitionLevel(true);
				}
				
				if(!checkTransition(state.getInTransitions().iterator())){
					error.setTransitionNoForm(true);
				}

				
				//controllo se state_padri contengono azioni
				if(!state.getNodes().isEmpty()) {
					
					if (state.getLabel().contains("entry:")|| state.getLabel().contains("during:")|| state.getLabel().contains("exit:")
							||state.getLabel().contains("en:")|| state.getLabel().contains("du:")|| state.getLabel().contains("ex:")) {
						error.setStateParentWithAction(true);
					}

					checkNode(state.getNodes());

				}
			}

		}
	}
	
	private boolean checkTransLevelIn(UnmodifiableIterator<StateflowTransition> transition, StateflowState state) {
		while(transition.hasNext()){
			StateflowTransition t= transition.next();
			if(t.getSrc()!=null){
				if (t.getSrc().getParent() != state.getParent()) {
					return false;
				}
			}
		}
		return true;
	}

	private boolean checkTransition(UnmodifiableIterator<StateflowTransition> transition) {
		while(transition.hasNext()){
			StateflowTransition t= transition.next();
			if(t.getLabel()!=null){
			if (!t.getLabel().startsWith("[")&&!t.getLabel().startsWith("/")&&!t.getLabel().startsWith("{")) {
				String condition=t.getLabel();
				String event="";
				event=condition;
				if(condition.contains("[")){
					event=condition.substring(0,condition.indexOf('['));
					}else{
						if(condition.contains("{")){
								if(condition.charAt(condition.indexOf('{')-1)!='/')
									event=condition.substring(0,condition.indexOf('{'));

						}else{
							if(condition.contains("/")){
								event=condition.substring(0,condition.indexOf('/'));

							}
							
						}
						
					}
					
					String[] events=event.split("\\|");
					for(int i=0; i<events.length;i++){
						boolean flag=false;
						for(int j=0; j<error.getEventConverted().size();j++){
							if(error.getEventConverted().get(j).getName()==events[i]){
								if(t.getDst().getInTransitions().size()==1){
									flag=true;

								}
							}
						}
						if(flag==false){
							error.setTransitionNoFormNotModificable(true);
						}
					}
						
						
				
				return false;
				
			}
			if(t.getLabel().contains("{")){
				if(t.getLabel().indexOf('{')!=0){
					if(t.getLabel().charAt(t.getLabel().indexOf('{')-1)!='/')
						error.setTransitionNoFormNotModificable(true);
				}else{
					error.setTransitionNoFormNotModificable(true);

				}

				return false;
			}
			if(t.getLabel().contains("/")){
				if(!(t.getDst().getInTransitions().size()==1)){
					error.setTransitionNoFormNotModificable(true);

				}
				return false;

			}
			}
		}

		return true;
	}


	private boolean checkEvents(StateflowMachine s) {
		if (!s.getEvents().isEmpty()) {

			return false;
		}
		return true;
	}

	private boolean checkEvents(StateflowChart c) {
		if (!c.getEvents().isEmpty()) {
			UnmodifiableIterator<StateflowEvent> eventIt=c.getEvents().iterator();
			StateflowEvent event=eventIt.next();
			if(event.getParameter("scope")=="LOCAL_EVENT"){
				error.addEventConverted(event);
			}
			return false;
		}
		return true;
	}

	private boolean checkType(StateflowNodeBase node, String type) {

		boolean flag = true;
			if (node.getParameter("type") == type) {
				flag = false;
			}
			return flag;

		}
	
	private void createModuleMain(String path) {
		FrameModule mod_main = main_win.getModule();
		main_win.setModelName(stateflow.getParameter("name"));
		main_win.setModelPath(path);
		main_win.createProjectTree();
		createModules(mod_main);
		createFrameModule(stateflow, mod_main);
		createViews();
		connectInput();
	}
	
	
	private void createModules(FrameModule main) {

		ModulesList ml = main.moduleList();
		UnmodifiableCollection<StateflowChart> fsm_modules = stateflow
				.getCharts();
		UnmodifiableIterator<StateflowChart> fsm_it = fsm_modules.iterator();

		for (int i = 0; i < fsm_modules.size(); i++) {
			StateflowChart e = fsm_it.next();
			String name = e.getName();
			FsmModule m = new FsmModule(name);
			ml.addFsmModule(m);
		}
		fsm_it = fsm_modules.iterator();
		for (int i = 0; i < fsm_modules.size(); i++) {
			createFsmModule(fsm_it.next(), (FsmModule) ml.getModule(i));
		}
	}

	private void createFsmModule(StateflowChart module, FsmModule parent) {

		ArrayList<StateflowData> local_vars = getData(module, "LOCAL_DATA");
		ArrayList<StateflowData> input_vars = getData(module, "INPUT_DATA");
		ArrayList<StateflowData> output_vars = getData(module, "OUTPUT_DATA");
		ArrayList<StateflowState> states = getStateLeaf(module.getNodes(),
				"", new ArrayList<StateflowTransition>(),
				new ArrayList<StateflowTransition>(),
				new ArrayList<StateflowState>());
		ArrayList<StateflowTransition> transitions = getTransition(states);
		if(!module.getEvents().isEmpty()){
			UnmodifiableIterator<StateflowEvent> events_it=module.getEvents().iterator();
			
			for(int i=0;i<module.getEvents().size();i++){
				StateflowEvent event= events_it.next();
				
				StateflowData data1= new StateflowData();
				data1.setParameter("name", event.getName());
				StateflowData data2= new StateflowData();
				data2.setParameter("name", event.getName()+"_old");
				
				data1.setParameter("dataType","boolean");
				data2.setParameter("dataType","boolean");
				
				
				data1.setParameter("props.initialValue","0");
				data2.setParameter("props.initialValue","0");

				
				//todo: eventi input e output?
				if(event.getParameter("scope")=="LOCAL_EVENT"){
				data1.setParameter("scope","LOCAL_DATA");
				data2.setParameter("scope","LOCAL_DATA");
				local_vars.add(data1);
				local_vars.add(data2);
				}
			}
		}
		FsmModified Fsm = new FsmModified(module, local_vars, input_vars,
				output_vars, states, transitions);
		Fsms.add(Fsm);

		addLocalVariable(parent, local_vars);
		addInputVariable(parent, input_vars);
		addOutputVariable(parent, output_vars);
		addState(parent, states);
		addTransition(parent, transitions);

	}

	private FrameModule createFrameModule(StateflowMachine stateflow,
			FrameModule parent) {

		UnmodifiableCollection<StateflowChart> fsm_modules = stateflow
				.getCharts();
		// ArrayList <StateflowData> local_vars = getData(model, "LOCAL_DATA");

		addFsmModuleInstance(parent, fsm_modules);
		// addLocalVariable(parent, local_vars);

		return parent;
	}

	private void addFsmModuleInstance(FrameModule parent,
			UnmodifiableCollection<StateflowChart> fsm_modules) {
		UnmodifiableIterator<StateflowChart> fsm_it = fsm_modules.iterator();
		int count=0;
		while (fsm_it.hasNext()) {
			count++;
			StateflowChart instance = fsm_it.next();
			FsmModuleInstance f_ist = createFsmModuleInstance(instance, parent,count);
			parent.addFsmModuleInstance(f_ist);
		}

	}

	private FsmModuleInstance createFsmModuleInstance(StateflowChart instance,
			FrameModule parent,int count) {

		String mod_name = instance.getName();
		boolean process = false;

		FsmModule fsm_mod = (FsmModule) parent.moduleList().getModule(mod_name);
		FsmModuleInstance mod = new FsmModuleInstance(parent, fsm_mod,
				main_win.getProjectTree());
		mod.setName(mod_name);
		mod.setProcess(process);

		return mod;
	}

	private void addLocalVariable(FsmModule parent,
			ArrayList<StateflowData> local_vars) {
		for (int i = 0; i < local_vars.size(); i++) {
			StateflowData var = local_vars.get(i);
			LocalVariable lv = createLocaleVariable(var, parent);
			parent.addLocalVariable(lv);
		}

	}

	private void addInputVariable(Module parent,
			ArrayList<StateflowData> input_vars) {
		for (int i = 0; i < input_vars.size(); i++) {
			StateflowData var = input_vars.get(i);
			InputVariable iv = createInputVariable(var, parent);
			parent.addInputVariable(iv);
		}
	}

	private void addOutputVariable(Module parent,
			ArrayList<StateflowData> output_vars) {
		for (int i = 0; i < output_vars.size(); i++) {
			StateflowData var = output_vars.get(i);
			OutputVariable ov = createOutputVariable(var, parent);
			parent.addOutputVariable(ov);
		}
	}

	private void addState(FsmModule parent, ArrayList<StateflowState> states) {
		for (int i = 0; i < states.size(); i++) {
			State s = createState(states.get(i), parent);
			parent.addState(s);
		}
	}

	private void addTransition(FsmModule parent,
			ArrayList<StateflowTransition> transitions) {
		for (int i = 0; i < transitions.size(); i++) {
			if (transitions.get(i).getSrc() != null) {
				createTransition(transitions.get(i), parent);
			}
		}
	}

	private LocalVariable createLocaleVariable(StateflowData var,
			FsmModule parent) {

		String name = var.getParameter("name");
		String values = var.getParameter("props.range.minimum")+".."+var.getParameter("props.range.maximum");
		String init = var.getParameter("props.initialValue");
		if(var.getParameter("props.range.minimum")==null||var.getParameter("props.range.maximum")==null){
			values="{0,1}";
		}
		if(init==null){
			init="0";
			
		}
		String type_text = covertType(var.getParameter("dataType"));
		Type type = null;

		if (type_text!=null) {
			type = Type.valueOf(type_text);
		}
		if(type==Type.valueOf("Boolean")){
			values="{0,1}";
		}
		LocalVariable lv = new LocalVariable(name, type, values, init, parent);

		return lv;
	}

	private InputVariable createInputVariable(StateflowData var, Module parent) {

		String name = var.getParameter("name");
		String values = var.getParameter("props.range.minimum")+".."+var.getParameter("props.range.maximum");
		String init = var.getParameter("props.initialValue");
		if(var.getParameter("props.range.minimum")==null||var.getParameter("props.range.maximum")==null){
			values="{0,1}";
		}
		if(init==null){
			init="0";
			
		}
		String type_text = covertType(var.getParameter("dataType"));
		Type type = null;

		if (type_text!=null) {
			type = Type.valueOf(type_text);
		}
		if(type==Type.valueOf("Boolean")){
			values="{0,1}";
		}

		InputVariable iv = new InputVariable(name, type, values, init, parent);

		return iv;
	}

	private OutputVariable createOutputVariable(StateflowData var, Module parent) {
		String name = var.getParameter("name");
		String values = var.getParameter("props.range.minimum")+".."+var.getParameter("props.range.maximum");
		String init = var.getParameter("props.initialValue");
		if(var.getParameter("props.range.minimum")==null||var.getParameter("props.range.maximum")==null){
			values="{0,1}";
		}
		if(init==null){
			init="0";
			
		}
		String type_text = covertType(var.getParameter("dataType"));
		Type type = null;

		if (type_text!=null) {
			type = Type.valueOf(type_text);
		}
		if(type==Type.valueOf("Boolean")){
			values="{0,1}";
		}

		OutputVariable ov = new OutputVariable(name, type, values, init, parent);

		return ov;
	}

	private State createState(StateflowState state, FsmModule parent) {
		String label = state.getLabel();
		label.replaceAll(" ", "");
		String[] sa = label.split("\\\\n");
		String name = sa[0];

		boolean initial = getInitial(state);

		State s = new State(parent, name);
		s.setInitial(initial);
		
		ArrayList<String> onentry_list = getString("entry", sa);
		if (onentry_list != null) {
			for (int i = 0; i < onentry_list.size(); i++) {
				
				s.addOnentry(onentry_list.get(i));
			}
		}
		ArrayList<String> during_list = getString("during", sa);
		if (!(during_list == null)) {
			for (int i = 0; i < during_list.size(); i++) {
				s.addDuring(during_list.get(i));

			}
		}
		ArrayList<String> onexit_list = getString("exit", sa);
		if (!(onexit_list == null)) {
			for (int i = 0; i < onexit_list.size(); i++) {
				s.addOnexit(onexit_list.get(i));

			}
		}
		return s;
	}

	private ArrayList<String> getString(String string, String[] sa) {
		
		boolean findFirst=false;
		ArrayList<String> action=new ArrayList<String>();
		
		String s1 = null;
		String s2 = null;
		String string2= null;
		if(string=="entry"){
			string2="en";
			s1="du";
			s2="ex";
		}
		if(string=="during"){
			string2="du";
			s1="en";
			s2="ex";
		}
		if(string=="exit"){
			string2="ex";
			s1="en";
			s2="du";
		}
		for (int i = 1; i < sa.length; i++) {
			if((!sa[i].startsWith("en:")&&!sa[i].startsWith("du:")&&!sa[i].startsWith("ex:")&&!sa[i].startsWith("entry:")&&!sa[i].startsWith("during:")&&!sa[i].startsWith("exit:"))&&findFirst){
				String[] tmp=sa[i].split(";");
				for (int t = 0; t < tmp.length; t++) {
					String[] tmp2=tmp[t].split(",");
					for (int t2 = 0; t2 < tmp2.length; t2++) {
						tmp2[t2].replace(" ", "");
						action.add(tmp2[t2]);
					}
				}
			}else{
				if(sa[i].startsWith(s1)||sa[i].startsWith(s2)&&findFirst){
					findFirst=false;
				}
			}
			if (sa[i].startsWith(string+ ":")) {
				findFirst=true;
				sa[i] = sa[i].replaceAll(string + ":", "");
				String[] tmp=sa[i].split(";");
				for (int t = 0; t < tmp.length; t++) {
					String[] tmp2=tmp[t].split(",");
					for (int t2 = 0; t2 < tmp2.length; t2++) {
						tmp2[t2].replace(" ", "");
						action.add(tmp2[t2]);
					}
				}

			}else{
			
			if (sa[i].startsWith(string2+ ":")) {
				findFirst=true;
				sa[i] = sa[i].replaceAll(string2 + ":", "");
				String[] tmp=sa[i].split(";");
				for (int t = 0; t < tmp.length; t++) {
					String[] tmp2=tmp[t].split(",");
					for (int t2 = 0; t2 < tmp2.length; t2++) {
						tmp2[t2].replace(" ", "");
						action.add(tmp2[t2]);
					}
				}

			}
			}

		}
		return action;
	}

	private boolean getInitial(StateflowState state) {

		UnmodifiableIterator<StateflowTransition> it = state.getInTransitions().iterator();
		while (it.hasNext()) {
			if (it.next().getSrc() == null) {
				return true;
			}
		}
		return false;
	}

	private Transition createTransition(StateflowTransition stateflowTransition, FsmModule parent) {
		
		String condition = stateflowTransition.getLabel();
		
		boolean tError=false;
		if(this.error.isTransitionNoForm()){
			condition=createTransitionNoForm(condition,parent,stateflowTransition);
			tError=getTranError(condition);
			if(!tError&& condition!=""){
				condition = condition.substring(condition.indexOf('[')+1,condition.lastIndexOf(']'));
			}
		}else{
			if(condition!=null)
			condition = condition.substring(condition.indexOf('[')+1,condition.lastIndexOf(']'));
	
		}
		
		
		
		
		State start_state = findState(parent,
				((StateflowState) stateflowTransition.getSrc()).getLabel());
		State end_state = findState(parent,
				((StateflowState) stateflowTransition.getDst()).getLabel());

		assert (start_state != null && end_state != null);
		Transition t = new Transition(start_state);
		if(condition!=null){
		condition=condition.replace("&&", "&");
		condition=condition.replace("||","|");
		condition=condition.replace("==", "=");
		}
		t.setCondition(condition);
		t.setEnd_state(end_state);
		if(!this.error.getDataScopeIncorrect().isEmpty()&&t.getCondition()!=null){
			ArrayList<StateflowData> dataList = this.error.getDataScopeIncorrect();
			for(int i=0; i<dataList.size(); i++){
				String data=dataList.get(i).getName();
				if(t.getCondition().contains(data)){
					tError=true;
				}
			}
		}
		t.setConversionError(tError);
		

		return t;
	}

	private boolean getTranError(String condition) {
		boolean error=false;
		if (!condition.startsWith("[")||(condition.contains("{")||condition.contains("/")))
			return true;
		
		return error;
	}

	private String createTransitionNoForm(String condition, FsmModule parent, StateflowTransition stateflowTransition) {
		String event="";
		String conditionAction="";
		String transitionAction="";
		String conditionEvent="";
		String transAction="";
		if(condition!=null){
			if (!condition.startsWith("[")&&!condition.startsWith("{")&&!condition.startsWith("/")) {
				event=condition;
				if(condition.contains("[")){
				event=condition.substring(0,condition.indexOf('['));
				}else{
					if(condition.contains("{")){
						if(condition.charAt(condition.indexOf('{')-1)!='/')
							event=condition.substring(0,condition.indexOf('{'));

					}else{
						if(condition.contains("/")){
							event=condition.substring(0,condition.indexOf('/'));

						}
						
					}
					
				}
				
				String[] events=event.split("\\|");
				for(int i=0; i<events.length;i++){
				if(getEventVar(events[i],parent)){
					if(conditionEvent!=""){
					conditionEvent= conditionEvent+"&&("+events[i]+"!="+events[i]+"_old)";
					}else{
						conditionEvent="("+events[i]+"!="+events[i]+"_old)";
					}
					event=event.replace(events[i], "");
					transAction= events[i]+"_old="+events[i];
				}
				}

				}
			if(condition.contains("{")){
				if(condition.indexOf('{')!=0){
					if(condition.charAt(condition.indexOf('{')-1)!='/')
						conditionAction = "'"+condition.substring(condition.indexOf('{'),condition.lastIndexOf('}')+1)+"'";
				}else{
					conditionAction = "'"+condition.substring(condition.indexOf('{'),condition.lastIndexOf('}')+1)+"'";

				}
			}
			if(condition.contains("/")){
				if(transitionAction.endsWith(";")){
					transitionAction=condition.substring(condition.indexOf('/'),(condition.length()-1));



				}
				transitionAction=condition.substring(condition.indexOf('/'),(condition.length()));
				if(transitionAction.contains("{")){
					transitionAction=condition.substring(condition.indexOf('{'),(condition.lastIndexOf('}')));
				}
				String tmp=transitionAction.substring(1);
				
				if(getEventVar(tmp,parent)){
					transitionAction="/"+tmp+"=!"+tmp+";";
				}
				
				if(stateflowTransition.getDst().getInTransitions().size()==1){
					State src=findState(parent,((StateflowState) stateflowTransition.getDst()).getLabel());
					if(transitionAction.endsWith(";")){
						transitionAction=transitionAction.substring(transitionAction.indexOf('/'),(transitionAction.length()-1));

					}
					transitionAction=transitionAction.substring(1);
					src.addOnentry(transitionAction);
					if(transAction.compareTo("")!=0){
						src.addOnentry(transAction);
						transAction="";
					}

					transitionAction="";
				}else{
					if(transAction.compareTo("")!=0&&transitionAction.compareTo("")!=0){
						transitionAction=transitionAction+"; "+transAction+";";


					}
					//error=true;

				}
			}
			if(transAction.compareTo("")!=0&&transitionAction.compareTo("")==0){
				if(stateflowTransition.getDst().getInTransitions().size()==1){
					State src=findState(parent,((StateflowState) stateflowTransition.getDst()).getLabel());
						src.addOnentry(transAction);
						transAction="";
					}else{
						transitionAction="/"+transAction+";";

					}
			}
			
			if(condition.contains("[")){
			condition = condition.substring(condition.indexOf('[')+1,condition.lastIndexOf(']'));
				if(conditionEvent!=""){
					condition="("+condition+")&&"+conditionEvent;
				}
			}else{
				condition=conditionEvent;
			}
			

			if(condition.compareTo("")==0){
			return event+conditionAction+transitionAction;
			}else{
			return event+"["+condition+"]"+conditionAction+transitionAction;
			}
			
		}else{
			return "";
		}
		
	}

	private boolean getEventVar(String events, FsmModule parent) {
		List<LocalVariable> local=parent.getLocal_variables();
		for(int i=0; i<local.size();i++)
			if(local.get(i).getName().compareTo(events)==0)
				return true;
		return false;
	}

	private State findState(FsmModule parent, String label) {
		Iterator<State> it = parent.getStates().iterator();
		String[] sa = label.split("\\\\n");
		String name = sa[0];
		while (it.hasNext()) {
			State s = it.next();

			if (s.getName().compareTo(name) == 0)

				return s;
		}
		return null;
	}

	private String covertType(String parameter) {
		
		if (parameter == "boolean") {
			return "Boolean";
		}
		else{
			return "Int";
		}
		
	}

	private ArrayList<StateflowTransition> getTransition(
			ArrayList<StateflowState> states) {
		ArrayList<StateflowTransition> transition = new ArrayList<StateflowTransition>();
		for (int i = 0; i < states.size(); i++) {
			UnmodifiableIterator<StateflowTransition> transition_it = states
					.get(i).getInTransitions().iterator();
			while (transition_it.hasNext()) {
				StateflowTransition t = transition_it.next();
				if (!transition.contains(t)) {
					transition.add(t);
				}
			}

		}
		return transition;
	}

	private ArrayList<StateflowData> getData(StateflowChart module, String type) {
		UnmodifiableIterator<StateflowData> vars_it = module.getData().iterator();
		ArrayList<StateflowData> tmp = new ArrayList<StateflowData>();
		while (vars_it.hasNext()) {
			StateflowData var = vars_it.next();
			if (var.getParameter("scope") == type) {
				tmp.add(var);
			}
		}

		return tmp;
	}

	protected ArrayList<StateflowState> getStateLeaf(
			UnmodifiableSet<StateflowNodeBase> state, String prefix,
			ArrayList<StateflowTransition> transOut,
			ArrayList<StateflowTransition> transIn,
			ArrayList<StateflowState> states) {

		UnmodifiableIterator<StateflowNodeBase> i = state.iterator();
		while (i.hasNext()) {
			StateflowState nodo = (StateflowState) i.next();
			if(nodo.getParameter("type")!="FUNC_STATE"){
			if (!nodo.getNodes().isEmpty()) {
				// nodo padre- va eliminato ricordando per� la sua presenza,
				// e le transizioni in ingresso vanno allo stato first mentre
				// quelle in uscita partono da tutti.
				String name = nodo.getLabel();
				String[] sa = name.split("[\\\\]");
				prefix = prefix.concat(sa[0] + "_");
				ArrayList<StateflowTransition> tmpOut = getTransOut(nodo);
				ArrayList<StateflowTransition> tmpIn = getTransIn(nodo);
				transOut.addAll(tmpOut);
				if (!transIn.isEmpty()) {
					editTransIn(transIn, nodo);
				}
				transIn = getTransIn(nodo);
				// transizioni in uscita, dovranno uscire dai figli
				state = nodo.getNodes();
				states = getStateLeaf(state, prefix, transOut, transIn,
						states);
				prefix = prefix.replaceAll(sa[0] + "_", "");
				// rimuovere transout
				Iterator<StateflowTransition> iter = tmpOut.iterator();
				while (iter.hasNext()) {
					iter.next().remove();
				}
				transOut.removeAll(tmpOut);
				transIn.removeAll(tmpIn);
				// funzione ricorsiva (passo i e prefix)
			}
			if (nodo.getNodes().isEmpty()) {
				if (!transOut.isEmpty()) {
					editTransOut(transOut, nodo);// devo settare la sorgente
				}										// di queste transizioni
														// a questo nodo
					String name = nodo.getLabel();
					String[] sa = name.split("[\\\\]");
					sa[0] = prefix.concat(sa[0]);
					name = sa[0];
					for (int i1 = 1; i1 < sa.length; i1++) {
						name = name + "\\" + sa[i1];
					}
					nodo.setParameter("labelString", name);
				
				if (!(transIn.isEmpty())) {
					editTransIn(transIn, nodo);
				}
				states.add(nodo);
			}

		}
		}
		return states;
	}

	private void editTransIn(ArrayList<StateflowTransition> transIn,
			StateflowState nodo) {

		boolean flag = false;
		ArrayList<StateflowTransition> trans_in = getTransIn(nodo);
		for (int i = 0; i < trans_in.size(); i++) {
			if (trans_in.get(i).getSrc() == null) {
				trans_in.get(i).remove();
				flag = true;
			}
		}
		Iterator<StateflowTransition> iter = transIn.iterator();
		if (flag) {
			while (iter.hasNext()) {
				StateflowTransition i = iter.next();
				StateflowNodeBase src = i.getSrc();
				StateflowNodeBase dst = nodo;
				if (src == null) {
					// creo nuova stateflow transition di default(iniziale), che
					// associo allo stato figlio, se anche questo � iniziale.
					new StateflowTransition(dst);
				} else {
					StateflowTransition nuova = new StateflowTransition(src,
							dst);
					String layout_dst[] = getStringMod(src
							.getParameter("position"));
					String layout_src[] = getStringMod(dst
							.getParameter("position"));
					String Layout_transition = getLayout(layout_src, layout_dst);
					UnmodifiableIterator<String> parametri = i
							.getDeclaredParameterNames().iterator();
					while (parametri.hasNext()) {
						String par = parametri.next();
						nuova.setParameter(par, i.getParameter(par));
					}
					nuova.setParameter("dst_intersection", Layout_transition);
				}
				i.remove();
			}
		}
	}

	// trasferisco la transizione in uscita del padre al figlio
	private void editTransOut(ArrayList<StateflowTransition> transout,
			StateflowState nodo) {

		Iterator<StateflowTransition> iter = transout.iterator();
		while (iter.hasNext()) {
			StateflowTransition i = iter.next();
			StateflowNodeBase src = nodo;
			StateflowNodeBase dst = i.getDst();
			String layout_src[] = getStringMod(src.getParameter("position"));
			String layout_dst[] = getStringMod(dst.getParameter("position"));
			String Layout_transition = getLayout(layout_src, layout_dst);
			UnmodifiableIterator<String> parametri = i
					.getDeclaredParameterNames().iterator();
			StateflowTransition nuova = new StateflowTransition(src, dst);
			while (parametri.hasNext()) {
				String par = parametri.next();
				nuova.setParameter(par, i.getParameter(par));
			}
			nuova.setParameter("src_intersection", Layout_transition);
			
			String dstIntersection=nuova.getDeclaredParameter("dst_intersection");
			dstIntersection = dstIntersection.substring(0, 0) + dstIntersection.substring(0 + 1);
			dstIntersection = dstIntersection.substring(0, dstIntersection.length() - 1)
					+ dstIntersection.substring(dstIntersection.length());
			
			String[] dst_int = dstIntersection.split(",");
			if(dst_int[0].equals("2")||dst_int[0].equals("4")){
			int he = (int) Float.parseFloat(layout_dst[3]);
			int ye = (int) Float.parseFloat(layout_dst[1]);
			int y=(int) Float.parseFloat(dst_int[5]);
			
			y= (int)((ye+15) + Math.random()*(he-15));
			String layout = "[" + dst_int[0] + "," +dst_int[1]+"," +dst_int[2]+"," +dst_int[3]+"," +dst_int[4]+"," +y+"," +dst_int[6]+","+dst_int[7]+"]";
			nuova.setParameter("dst_intersection", layout);
			}
			else{
				if(dst_int[0].equals("1")||dst_int[0].equals("3")){
					int we = (int) Float.parseFloat(layout_dst[2]);
					int xe = (int) Float.parseFloat(layout_dst[0]);
					int x=(int) Float.parseFloat(dst_int[4]);
					
					x= (int)((xe+15) + Math.random()*(we-15));
					String layout = "[" + dst_int[0] + "," +dst_int[1]+"," +dst_int[2]+"," +dst_int[3]+"," +x+"," +dst_int[5]+"," +dst_int[6]+","+dst_int[7]+"]";
					nuova.setParameter("dst_intersection", layout);
			}
			}
		}

	}

	private String getLayout(String[] layout_src, String[] layout_dst) {

		// dati stato in ingresso
		int xs = (int) Float.parseFloat(layout_src[0]);
		int ys = (int) Float.parseFloat(layout_src[1]);
		int ws = (int) Float.parseFloat(layout_src[2]);
		int hs = (int) Float.parseFloat(layout_src[3]);
		// dati stato in uscita
		int xe = (int) Float.parseFloat(layout_dst[0]);
		int ye = (int) Float.parseFloat(layout_dst[1]);
		int we = (int) Float.parseFloat(layout_dst[2]);
		//int he = (int) Float.parseFloat(layout_dst[3]);

		String layout;
		int pos = 0;
		int x = 0;
		int y = 0;
		if (xs+ws < xe) {
			pos = 2;
			x = xs + ws;
			y = ys + (int) (Math.random() * hs/2);
			
		} else {
			if(xs+ws<=xe&&xs<xe+we){
				if(ye>ys+hs){
					pos=3;
					x=xs+(int) (Math.random() * ws/2);
					y=ys+hs;	
				}else{
					pos =1;
					x=xs+(int) (Math.random() * ws/2);
					y=ys;
				}
				
				
			}else{
				pos = 4;
				x = xs;
				y = ys + (int) (Math.random() * hs/2);
				}
			}
		layout = "[" + pos + ", 0, -1, 0.9335, " + x + ", " + y
				+ ", 0, 64.4289]";

		return layout;
	}

	private ArrayList<StateflowTransition> getTransOut(StateflowState state) {
		ArrayList<StateflowTransition> transitions_out = new ArrayList<StateflowTransition>();
		UnmodifiableSet<StateflowTransition> out = state.getOutTransitions();
		UnmodifiableIterator<StateflowTransition> out_it = out.iterator();
		while (out_it.hasNext()) {
			transitions_out.add(out_it.next());
		}
		return transitions_out;
	}

	private ArrayList<StateflowTransition> getTransIn(StateflowState state) {

		ArrayList<StateflowTransition> transitions_in = new ArrayList<StateflowTransition>();
		UnmodifiableSet<StateflowTransition> in = state.getInTransitions();
		UnmodifiableIterator<StateflowTransition> in_it = in.iterator();
		while (in_it.hasNext()) {
			transitions_in.add(in_it.next());

		}
		return transitions_in;
	}

	private void createViews() {
		main_win.new_model_created.emit(true);
		main_win.getView().setEnabled(true);
		new FrameModuleTreeView(main_win.getModule(), main_win.getProjectTree());
		main_win.getModule().added.emit();
		createFsmModuleInstanceViews(main_win);
	}

	private void createFsmModuleInstanceViews(FrameModuleWindowView parent_view) {
		Iterator<FsmModuleInstance> it = parent_view.getModule()
				.getFsmModuleInstances().iterator();
		while (it.hasNext()) {
			FsmModuleInstance instance = it.next();

			StateflowChart e = null;
			for (int i = 0; i < Fsms.size(); i++) {
				StateflowChart m = Fsms.get(i).getChart();
				if (m.getName().compareTo(
						instance.getInstancedModule().getName()) == 0) {
					e = Fsms.get(i).getChart();
					i = Fsms.size();
				}
			}
			assert (e != null);
			
			String layout = e.getStateflowBlock().getParameter("Position");

			layout = layout.substring(0, 0) + layout.substring(0 + 1);
			layout = layout.substring(0, layout.length() - 1)
					+ layout.substring(layout.length());

			String[] l = layout.split(",");

			int x = (int) Float.parseFloat(l[0]);
			int y = (int) Float.parseFloat(l[1]);
			int width = (int) (Float.parseFloat(l[2])-Float.parseFloat(l[0]));
			int height = (int) (Float.parseFloat(l[3])-Float.parseFloat(l[1]));


			FsmModuleInstanceGraphicView instance_gview = new FsmModuleInstanceGraphicView(
					instance, x, y, parent_view.getView(),
					parent_view.getProjectTree());
			instance_gview.setWidth(width);
			instance_gview.setHeight(height);
			instance_gview.resize();
			instance_gview.resized.emit();

			if (instance.isProcess()) {
				instance_gview.processActivated(true);
			}

			new FsmModuleInstanceTreeView(instance,
					parent_view.getProjectTree(), parent_view.getModule()
							.getName(), instance_gview.getMenu());

			createFsmModuleViews((FsmModule) instance.getInstancedModule(),
					instance_gview, parent_view.getProjectTree());

			instance.added.emit();
		}
	}

	private void createFsmModuleViews(FsmModule module,
			FsmModuleInstanceGraphicView instance_view, TreeWidget project_tree) {
		if (!created_modules.contains(module)) {
			FsmModuleWindowView m_view = new FsmModuleWindowView(module,
					project_tree);
			new FsmModuleTreeView(module, project_tree);

			created_modules.add(module);
			module.added.emit();

			createStateViews(m_view, project_tree);
			createTransitionViews(m_view);
			createLocalVariableViews(m_view, project_tree);
			createOutputVariableViews(m_view, instance_view, project_tree);
			createInputVariableViews(m_view, instance_view, project_tree);
			
		} else if (instance_view != null) {
			// createOnlyHookOutView(instance_view, project_tree);
			// createOnlyHookInView(instance_view, project_tree);
		}
	}

	private void createStateViews(FsmModuleWindowView parent_view,
			TreeWidget project_tree) {
		ArrayList<FsmModified> fsm_list = Fsms;
		FsmModified fsm = null;
		for (int i = 0; i < (fsm_list).size(); i++) {
			if ((fsm = fsm_list.get(i)).getChart().getName()
					.compareTo(parent_view.getModule().getName()) == 0) {
				break;
			}
		}

		Iterator<State> it = parent_view.getModule().getStates().iterator();

		while (it.hasNext()) {
			State s = it.next();

			ArrayList<StateflowState> states = fsm.getStates();

			for (int i = 0; i < states.size(); i++) {
				String label = states.get(i).getLabel();
				String[] sa = label.split("\\\\n");
				String name = sa[0];
				if (name.compareTo(s.getName()) == 0) {

					String layout = states.get(i).getParameter("position");

					layout = layout.substring(0, 0) + layout.substring(0 + 1);
					layout = layout.substring(0, layout.length() - 1)
							+ layout.substring(layout.length());

					String[] l = layout.split(",");

					int x = (int) Float.parseFloat(l[0]);
					int y = (int) Float.parseFloat(l[1]);
					int w = (int) Float.parseFloat(l[2]);
					int h = (int) Float.parseFloat(l[3]);

					StateGraphicView sgv = new StateGraphicView(s, x, y, w, h,parent_view.getView());
					sgv.resized.emit();
					w=sgv.width();
					h=sgv.height();
					layout="["+x+","+y+","+w+","+h+"]";
					states.get(i).setParameter("position", layout);
					sgv.state_clicked.connect(parent_view,"stateClicked(StateGraphicView, int, int, LinePosition)");
					parent_view.getTransitionButton().toggled.connect(sgv,"drawingTransition(boolean)");
					new StateTreeView(s, project_tree, sgv.getMenu());
					s.added.emit();
				}
			}
		}
	}

	private void createTransitionViews(FsmModuleWindowView fsm_view) {

		Iterator<State> it1 = fsm_view.getModule().getStates().iterator();
		while (it1.hasNext()) {
			State ss = it1.next();
			Iterator<Transition> it2 = ss.getExiting_transitions().iterator();
			while (it2.hasNext()) {
				Transition t = it2.next();
				State es = t.getEnd_state();

				ArrayList<FsmModified> fsm_list = Fsms;

				FsmModified fsm = null;
				for (int i = 0; i < fsm_list.size(); i++) {
					if (fsm_list.get(i).getChart().getName()
							.compareTo(fsm_view.getModule().getName()) == 0) {
						fsm = fsm_list.get(i);
						break;
					}
				}
				assert fsm != null;

				ArrayList<StateflowTransition> transitions = fsm.getTransitions();
				StateflowTransition transition = null;

				for (int i = 0; i < transitions.size(); i++) {
					if (transitions.get(i).getSrc() != null) {
						String label = ((StateflowState) transitions.get(i).getSrc()).getLabel();
						String[] sa = label.split("\\\\n");
						String src_state = sa[0];
						label = ((StateflowState) transitions.get(i).getDst()).getLabel();
						sa = label.split("\\\\n");
						String dst_state = sa[0];

						if (src_state.compareTo(ss.getName()) == 0 && dst_state.compareTo(es.getName()) == 0) {
							transition = transitions.get(i);
							break;
						}
					}
				}
				assert (transition != null);
				String src = transition.getParameter("src_intersection");
				src = src.substring(0, 0) + src.substring(0 + 1);
				src = src.substring(0, src.length() - 1)
						+ src.substring(src.length());

				String[] src_int = src.split(",");

				String dst = transition.getParameter("dst_intersection");

				dst = dst.substring(0, 0) + dst.substring(0 + 1);
				dst = dst.substring(0, dst.length() - 1)
						+ dst.substring(dst.length());

				String[] dst_int = dst.split(",");

				int start = Integer.valueOf(src_int[0]);
				int end = Integer.valueOf(dst_int[0]);

				String s = getPosition(start);
				String e = getPosition(end);
				String layout_src[] = getStringMod(transition.getSrc().getParameter("position"));
				String layout_dst[] = getStringMod(transition.getDst().getParameter("position"));
				
				int sx = (int) Float.parseFloat(src_int[4]);
				int sy = (int) Float.parseFloat(src_int[5]);
				int ex = (int) Float.parseFloat(dst_int[4]);
				int ey = (int) Float.parseFloat(dst_int[5]);
				
				if(start==1){
					sy=(int) Float.parseFloat(layout_src[1]);
				}
				if(start==2){
					sx=(int) (Float.parseFloat(layout_src[0])+Float.parseFloat(layout_src[2]));
				}
				if(start==3){
					sy=(int) (Float.parseFloat(layout_src[1])+Float.parseFloat(layout_src[3]));
				}
				if(start==4){
					sx=(int) Float.parseFloat(layout_src[0]);
				}
				if(end==1){
					ey=(int) Float.parseFloat(layout_dst[1]);

				}
				if(end==2){
					ex=(int) (Float.parseFloat(layout_dst[0])+Float.parseFloat(layout_dst[2]));

				}
				if(end==3){
					ey=(int) (Float.parseFloat(layout_dst[1])+Float.parseFloat(layout_dst[3]));
				}
				if(end==4){
					ex=(int) Float.parseFloat(layout_dst[0]);

				}

				LinePosition start_pos = LinePosition.valueOf(s);
				LinePosition end_pos = LinePosition.valueOf(e);
				TransitionGraphicView tgv = new TransitionGraphicView(t, sx,
						sy, ex, ey, start_pos, end_pos, fsm_view.getView());
				ss.added_exit_transition.emit(tgv);
				es.added_entry_transition.emit(tgv);
				transitions.remove(transition);
			}
		}
	}

	private void createLocalVariableViews(ModuleWindowView parent_view,
			TreeWidget project_tree) {
		Iterator<LocalVariable> it = parent_view.getModule()
				.getLocal_variables().iterator();

		while (it.hasNext()) {
			LocalVariable lv = it.next();

			new LocalVariableTreeView(project_tree, lv);
			new LocalVariableDockView(parent_view.getLocalTree(), lv, null);

			lv.added.emit();
		}
	}

	private void createInputVariableViews(ModuleWindowView parent_view,ModuleInstanceGraphicView instance_parent_view,
			TreeWidget project_tree) {
		Iterator<InputVariable> it = parent_view.getModule()
				.getInput_variables().iterator();
		int counter = 1;
		while (it.hasNext()) {

			InputVariable in = it.next();

			new InputVariableDockView(in, parent_view.getInputTree());

			if (instance_parent_view != null) {
				ArrayList<FsmModified> fsm_list = Fsms;
				FsmModified fsm = null;
				for (int i = 0; i < (fsm_list).size(); i++) {
					if ((fsm = fsm_list.get(i)).getChart().getName()
							.compareTo(instance_parent_view.getModuleName()) == 0) {
						break;
					}
				}

				assert (fsm != null);

				ArrayList<StateflowData> inputs = fsm.getInput_vars();

				StateflowData input = null;

				for (int i = 0; i < inputs.size(); i++) {

					if (inputs.get(i).getName().compareTo(in.getName()) == 0) {
						input = inputs.get(i);
						break;
					}
				}
				assert (input != null);

				String layout = fsm.getChart().getStateflowBlock().getParameter("Position");
				layout = layout.substring(0, 0) + layout.substring(0 + 1);
				layout = layout.substring(0, layout.length() - 1)
						+ layout.substring(layout.length());

				String[] l = layout.split(",");


				int x = (int) Float.parseFloat(l[0])-8;
				int h = (int) (Float.parseFloat(l[3])-Float.parseFloat(l[1]));
				int y = (int) Float.parseFloat(l[1]) + counter * (h/(inputs.size() + 1));
				

				String orientation = "to_right";
				instance_parent_view.resized.emit();
				InputVariableHookView ivhv = new InputVariableHookView(
						instance_parent_view, in, x, y,
						instance_parent_view.getView(), orientation);
				input_to_connect.add(ivhv);
			}

			new InputVariableTreeView(in, parent_view.getModule(), project_tree);
			in.added.emit();
			counter++;
		}
	}

	private void createOutputVariableViews(ModuleWindowView parent_view,
			ModuleInstanceGraphicView instance_parent_view,
			TreeWidget project_tree) {
		Iterator<OutputVariable> it = parent_view.getModule().getOutput_variables().iterator();
		int counter = 1;

		while (it.hasNext()) {
			OutputVariable out = it.next();

			
			new OutputVariableDockView(out, parent_view.getOutputTree());

			if (instance_parent_view != null) {

				ArrayList<FsmModified> fsm_list = Fsms;
				FsmModified fsm = null;
				for (int i = 0; i < (fsm_list).size(); i++) {
					if ((fsm = fsm_list.get(i)).getChart().getName()
							.compareTo(instance_parent_view.getModuleName()) == 0) {
						break;
					}
				}

				assert (fsm != null);

				ArrayList<StateflowData> outputs = fsm.getInput_vars();

				StateflowData output = null;

				for (int i = 0; i < outputs.size(); i++) {

					if (outputs.get(i).getName().compareTo(out.getName()) == 0) {
						output = outputs.get(i);
						break;
					}
				}
				assert (output != null);

				String layout = fsm.getChart().getStateflowBlock().getParameter("Position");
				layout = layout.substring(0, 0) + layout.substring(0 + 1);
				layout = layout.substring(0, layout.length() - 1)
						+ layout.substring(layout.length());

				String[] l = layout.split(",");

				int x = (int) ( Float.parseFloat(l[2]))+3;
				int h = (int) (Float.parseFloat(l[3])-Float.parseFloat(l[1]));
				int y = (int) Float.parseFloat(l[1]) + counter * (h/(outputs.size() + 1));

				String orientation = "to_left";

				new OutputVariableHookView(instance_parent_view, out, x, y,
						instance_parent_view.getView(), orientation);
				instance_parent_view.resized.emit();
			}

			new OutputVariableTreeView(out, parent_view.getModule(),
					project_tree);
			out.added.emit();
			counter++;

		}
	}

	private String getPosition(int pos) {
		if (pos == 1) {
			return "TOP";
		}
		if (pos == 2) {
			return "RIGHT";
		}
		if (pos == 3) {
			return "BOTTOM";
		}
		if (pos == 4) {
			return "LEFT";
		}
		return null;
	}

	private String[] getStringMod(String s) {
		s = s.substring(0, 0) + s.substring(0 + 1);
		s = s.substring(0, s.length() - 1) + s.substring(s.length());

		String[] l = s.split(",");
		return l;
	}
	
	private void connectInput()
	{
	
		Iterator<FsmModified> fsmIterator=Fsms.iterator();
		while(fsmIterator.hasNext()){
			FsmModified fsm =fsmIterator.next();
			Iterator<SimulinkLine> linesIterator = fsm.getChart().getStateflowBlock().getInLines().iterator();
			while (linesIterator.hasNext()){
				SimulinkLine line = linesIterator.next();
				if(line.getDstPort().getBlock().getClass().getName().compareTo("edu.tum.cs.simulink.model.stateflow.StateflowBlock")==0&&
						line.getSrcPort().getBlock().getClass().getName().compareTo("edu.tum.cs.simulink.model.stateflow.StateflowBlock")==0){
			
					String indexIn= line.getDstPort().getIndex();
					String indexChartIn = line.getDstPort().getBlock().getId();
					UnmodifiableIterator<SimulinkBlock> portItIn =stateflow.getModel().getBlock(indexChartIn).getSubBlocks().iterator();
		   
					String inputName=getVarName(portItIn, indexIn,"Inport");
			
					String indexOut= line.getSrcPort().getIndex();
					String indexChartOut = line.getSrcPort().getBlock().getId();
					UnmodifiableIterator<SimulinkBlock> portItOut =stateflow.getModel().getBlock(indexChartOut).getSubBlocks().iterator();			

					String var_name=getVarName(portItOut, indexOut,"Outport");
					String chartName= line.getSrcPort().getBlock().getName();
					
					InputVariableHookView in= getInputHookView(inputName);
					FrameModule m = ((ModuleInstanceGraphicView)in.getParent()).getInstance().getParentModule();
			
					connectInputToOutput(chartName, in, var_name, m);
			
					}
				
				}
			}
	}
	
	
	private InputVariableHookView getInputHookView(String inputName){
		Iterator<InputVariableHookView> inputIterator=input_to_connect.iterator();
		while(inputIterator.hasNext()){
			InputVariableHookView input= inputIterator.next();
			if(input.getInputVariable().getName().compareTo(inputName)==0){
				return input;
			}
		}	
		return null;
	}
	
	private String getVarName(UnmodifiableIterator<SimulinkBlock> portIt,String index, String type){
		String var_name=null;
	    while(portIt.hasNext()){
	    	SimulinkBlock port= portIt.next();
	    	if(port.getParameter("BlockType").compareTo(type)==0){
	    	String portid= port.getParameter("Port");
	    	if(portid==null){
	    		if(index=="1"){
		    		var_name= port.getName();
		    		}

	    	}else{
	    		if(portid.compareTo(index)==0){
	    			var_name= port.getName();
	    		}
	    	}
	    }
	    }
	    return var_name;
	}
	
	private void connectInputToOutput(String chartName, InputVariableHookView in, String var_name, FrameModule m)
	{
		ModuleInstance module = null; 
		
		Iterator<FsmModuleInstance> it2 = m.getFsmModuleInstances().iterator();
		
		while (module == null && it2.hasNext())
		{
			FsmModuleInstance mod = it2.next();
			if (mod.getName().compareTo(chartName) == 0)
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

	
	

}
