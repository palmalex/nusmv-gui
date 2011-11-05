/********************************************************************************
*                                                                               *
*   Module      :   StateflowModelNoForm.java                                                *

*   Author      :   Daniele Sbaraccani		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/

package dialog;

import java.util.ArrayList;

import translator.Error;

import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QPushButton;

import edu.tum.cs.simulink.model.stateflow.StateflowData;
import edu.tum.cs.simulink.model.stateflow.StateflowEvent;

public class StateflowConfirmConversion extends QMessageBox
{
	private boolean accepted;
	/**
	 * Costruttore: crea il MsgBox delle modifiche effettuate	 */
	public StateflowConfirmConversion(Error error)
	{
		
			setWindowTitle("Warning: Stateflow model is not compatible");
			setIconPixmap(new QPixmap("src/pixmap/Esclamation.png"));

			String text= "The following adjustments have been applied : \n";
			if(!error.getFunzioniIgnorate().isEmpty()){
				ArrayList<String> function=error.getFunzioniIgnorate();
				for(int i=0; i< function.size();i++){
					text=text.concat(" - function "+function.get(i) +" has been ignored. \n");
				}
			}
			
			if(!error.getStateHierarchy().isEmpty()){
				ArrayList<String> state=error.getStateHierarchy();
				for(int i=0; i< state.size();i++){
					text=text.concat(" - State "+state.get(i) +" has been deleted, transitions have been inherited by children state. \n");
				}
			}
			
			if(!error.getEventConverted().isEmpty()){
				ArrayList<StateflowEvent> event=error.getEventConverted();
				for(int i=0; i< event.size();i++){
					text=text.concat(" - Event "+event.get(i).getName() +" will be converted into 2 boolean variable \n");
				}

			}
			

			
			if(!error.getDataTypeConvert().isEmpty()){
				ArrayList<StateflowData> data=error.getDataTypeConvert();
				for(int i=0; i< data.size();i++){
					text=text.concat(" - Data "+data.get(i).getName() +" will be converted from "+data.get(i).getParameter("dataType")+" to integer \n");
				}

			}
			
			if(!error.getDataScopeIncorrect().isEmpty()){
				ArrayList<StateflowData> data=error.getDataScopeIncorrect();
				for(int i=0; i< data.size();i++){
					text=text.concat(" - Data "+data.get(i).getName() +" has been ignored. \n");
				}

			}
			if(error.isTransitionNoFormNotModificable()){
			text=text.concat("\n\n WARNING: Adjusment marked in red will require futurer modifications by the user");
			}

				setText(text);

				QPushButton ok = new QPushButton("Ok");
				QPushButton cancel = new QPushButton("Cancel");

				
				ok.clicked.connect(this, "accepted()");
				addButton(ok, ButtonRole.AcceptRole);
				addButton(cancel, ButtonRole.RejectRole);
			
		}
		

	protected void accepted() {
		accepted=true;
	}
	
	public boolean isAccepted(){
		return accepted;
	}



}
