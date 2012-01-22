package apps;

import com.trolltech.qt.gui.QIcon;

public class Icon
{
	private static final String icon_path = "src/pixmap/";
	
	public static QIcon delete()
	{
		return new QIcon(icon_path + "delete.png");
	}
	
	public static QIcon fsmModule()
	{
		return new QIcon(icon_path + "fsm_module.png");
	}
	
	public static QIcon frameModule()
	{
		return new QIcon(icon_path + "frame_module.png");
	}
	
	public static QIcon nusmv()
	{
		return new QIcon(icon_path + "nusmv.gif");
	}
	
	public static QIcon magnifier(){
		return new QIcon(icon_path + "magnifier.png");
	}
}
