/********************************************************************************
*                                                                               *
*   Module      :   ModuleListView.java                                         *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import model.FrameModule;
import model.FsmModule;
import model.Module;
import model.ModulesList;
import apps.Icon;

import com.trolltech.qt.core.Qt.ItemFlag;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QListWidget;
import com.trolltech.qt.gui.QListWidgetItem;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QItemSelectionModel.SelectionFlag;
import command.RemoveModuleCommand;

import dialog.ModuleRenameDialog;

/**
 * Vista della lista dei moduli.
 * @author Silvia Lorenzini
 *
 */
public class ModuleListView extends QListWidget
{
	private ModulesList module_list;
	private FrameModuleWindowView frame_view;
	private QMenu item_menu;
	private QMenu menu;
	private QAction delete;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 * @param list lista dei moduli definiti.
	 * @param view finestra del modulo Frame padre.
	 */
	public ModuleListView(ModulesList list, FrameModuleWindowView view)
	{
		this.module_list = list;
		this.frame_view = view;
		createItems();
		createMenu();
		connectSignals();
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	protected void frameModuleAdded(FrameModule m)
	{
		QListWidgetItem item = new QListWidgetItem(Icon.frameModule(), m.getName());
		addItem(item);
	}
	
	protected void fsmModuleAdded(FsmModule m)
	{
		QListWidgetItem item = new QListWidgetItem(Icon.fsmModule(), m.getName());
		addItem(item);
	}
	
	protected void moduleRemoved(int index)
	{
		takeItem(index);
	}
	
	protected void removeAll()
	{
		while (count() > 0)
		{
			takeItem(0);
		}
	}
	
	/**
	 * Modifica il nome di un modulo data la posizione
	 * @param index posizione del modulo.
	 * @param name nuovo nome del modulo.
	 */
	protected void moduleNameChanged(int index, String name)
	{
		QListWidgetItem item = item(index);
		item.setText(name);
	}
	
	/**
	 * Crea un nuovo modulo Frame e le relative viste.
	 */
	protected void createNewFrameModule()
	{
		FrameModule m = new FrameModule();
		new FrameModuleWindowView(m, frame_view.getProjectTree());
		new FrameModuleTreeView(m, frame_view.getProjectTree());
		module_list.addFrameModule(m);
		m.added.emit();
	}
	
	/**
	 * Crea un nuovo modulo Fsm e le relative viste.
	 */
	protected void createNewFsmModule()
	{
		FsmModule m = new FsmModule();
		new FsmModuleWindowView(m, frame_view.getProjectTree());
		new FsmModuleTreeView(m, frame_view.getProjectTree());
		module_list.addFsmModule(m);
		m.added.emit();
	}
	
	protected void removeModule()
	{
		Module m = module_list.getModule(currentIndex().row());
		
		frame_view.getView().getUndoStack().push(new RemoveModuleCommand(m));
	}
	
	protected void renameModule()
	{
		new ModuleRenameDialog(frame_view.getView(), module_list.getModule(currentIndex().row())).exec();
	}
	
	/**
	 * Crea un istanza di un modulo.
	 * Comando da menu-item selezionato.
	 */
	protected void createInstance()
	{
		Module m = module_list.getModule(currentIndex().row());
		
		if (m.getClass().getName().compareTo("model.FrameModule") == 0)
		{
			frame_view.addingFrameModule();
			QCursor c = new QCursor(new QPixmap("src/pixmap/cursor_add_frame.png"));
			frame_view.getView().setAddObjectCursor(c);
		}
		else
		{
			frame_view.addingFsmModule();
			QCursor c = new QCursor(new QPixmap("src/pixmap/cursor_add_fsm.png"));
			frame_view.getView().setAddObjectCursor(c);
		}
		frame_view.addDuplicate();
		frame_view.setModuleToInstance(m);
	}
	
	/**
	 * Crea un nuovo modulo copiando quello selezionato.
	 */
	protected void createCopy()
	{
		Module m = module_list.getModule(currentIndex().row());
		Module copied = m.copy(frame_view.getProjectTree(), null);
		copied.added.emit();
	}
	
	@Override
	protected void mousePressEvent(QMouseEvent event)
	{
		if (itemAt(event.pos()) == null && currentItem() != null)
		{
			currentItem().setSelected(false);
			update();
		}
		if (event.button() == MouseButton.RightButton)
		{
			if (itemAt(event.pos()) == null)
			{
				menu.exec(event.globalPos());
			}
			else
			{
				QListWidgetItem item = itemAt(event.pos());
				setCurrentItem(item, SelectionFlag.SelectCurrent);
				update();
				
				if (currentItem().text().compareTo(frame_view.getModule().getName()) == 0)
				{
					delete.setEnabled(false);
				}
				else
				{
					delete.setEnabled(true);
				}
				item_menu.exec(event.globalPos());
			}
		}
		super.mousePressEvent(event);
	}
	
	@Override
	protected void mouseDoubleClickEvent(QMouseEvent event)
	{
		if (event.button() == MouseButton.LeftButton && itemAt(event.pos()) != null)
		{
			Module m = module_list.getModule(currentItem().text());
			if (m != null)
			{
				m.show_window.emit();
			}
		}
		super.mouseDoubleClickEvent(event);
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	private void createMenu()
	{
		menu = new QMenu();
		
		QAction add_frame = new QAction("create new Frame module", menu);
		add_frame.setIcon(Icon.frameModule());
		add_frame.triggered.connect(this, "createNewFrameModule()");
		
		QAction add_fsm = new QAction("create new FSM module", menu);
		add_fsm.triggered.connect(this, "createNewFsmModule()");
		add_fsm.setIcon(Icon.fsmModule());
		
		menu.addAction(add_frame);
		menu.addAction(add_fsm);
		
		item_menu = new QMenu();
		
		delete = new QAction("Remove", item_menu);
		delete.triggered.connect(this, "removeModule()");
		delete.setIcon(Icon.delete());
		
		QAction instance = new QAction("Instance", item_menu);
		instance.triggered.connect(this, "createInstance()");
		
		QAction copy = new QAction("Copy", item_menu);
		copy.triggered.connect(this, "createCopy()");
		
		QAction rename = new QAction("Rename", menu);
		rename.triggered.connect(this, "renameModule()");
		
		item_menu.addAction(rename);
		item_menu.addSeparator();
		item_menu.addAction(instance);
		item_menu.addAction(copy);
		item_menu.addSeparator();
		item_menu.addAction(delete);
	}
	
	private void createItems()
	{
		for (int i = 0; i < module_list.size(); i++)
		{
			Module m = module_list.getModule(i);
			if (m.getClass().getName().compareTo("model.FrameModule") == 0)
			{
				QListWidgetItem item = new QListWidgetItem(Icon.frameModule(), m.getName());
				if (m.getName().compareTo("main") == 0)
				{
					item.setFlags(ItemFlag.NoItemFlags);
				}
				addItem(item);
			}
			else
			{
				QListWidgetItem item = new QListWidgetItem(Icon.fsmModule(), m.getName());
				addItem(item);
			}
		}
	}
	
	private void connectSignals()
	{
		module_list.frame_module_added.connect(this, "frameModuleAdded(FrameModule)");
		module_list.fsm_module_added.connect(this, "fsmModuleAdded(FsmModule)");
		module_list.module_removed.connect(this, "moduleRemoved(int)");
		module_list.module_name_changed.connect(this, "moduleNameChanged(int, String)");
		module_list.remove_all.connect(this, "removeAll()");
	}
}
