package translator;

import java.util.ArrayList;

import edu.tum.cs.simulink.model.stateflow.StateflowData;
import edu.tum.cs.simulink.model.stateflow.StateflowEvent;

/**
 * Classe errori riscontrati nel modello STateflow.
 * @author Daniele Sbaraccani
 *
 */
public class Error {

	private boolean box;
	private boolean junction;
	private boolean event;
	private boolean function;
	private boolean stateParentWithAction;
	private boolean transitionLevel;
	private boolean stateAnd;
	private boolean actionNoForm;
	private boolean dataNoForm;
	private boolean typeNoform;
	private boolean transitionNoFormNotModificable;
	private boolean transitionNoForm;
	private boolean stateflowForm;
	private boolean truthTable;
	private boolean eMfunction;
	private boolean simulinkFunction;
	private ArrayList<String> stateHierarchy;
	private ArrayList<String> funzioniIgnorate;
	private ArrayList<StateflowData> dataTypeConvert;
	private ArrayList<StateflowData> dataScopeIncorrect;
	private ArrayList<StateflowEvent> eventConverted;

	

	public Error() {
		super();
		setBox(false);
		setJunction(false);
		setEvent(false);
		function=false;
		stateParentWithAction=false;
		transitionLevel=false;
		stateAnd=false;
		setActionNoForm(false);
		setDataNoForm(false);
		setTypeNoform(false);
		setTransitionNoForm(false);
		setStateflowForm(false);
		setTruthTable(false);
		seteMfunction(false);
		setSimulinkFunction(false);
		setTransitionNoFormNotModificable(false);
		funzioniIgnorate=new ArrayList<String>();
		stateHierarchy=new ArrayList<String>();
		dataTypeConvert=new ArrayList<StateflowData>();
		eventConverted=new ArrayList<StateflowEvent>();
		dataScopeIncorrect=new ArrayList<StateflowData>();
		}
	
	public Error(String error){
		if(error == "stateflowNOform"){
		setBox(false);
		setJunction(false);
		setEvent(false);
		function=false;
		stateParentWithAction=false;
		transitionLevel=false;
		stateAnd=false;
		setActionNoForm(false);
		setDataNoForm(false);
		setTypeNoform(false);
		setTransitionNoForm(false);
		setStateflowForm(true);
		}
	}
	



	public boolean isBox() {
		return box;
	}

	public void setBox(boolean box) {
		this.box = box;
	}

	public boolean isJunction() {
		return junction;
	}

	public void setJunction(boolean junction) {
		this.junction = junction;
	}

	public boolean isEvent() {
		return event;
	}

	public void setEvent(boolean event) {
		this.event = event;
	}

	public boolean isFunction() {
		return function;
	}

	public void setFunction(boolean function) {
		this.function = function;
	}
	public void setStateflowForm(boolean stateflowForm){
		this.stateflowForm=stateflowForm;
	}
	/**
	 * ritorna risultato degli errori
	 * @return true se non ci sono errori, false se abbiamo almeno un errore.
	*/
	public boolean getResult(){
		boolean result=!box&&!junction&&!event&&!function&&
		!stateParentWithAction&&!transitionLevel&&!stateAnd&&!actionNoForm&&
		!dataNoForm&&!typeNoform&&!transitionNoForm&&!truthTable&&!eMfunction&&!simulinkFunction&&stateHierarchy.isEmpty();
		return result;
	}
	
	public boolean getResultError(){
		boolean result=!box&&!junction&&!stateParentWithAction&&!transitionLevel&&!actionNoForm&&!stateflowForm&&!truthTable&&!eMfunction&&!simulinkFunction;
		return result;
	}
	
	public boolean getResultWarning(){
		boolean result=!event&&!function&&!dataNoForm&&!typeNoform&&!transitionNoForm&&stateHierarchy.isEmpty();
		return result;
	}
	
	public boolean getStateflowForm(){
		return stateflowForm;
	}

	public void setStateParentWithAction(boolean stateParentWithAction) {
		this.stateParentWithAction = stateParentWithAction;
	}

	public boolean isStateParentWithAction() {
		return stateParentWithAction;
	}

	public void setTransitionLevel(boolean transitionLevel) {
		this.transitionLevel = transitionLevel;
	}

	public boolean isTransitionLevel() {
		return transitionLevel;
	}

	public void setStateAnd(boolean stateAnd) {
		this.stateAnd = stateAnd;
	}

	public boolean isStateAnd() {
		return stateAnd;
	}

	public void setActionNoForm(boolean actionNoForm) {
		this.actionNoForm = actionNoForm;
	}

	public boolean isActionNoForm() {
		return actionNoForm;
	}

	public void setDataNoForm(boolean dataStoreMemoryData) {
		this.dataNoForm = dataStoreMemoryData;
	}

	public boolean isDataNoForm() {
		return dataNoForm;
	}

	public void setTypeNoform(boolean typeNoform) {
		this.typeNoform = typeNoform;
	}

	public boolean isTypeNoform() {
		return typeNoform;
	}

	public void setTransitionNoForm(boolean transitionNoForm) {
		this.transitionNoForm = transitionNoForm;
	}

	public boolean isTransitionNoForm() {
		return transitionNoForm;
	}

	public void addFunzioniIgnorate(String funzione) {
		funzioniIgnorate.add(funzione);
	}

	public ArrayList<String> getFunzioniIgnorate() {
		return funzioniIgnorate;
	}

	public void addDataTypeConvert(StateflowData data) {
		dataTypeConvert.add(data);
	}

	public ArrayList<StateflowData> getDataTypeConvert() {
		return dataTypeConvert;
	}

	public void addEventConverted(StateflowEvent event) {
		eventConverted.add(event);
	}

	public ArrayList<StateflowEvent> getEventConverted() {
		return eventConverted;
	}

	public void addStateHierarchy(String state) {
		stateHierarchy.add(state);
	}

	public ArrayList<String> getStateHierarchy() {
		return stateHierarchy;
	}

	public void setTruthTable(boolean truthTable) {
		this.truthTable = truthTable;
	}

	public boolean isTruthTable() {
		return truthTable;
	}

	public void seteMfunction(boolean eMfunction) {
		this.eMfunction = eMfunction;
	}

	public boolean iseMfunction() {
		return eMfunction;
	}

	public void setSimulinkFunction(boolean simulinkFunction) {
		this.simulinkFunction = simulinkFunction;
	}

	public boolean isSimulinkFunction() {
		return simulinkFunction;
	}

	public void setTransitionNoFormNotModificable(boolean transitionNoFormModificable) {
		this.transitionNoFormNotModificable = transitionNoFormModificable;
	}

	public boolean isTransitionNoFormNotModificable() {
		return transitionNoFormNotModificable;
	}

	public void addDataScopeIncorrect(StateflowData data) {
		dataScopeIncorrect.add(data);
	}

	public ArrayList<StateflowData> getDataScopeIncorrect() {
		return dataScopeIncorrect;
	}



}
