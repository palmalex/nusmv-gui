/********************************************************************************
*                                                                               *
*   Module      :   FsmModified.java                                                *

*   Author      :   Daniele Sbaraccani		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/

package translator;

import java.util.ArrayList;

import edu.tum.cs.simulink.model.stateflow.StateflowChart;
import edu.tum.cs.simulink.model.stateflow.StateflowData;
import edu.tum.cs.simulink.model.stateflow.StateflowState;
import edu.tum.cs.simulink.model.stateflow.StateflowTransition;
/**
 * Classe FSM modificato, FSM costruito con le classi simulink Stateflow
 * @author Daniele Sbaraccani
 *
 */
public class FsmModified {
	private StateflowChart chart;
	private ArrayList <StateflowData> localList= new ArrayList<StateflowData>();
	private ArrayList <StateflowData> inputList = new ArrayList<StateflowData>();
	private ArrayList <StateflowData> outputList = new ArrayList<StateflowData>();
	private ArrayList<StateflowState> states = new ArrayList<StateflowState>();
	private ArrayList<StateflowTransition> transitions = new ArrayList<StateflowTransition>();
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 * @param chart SteflowChart.
	 * @param localList ArrayList delle variabili locali del chart.
	 * @param inputList ArrayList delle variabili di input del chart.
	 * @param outputList ArrayList delle variabili di output del chart.
	 * @param states ArrayList degli stati con "gerarchia rimossa" del chart.
	 * @param transitions ArrayList delle transizioni del chart.
	 */
	public FsmModified(StateflowChart chart, ArrayList <StateflowData> localList,ArrayList <StateflowData> inputList,ArrayList <StateflowData> outputList,ArrayList<StateflowState> states,ArrayList<StateflowTransition> transitions  ){
		this.chart=chart;
		this.localList=localList;
		this.inputList=inputList;
		this.outputList=outputList;
		this.states=states;
		this.transitions=transitions;
		
		}
	
	/**
	 * Restituisce il chart.
	 * @return StateflowChart.
	 */
	public StateflowChart getChart(){
		return chart;
	}
	
	/**
	 * Restituisce la lista delle variabili locali.
	 * @return ArrayList di StateflowData.
	 */
	public ArrayList<StateflowData> getLocal_vars() {
		return localList;
	}
	/**
	 * Restituisce la lista delle variabili di input.
	 * @return ArrayList di StateflowData.
	 */
	public ArrayList<StateflowData> getInput_vars() {
		return inputList;
	}
	/**
	 * Restituisce la lista delle variabili di output.
	 * @return ArrayList di StateflowData.
	 */
	public ArrayList<StateflowData> getOutput_vars() {
		return outputList;
	}
	/**
	 * Restituisce la lista degli stati.
	 * @return ArrayList di StateflowState.
	 */
	public ArrayList<StateflowState> getStates() {
		return states;
	}
	/**
	 * Restituisce la lista delle transizioni.
	 * @return ArrayList di StateflowTransition.
	 */
	public ArrayList<StateflowTransition> getTransitions() {
		return transitions;
	}
	

}
