package xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.trolltech.qt.core.QFile;
import com.trolltech.qt.xml.QDomDocument;
import com.trolltech.qt.xml.QDomElement;
import com.trolltech.qt.xml.QDomNodeList;

public class SmvCreator 
{
	private QDomDocument dom;
	private FileWriter out;
	private File smv;
	
	public SmvCreator(String path, String model_name)
	{		
		createDomFromFile(path);
		createSmvFile(model_name, path);
	}
	
	public File getSmvPath()
	{
		return smv;
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
	
	private void createSmvFile(String model_name, String path)
	{
		String filename = model_name + ".smv"; 
		String smv_path = "";
		String s[] = path.split("/");
		
		for (int i = 0; i < s.length - 1; i++)
		{
			smv_path += s[i] + "/";
		}
		smv_path += filename;
		
		smv = new File(smv_path);
		try
		{
			out = new FileWriter(smv);
			
			printFileFromDom();
			
			out.close();
		}
		catch(IOException e) {System.err.println("Errore nella creazione del file .smv");}
	}
	
	private void printFileFromDom() throws IOException
	{
		printModules(dom.elementsByTagName("Module"));
		printFsm(dom.elementsByTagName("FSM"));
	}
	
	private void printModules(QDomNodeList modules) throws IOException
	{
		List<String> mod_name = new ArrayList<String>(0);
		
		for (int i = 0; i < modules.size(); i++)
		{
			QDomElement module = modules.at(i).toElement();
			
			if (!mod_name.contains(module.attribute("name")))
			{
				mod_name.add(module.attribute("name"));
				
				out.write("\n\nMODULE " + module.attribute("name"));
				
				printInputVars(module.elementsByTagName("InputVariable"));
				
				out.write("\tVAR\n");
				printModuleVar(module.elementsByTagName("FrameInstanceVariable"), 
							   module.elementsByTagName("FsmInstanceVariable"),
							   module.elementsByTagName("LocalVariable"),
							   module.elementsByTagName("OutputVariable"));
				
				out.write("\n\tASSIGN\n");
				
				printAssignVar(module.elementsByTagName("LocalVariable"), module.elementsByTagName("OutputVariable"));
				
				printSpecifications(module.elementsByTagName("Specification"));
				
				printFairness(module.elementsByTagName("Fairness"));
			}
		}
	}
	
	private void printFsm(QDomNodeList fsm) throws IOException
	{
		List<String> mod_name = new ArrayList<String>(0);
		
		for (int i = 0; i < fsm.size(); i++)
		{
			QDomElement module = fsm.at(i).toElement();
			
			if (!mod_name.contains(module.attribute("name")))
			{
				mod_name.add(module.attribute("name"));
				
				out.write("\n\nMODULE " + module.attribute("name"));
				
				printInputVars(module.elementsByTagName("InputVariable"));
				
				out.write("\tVAR\n");
				printFsmModuleVar(module.elementsByTagName("OutputVariable"), module.elementsByTagName("LocalVariable"), module.elementsByTagName("State"));
				printChoiceVar(module.elementsByTagName("ChoiceVar"));
				
				out.write("\n\tASSIGN\n");
				
				printFsmAssignVar(module.elementsByTagName("OutputVariable"), module.elementsByTagName("LocalVariable"), 
								  module.elementsByTagName("ChoiceVar"), module.elementsByTagName("State"), 
								  module.elementsByTagName("Transition"), module.elementsByTagName("NonDetTransition"));
				
				printFairness(module.elementsByTagName("Fairness"));
			}
		}
	}
	
	private void printModuleVar(QDomNodeList submodules, QDomNodeList fsm_modules, 
								QDomNodeList local_vars, QDomNodeList output_vars) throws IOException
	{
		for(int i = 0; i < local_vars.size(); i++)
		{
			QDomElement var = local_vars.at(i).toElement().firstChildElement("Properties");
			out.write("\t\t" + var.attribute("name") + "\t: " + var.attribute("values") + ";\n");
		}
		for(int i = 0; i < output_vars.size(); i++)
		{
			QDomElement var = output_vars.at(i).toElement().firstChildElement("Properties");
			out.write("\t\t" + var.attribute("name") + "\t: " + var.attribute("values") + ";\n");
		}
		for (int i = 0; i < submodules.size(); i++)
		{
			QDomElement mod = submodules.at(i).toElement().firstChildElement("Properties");
			out.write("\t\t" + mod.attribute("instance_name") + "\t: ");
			
			String sync = "";
			if (Boolean.parseBoolean(mod.attribute("process")))
			{
				sync = "process ";
			}
			out.write(sync + mod.attribute("module_name"));

			printModuleInput(mod);
		}
		for (int i = 0; i < fsm_modules.size(); i++)
		{
			QDomElement mod = fsm_modules.at(i).toElement().firstChildElement("Properties");
			out.write("\t\t" + mod.attribute("instance_name") + "\t: ");
			
			String sync = "";
			if (Boolean.parseBoolean(mod.attribute("process")))
			{
				sync = "process ";
			}
			out.write(sync + mod.attribute("module_name"));
			
			printModuleInput(mod);
		}
	}
	
	private void printFsmModuleVar(QDomNodeList out_variables, QDomNodeList local_variables, QDomNodeList states) throws IOException
	{
		for (int i = 0; i < local_variables.size(); i++)
		{
			QDomElement var = local_variables.at(i).toElement().firstChildElement("Properties");
			out.write("\t\t" + var.attribute("name") + "\t: " + var.attribute("values") + ";\n");
		}
		
		for (int i = 0; i < out_variables.size(); i++)
		{
			QDomElement var = out_variables.at(i).toElement().firstChildElement("Properties");
			
			out.write("\t\t" + var.attribute("name") + "\t: " + var.attribute("values") + ";\n");
		}
		
		if (!states.isEmpty())
		{
			out.write("\t\tstate\t: {");
			
			for (int i = 0; i < states.size() - 1; i++)
			{
				QDomElement s = states.at(i).toElement().firstChildElement("Properties");
				out.write(s.attribute("name") + ", ");
			}
			out.write(states.at(states.size() - 1).toElement().firstChildElement("Properties").attribute("name") + "};\n");		
		}
	}
	
	private void printChoiceVar(QDomNodeList choice_vars) throws IOException
	{
		for (int i = 0; i < choice_vars.size(); i++)
		{
			out.write("\t\t" + choice_vars.at(i).toElement().attribute("name") + " : " + 
					  choice_vars.at(i).toElement().attribute("values") + ";\n");
		}
	}
	
	private void printAssignVar(QDomNodeList local_vars, QDomNodeList output_vars) throws IOException
	{
		for (int i = 0; i < local_vars.size(); i++)
		{
			QDomElement var = local_vars.at(i).toElement().firstChildElement("Properties");
			String init = var.attribute("initial");
			
			if (init.compareTo("") != 0)
			
				out.write("\t\tinit(" + var.attribute("name") + ")\t:= " + init + ";\n");
		}
		
		for (int i = 0; i < output_vars.size(); i++)
		{
			QDomElement var = output_vars.at(i).toElement().firstChildElement("Properties");
			String init = var.attribute("initial");
			
			if (init.compareTo("") != 0)
			
				out.write("\t\tinit(" + var.attribute("name") + ")\t:= " + init + ";\n");
		}
	}
	
	private void printFsmAssignVar(QDomNodeList out_vars, QDomNodeList local_vars, QDomNodeList choice_vars, QDomNodeList states, 
			                       QDomNodeList transitions, QDomNodeList nd_transitions) throws IOException
	{
		printInit(out_vars, local_vars, states);
		printInitChoiceVars(choice_vars);
		
		out.write("\n");
		
		printNext(transitions, nd_transitions, states);
		printNextChoiceVar(choice_vars);
	}
	
	private void printInit(QDomNodeList out_variables, QDomNodeList local_variables, QDomNodeList states) throws IOException
	{
		for (int i = 0; i < local_variables.size(); i++)
		{
			QDomElement var = local_variables.at(i).toElement().firstChildElement("Properties");
			String init = var.attribute("initial");
			
			if (init.compareTo("") != 0)
			{
				out.write("\t\tinit(" + var.attribute("name") + ")\t:= " + init + ";\n");
			}
		}
		for (int i = 0; i < out_variables.size(); i++)
		{
			QDomElement var = out_variables.at(i).toElement().firstChildElement("Properties");
			String init = var.attribute("initial");
			
			if (init.compareTo("") != 0)
			{
				out.write("\t\tinit(" + var.attribute("name") + ")\t:= " + init + ";\n");
			}
		}
		
		for (int i = 0; i < states.size(); i++)
		{
			QDomElement s = states.at(i).toElement().firstChildElement("Properties");
			
			if (s.attribute("initial").compareTo("true") == 0)
			{
				out.write("\t\tinit(state)\t:= " + s.attribute("name") + ";\n");
				i = states.size();
			}
		}
	}
	
	private void printInitChoiceVars(QDomNodeList choice_vars) throws IOException
	{
		for (int i = 0; i < choice_vars.size(); i++)
		{
			QDomElement var = choice_vars.at(i).toElement();
			
			if (var.attribute("initial").compareTo("") != 0)
			{
				out.write("\t\tinit(" + var.attribute("name") + ") := " + var.attribute("initial") + ";\n");
			}
		}
	}
	
	private void printNext(QDomNodeList transitions, QDomNodeList nd_transitions, QDomNodeList states) throws IOException
	{
		if (!transitions.isEmpty())
		{
			if (states.size() > 1 )
			{
				out.write("\t\tnext(state)\t:=\n");
				out.write("\t\t\tcase\n");
				
				printStateTransitions(transitions);
				printStateNoDetTransitions(nd_transitions);
				
				// FIX: Alessio Palmieri
				// change the default value from 1 to TRUE to fit new NUSMV version
				//out.write("\t\t\t\t1 : state;\n\t\t\tesac;\n\n");
				out.write("\t\t\t\tTRUE : state;\n\t\t\tesac;\n\n");
				
			}
			printVariableTransitions(transitions, nd_transitions, states);
		}
	}
	
	private void printNextChoiceVar(QDomNodeList choice_vars) throws IOException
	{
		for (int i = 0; i < choice_vars.size(); i++)
		{
			QDomElement var = choice_vars.at(i).toElement();
			QDomNodeList next_list = var.elementsByTagName("Next");
			
			out.write("\t\tnext(" + var.attribute("name") + ") :=\n\t\t\tcase\n");
			for (int j = 0; j < next_list.size(); j++)
			{
				QDomElement next = next_list.at(j).toElement();
				
				out.write("\t\t\t\tstate = " + next.attribute("start_state"));
				out.write(" & " + next.attribute("condition") + " : ");
				out.write(var.attribute("values") + ";\n");
			}
			// FIX: Alessio Palmieri
			// Changed the default value from 1 to TRUE to fit new NUSMV version
			out.write("\t\t\t\tTRUE : " + var.attribute("name") + ";\n");
			out.write("\t\t\tesac;\n\n");
		}
	}
	
	private void printStateTransitions(QDomNodeList transitions) throws IOException
	{
		List<List<String>> condition = saveConditions(transitions);
		
		for (int i = 0; i < condition.size(); i++)
		{
			List<String> list = condition.get(i);
			String cond = list.get(0);
			
			for (int j = 1; j < list.size();)
			{
				String tokens[] = list.get(j).split("start:");
				String start = tokens[tokens.length-1];
				out.write("\t\t\t\tstate = " + start + " & " + cond + " : ");
				if (list.size() > j+2 && list.get(j+2).contains("start:"))
				{
					out.write(list.get(j+1) + ";\n");
					j += 2;
				}
				else if (list.size() > j+2)
				{
					out.write("{" + list.get(j+1));
					int k = j+2;
					
					for (; list.size() > k && !list.get(k).contains("start:"); k++)
					{
						out.write(", " + list.get(k));
					}
					out.write("};\n");
					j = k;
				}
				else
				{
					out.write(list.get(j+1) + ";\n");
					j += 2;
				}
			}
		}
	}
	
	private List<List<String>> saveConditions(QDomNodeList transitions)
	{
		List<List<String>> conditions = new ArrayList<List<String>>(0);
		
		for (int i = 0; i < transitions.size(); i++)
		{
			QDomElement t = transitions.at(i).firstChildElement("Properties");
			boolean cond_exists = false;
			
			if (t.attribute("condition").compareTo("") != 0)
			{
				for (int j = 0; j < conditions.size(); j++)
				{	
					List<String> list = conditions.get(j);
					
					if (list.get(0).compareTo(t.attribute("condition")) == 0)
					{
						int index = -1;
						if ((index = list.indexOf("start:" + t.attribute("start_state"))) > 0)
						{
							list.add(index+1, t.attribute("end_state"));
						}
						else
						{
							list.add(1, "start:" + t.attribute("start_state"));
							list.add(2, t.attribute("end_state"));
						}
						j = conditions.size();
						cond_exists = true;
					}
				}
				if (!cond_exists)
				{
					List<String> list = new ArrayList<String>(0);
					
					list.add(0, t.attribute("condition"));
					list.add(1, "start:" + t.attribute("start_state"));
					list.add(2, t.attribute("end_state"));
					
					conditions.add(list);
				}
			}
		}		
		return conditions; 
	}
	
	private void printStateNoDetTransitions(QDomNodeList nd_transitions) throws IOException
	{
		for (int i = 0; i < nd_transitions.size(); i++)
		{
			QDomElement t = nd_transitions.at(i).toElement();
			
			out.write("\t\t\t\tstate = " + t.attribute("start_state")); 	
			out.write(" & " + t.attribute("condition"));
			out.write(" : " + t.attribute("end_state") + ";\n");
		}
	}
	
	private void printVariableTransitions(QDomNodeList transitions, QDomNodeList nd_transitions, QDomNodeList states) throws IOException
	{
		List<VariableTransition> vt = new ArrayList<VariableTransition>(0);
		List<VariableTransition> vtd = new ArrayList<VariableTransition>(0);
		
		for (int i = 0; i < transitions.size(); i++)
		{
			vt.addAll(getVariableTransition(transitions.at(i).toElement(), nd_transitions));
		}
		vtd.addAll(getDuringVariableTransition(states, transitions));
		
		for (int i = 0; i < vt.size();)
		{
			VariableTransition v = vt.get(0);
			String name = v.getName().trim();
			
			out.write("\t\tnext(" + name + ") := \n" );
			out.write("\t\t\tcase\n");
			out.write("\t\t\t\tstate = " + v.getStart_state());
			
			for (int k = 0; k < v.getConditions().length; k++)
			{
				out.write(" & " + v.getConditions()[k]);
			}
			out.write(" : " + v.getNext_value() + ";\n");
			vt.remove(v);
			
			printVariableNext(name, vt, vtd);
			
			// FIX: Alessio Palmieri to get compatibility with new version
			// 1 to NEW
			//out.write("\t\t\t\t1\t: " + name + ";\n\t\t\tesac;\n\n");
			out.write("\t\t\t\tTRUE\t: " + name + ";\n\t\t\tesac;\n\n");
		}
		if (!vtd.isEmpty())
		{
			for (int i = 0; i < vtd.size();)
			{
				VariableTransition v = vtd.get(i);
				String name = v.getName();
				
				out.write("\t\tnext(" + name + ") := \n" );
				out.write("\t\t\tcase\n");
								
				out.write("\t\t\t\tstate = " + v.getStart_state());
				
				for (int k = 0; k < v.getConditions().length; k++)
				{
					out.write(" & " + v.getConditions()[k]);
				}
				out.write(" : " + v.getNext_value() + ";\n");
				
				vtd.remove(v);
				printVariableNext(name, vt, vtd);
				
				out.write("\t\t\t\t1\t: " + name + ";\n\t\t\tesac;\n\n");
			}
		}
	}
	
	private void printVariableNext(String name, List<VariableTransition> vt, List<VariableTransition> vtd) throws IOException
	{
		VariableTransition v;
		
		for (int j = 0; j < vt.size(); j++)
		{
			if ((v = vt.get(j)).getName().compareTo(name) == 0)
			{
				out.write("\t\t\t\tstate = " + v.getStart_state());
				
				for (int k = 0; k < v.getConditions().length; k++)
				{
					out.write(" & " + v.getConditions()[k]);
				}
				
				out.write(" : " + v.getNext_value() + ";\n");
				vt.remove(v);
				j--;
			}
		}
		for (int j = 0; j < vtd.size(); j++)
		{
			if ((v = vtd.get(j)).getName().compareTo(name) == 0)
			{
				out.write("\t\t\t\tstate = " + v.getStart_state());
				
				for (int k = 0; k < v.getConditions().length; k++)
				{
					out.write(" & " + v.getConditions()[k]);
				}
				
				out.write(" : " + v.getNext_value() + ";\n");
				
				vtd.remove(v);
				j--;
			}
		}
	}
	
	private List<VariableTransition> getVariableTransition(QDomElement t, QDomNodeList nd_transitions)
	{
		List<VariableTransition> vt = new ArrayList<VariableTransition>(0);
		QDomNodeList actions = t.elementsByTagName("Action");
		
		for (int j = 0; j < actions.size(); j++)
		{
			String a = actions.at(j).toElement().attribute("string");
			if (a.contains("\n"))
			{
				String conds[] = a.split("\n");
				
				for (int i = 0; i < conds.length; i++)
				{
					String tokens[] = conds[i].split("=");
					String var = tokens[0];
					String next = tokens[1];
					
					QDomElement prop = t.firstChildElement("Properties");
					
					if (prop.attribute("condition").compareTo("") != 0)
					{
						VariableTransition v = new VariableTransition(var.trim(), prop.attribute("start_state"),
																  next, prop.attribute("condition").split(";"));
						vt.add(v);
					}
					else
					{
						for (int k = 0; k < nd_transitions.size(); k++)
						{
							QDomNodeList nd_actions = nd_transitions.at(k).toElement().elementsByTagName("Action");
							
							for (int kk = 0; kk < nd_actions.size(); kk++)
							{
								String nd_a = nd_actions.at(kk).toElement().attribute("string");
								
								if (nd_a.compareTo(a) == 0)
								{
									String condition[] = {nd_transitions.at(k).toElement().attribute("condition")};
									VariableTransition v = new VariableTransition(var.trim(), prop.attribute("start_state"),
											  next, condition);
									vt.add(v);
									kk = nd_actions.size();
									k = nd_transitions.size();
								}
							}
						}
					}					
				}
			}
			else
			{
				String tokens[] = a.split("=");
				String var = tokens[0];
				String next = tokens[1];
				
				QDomElement prop = t.firstChildElement("Properties");
				
				if (prop.attribute("condition").compareTo("") != 0)
				{
					VariableTransition v = new VariableTransition(var.trim(), prop.attribute("start_state"),
															  next, prop.attribute("condition").split(";"));
					vt.add(v);
				}
				else
				{
					for (int k = 0; k < nd_transitions.size(); k++)
					{
						QDomNodeList nd_actions = nd_transitions.at(k).toElement().elementsByTagName("Action");
						
						for (int kk = 0; kk < nd_actions.size(); kk++)
						{
							String nd_a = nd_actions.at(kk).toElement().attribute("string");
							
							if (nd_a.compareTo(a) == 0)
							{
								String condition[] = {nd_transitions.at(k).toElement().attribute("condition")};
								VariableTransition v = new VariableTransition(var.trim(), prop.attribute("start_state"),
										  next, condition);
								vt.add(v);
								kk = nd_actions.size();
								k = nd_transitions.size();
							}
						}
					}
				}					
			}
		}
		return vt;
	}
	
	private List<VariableTransition> getDuringVariableTransition(QDomNodeList states, QDomNodeList transitions)
	{
		List<VariableTransition> vt = new ArrayList<VariableTransition>(0);
		
		for (int i = 0; i < states.size(); i++)
		{
			QDomElement s = states.at(i).toElement();
			String d = s.firstChildElement("Properties").firstChildElement("During").attribute("action");
			if (d.compareTo("") != 0)
			{
				String name = s.firstChildElement("Properties").attribute("name");
				String conditions = "";
				
				for (int j = 0; j < transitions.size(); j++)
				{
					QDomElement p = transitions.at(j).firstChildElement("Properties");
					
					if (p.attribute("start_state").compareTo(name) == 0)
					{
						String tokens[] = p.attribute("condition").split(";");
						for (int k = 0; k < tokens.length; k++)
						{
							if (tokens[k].trim().charAt(0) != '!')
							
								conditions += "!(" + tokens[k] + ");";
							
							else
							{
								conditions = tokens[k].substring(1);
							}
						}
					}
				}
				String c[] = conditions.split(";");
				String tokens[] = d.split("=");
				String var = tokens[0];
				String next = tokens[1];
				
				VariableTransition v = new VariableTransition(var.trim(), s.firstChildElement("Properties").attribute("name"), next, c);
				
				vt.add(v);
			}
		}
		return vt;
	}
	
	private void printInputVars(QDomNodeList input_vars) throws IOException
	{
		if (input_vars.isEmpty())
		{
			out.write("()\n");
		}
		else
		{
			out.write("(" + input_vars.item(0).firstChildElement("Properties").attribute("name"));
			
			for (int i = 1; i < input_vars.length(); i++)
			{
				out.write(", " + input_vars.item(i).firstChildElement("Properties").attribute("name"));
			}
			out.write(")\n");
		}
	}
	
	private void printModuleInput(QDomElement module) throws IOException
	{
		out.write("(");
		
		QDomNodeList inputs = module.elementsByTagName("Input");
		
		if (!inputs.isEmpty())
		{	
			for (int i = 0; i < inputs.size() - 1; i++)
			{
				if(printInputType(inputs.at(i).firstChildElement("OutputInfo")).compareTo("")==0){
					out.write(inputs.at(i).firstChildElement("Variable").attribute("name") + ", ");
				}else{
					out.write(printInputType(inputs.at(i).firstChildElement("OutputInfo")) + ", ");
				}
			}
			if(printInputType(inputs.at(inputs.size() - 1).firstChildElement("OutputInfo")).compareTo("")==0){
				out.write(inputs.at(inputs.size() - 1).firstChildElement("Variable").attribute("name"));	
			}else{
				out.write(printInputType(inputs.at(inputs.size() - 1).firstChildElement("OutputInfo")));
			}
			
		}
		out.write(");\n");
		}
	
	private String printInputType(QDomElement out_info)
	{
		if (out_info.attribute("from").compareTo("module") == 0)
		{
			return out_info.attribute("instance_name") + "." + out_info.attribute("var_name");
		}
		else
		{
			return out_info.attribute("var_name");
		}
	}
	
	private void printSpecifications(QDomNodeList spec_list) throws IOException
	{
		out.write("\n");
		
		for (int i = 0; i < spec_list.size(); i++)
		{
			QDomElement spec = spec_list.at(i).toElement();
			
			out.write(spec.attribute("type") + "SPEC " + spec.attribute("formula") + "\n");
		}
	}
	
	private void printFairness(QDomNodeList fairness_list) throws IOException
	{
		for (int i = 0; i < fairness_list.size(); i++)
		{
			QDomElement fairness = fairness_list.item(i).toElement();
			
			out.write("\nFAIRNESS\n\t" + fairness.attribute("constraint"));
		}
	}
}
