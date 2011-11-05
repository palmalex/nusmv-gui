package dialog;

import view.FrameModuleWindowView;

import com.trolltech.qt.core.Qt.TextFormat;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QPushButton;

public class CloseModifiedDialog extends QMessageBox
{
	private FrameModuleWindowView main_view;
	private boolean is_accepted;
		
	public CloseModifiedDialog(FrameModuleWindowView main_view)
	{
		this.main_view = main_view;
		this.is_accepted = false;
		setTextFormat(TextFormat.RichText);
		setText("The model has been modified.");
		setInformativeText("Do you want to save your changes before load another model?");
		setIconPixmap(new QPixmap("src/pixmap/warning.png"));
		
		QPushButton discard = new QPushButton("Don't save");
		discard.clicked.connect(this, "discard()");		
		addButton(discard, ButtonRole.ActionRole);
		
		QPushButton save = new QPushButton(new QIcon("src/pixmap/filesave.png"), "Save");
		save.clicked.connect(this, "save()");
		addButton(save, ButtonRole.ActionRole);
		
		QPushButton cancel = new QPushButton(new QIcon("src/pixmap/delete.png"), "Cancel");
		cancel.clicked.connect(this, "close()");		
		addButton(cancel, ButtonRole.ActionRole);
	}
	
	protected void save()
	{
		main_view.getModule().save();
		is_accepted = true;
		
	}
	
	protected void discard()
	{
		is_accepted = true;
	}
	
	public boolean isAccepted()
	{
		return is_accepted;
	}
}
