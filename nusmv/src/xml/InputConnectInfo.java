package xml;

import com.trolltech.qt.xml.QDomElement;

import view.InputVariableHookView;

public class InputConnectInfo
{
	private InputVariableHookView in;
	private QDomElement out_object;
	
	public InputConnectInfo(InputVariableHookView in, QDomElement out)
	{
		this.in = in;
		this.out_object = out;
	}
	
	public InputVariableHookView getInputHookView()
	{
		return in;
	}
	
	public QDomElement getOutObject()
	{
		return out_object;
	}
}
