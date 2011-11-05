package dialog;

import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QPushButton;

public class ExitConformDialog extends QMessageBox
{
	public ExitConformDialog()
	{
		super();
		setWindowTitle("Confirm Exit");
		setWindowIcon(apps.Icon.nusmv());
		setText("Do you really want to exit?");
		setIconPixmap(new QPixmap("src/pixmap/question.png"));
		QPushButton ok = new QPushButton("Ok");
		QPushButton cancel = new QPushButton("Cancel");
		
		addButton(ok, ButtonRole.AcceptRole);
		addButton(cancel, ButtonRole.RejectRole);
	}
}
