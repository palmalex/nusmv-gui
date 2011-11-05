package xml;

public class VariableTransition 
{
	private String name;
	private String start_state;
	private String next_value;
	private String[] conditions;
	
	public VariableTransition(String name, String start_state, String next_value, String[] conditions)
	{
		this.name = name;
		this.start_state = start_state;
		this.next_value = next_value;
		this.conditions = conditions;
	}

	public String[] getConditions() 
	{
		return conditions;
	}

	public void setConditions(String[] conditions) 
	{
		this.conditions = conditions;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

	public String getNext_value() 
	{
		return next_value;
	}

	public void setNext_value(String next_value) 
	{
		this.next_value = next_value;
	}

	public String getStart_state() 
	{
		return start_state;
	}

	public void setStart_state(String start_state) 
	{
		this.start_state = start_state;
	}	
}
