/********************************************************************************
*                                                                               *
*   Module      :   StateflowModelNoForm.java                                                *

*   Author      :   Daniele Sbaraccani		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package dialog;

import translator.Error;

import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QPushButton;

public class StateflowModelNoForm extends QMessageBox
{
	private boolean test=false;
	/**
	 * Costruttore: crea il MsgBox delle incompatibilità riscontrate
	 */
	public StateflowModelNoForm(Error error)
	{
		if(test==false){
			
			
			
			QPushButton buttom1 = new QPushButton("Ok");
			QPushButton buttom2 = new QPushButton("Continue");
			setWindowIcon(apps.Icon.nusmv());

	        String text= "Causes : \n";
		if(error.getStateflowForm()){
			
			setWindowTitle("Error: Stateflow model contains errors");
			text=("The file is not recognized as Stateflow model \n");
			setIconPixmap(new QPixmap("src/pixmap/error.png"));
			buttom1.setText("Ok");

		}else{
			
			if(!error.getResultError()){
				setWindowTitle("Error: Stateflow model is not compatible");
				buttom1.setText("Ok");
				text=text.concat("\nError: \n");
				if(error.isBox())
					text=text.concat("- Box are not supported \n");
				if(error.isJunction())
					text=text.concat("- Junctions are not supported \n");
				if(error.isStateAnd())
					text=text.concat("- States And are not supported \n");
				if(error.isSimulinkFunction())
					text=text.concat("- Simulink functions are not supported \n");
				if(error.isTruthTable())
					text=text.concat("- Truth Tables are not supported \n");
				if(error.iseMfunction())
					text=text.concat("- eM functions are not supported \n");
				if(error.isActionNoForm())
					text=text.concat("- Presence of actions Bind, on EVENT_NAME or type unknown \n");
				if(error.isStateParentWithAction())
					text=text.concat("- State parent with actions are not supported \n");
				if(error.isTransitionLevel())
					text=text.concat("- Transitions between different levels \n");




			}else{
				setWindowTitle("Warning: Stateflow model is not compatible");
				buttom1.setText("Cancel");


			}
		if(!error.getResultWarning()){
			text=text.concat("\nWarning: \n");
		if(error.isEvent())
			text=text.concat("- Event are not supported \n");
		if(error.isFunction())
			text=text.concat("- Functions are not supported\n");
		if(!error.getStateHierarchy().isEmpty()){
			text=text.concat("- States Hierarchy are not supported \n");
		}
		if(error.isDataNoForm())
			text=text.concat("- Data Scope is not compatible (only local input and output) \n");
		if(error.isTypeNoform())
			text=text.concat("- Data Type is not compatible (only integer,boolean e enumeration) \n");
		setText(text);
		if(error.isTransitionNoForm())
			text=text.concat("- Transition format is not compatible \n");
		}


		
		if(!error.getResultError()){
			setIconPixmap(new QPixmap("src/pixmap/error.png"));
		}else{
			setIconPixmap(new QPixmap("src/pixmap/warning.png"));

		}
		}
		setText(text);
		

		
		if(error.getResultError()){
		buttom2.clicked.connect(this, "test()");
		addButton(buttom2, ButtonRole.YesRole);
		text=text.concat("\n\nNuSMVGUI will perform conversion anyway");
		}
		addButton(buttom1, ButtonRole.AcceptRole);
		setText(text);
		}
        
       
			
	}



	protected void test() {
		test=true;
	}
	
	public boolean isTest(){
		return test;
	}






}
