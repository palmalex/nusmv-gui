/********************************************************************************
*                                                                               *
*   Module      :   InputVariableHookView.java                                  *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicHookText;
import item.GraphicLine;
import item.GraphicView;
import item.TriangleIn;
import model.InputVariable;
import model.ModuleInstance;
import model.Variable;
import xml.XmlCreator;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import command.RemoveInputVariableCommand;

import dialog.RenameInputVariableDialog;

/**
 * Vista (triangolino verso l'interno del rettangolo) della variabile di ingresso di 
 * un modulo per l'aggancio nel passaggio di variaibli.
 * @author Silvia Lorenzini
 *
 */
public class InputVariableHookView extends TriangleIn
{
	private QMenu menu;
	private InputVariable in;
	private GraphicLine entering_line;
	
	public Signal1<InputVariableHookView> hook_added;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 * @param g_instance vista grafica della variabile di istanza che contiene il triangolino.
	 * @param iv variabile di ingresso relativa alla vista.
	 * @param x coordinata x di posizione. 
	 * @param y coordinata y di posizione.
	 * @param view vista della scena grafica.
	 */
	public InputVariableHookView(ModuleInstanceGraphicView g_instance, InputVariable iv, int x, int y, GraphicView view)
	{
		super(g_instance, view.getMousePos().x(), view.getMousePos().y(), 8, 8, view);
		this.in = iv;
		this.entering_line = null;
		this.text = null;
		createMenu();
		connectSignals(g_instance);
	}
	
	/**
	 * Costruttore.
	 * @param in variabile di ingresso relativa alla vista.
	 * @param g_instance vista grafica della variabile di istanza che contiene il triangolino.
	 * @param hook altra vista gancio.
	 */
	public InputVariableHookView(InputVariable in, ModuleInstanceGraphicView g_instance, InputVariableHookView hook)
	{
		super(g_instance, hook);
		this.in = in;
		this.entering_line = null;
		this.text = null;
		createMenu();
		setText();
		
		connectSignals(g_instance);
	}
	
	/**
	 * Costruttore.
	 * @param g_instance vista grafica della variabile di istanza che contiene il triangolino.
	 * @param iv variabile di ingresso relativa alla vista.
	 * @param x coordinata x di posizione. 
	 * @param y coordinata y di posizione. 
	 * @param view vista della scena grafica.
	 * @param orientation stringa con l'orientazione del triangolo ("to_up", "to_down", "to_left" oppure "to_right").
	 */
	public InputVariableHookView(ModuleInstanceGraphicView g_instance, InputVariable iv, int x, int y, GraphicView view, String orientation)
	{
		super(g_instance, x, y, 8, 8, view, orientation);
		this.in = iv;
		this.entering_line = null;
		this.text = null;
		createMenu();
		setText();
		connectSignals(g_instance);		
	}
	
	public Variable getInputVariable()
	{
		return in;
	}
	
	/**
	 * Imposta la linea di collegamento alla variabile e connette i segnali.
	 * @param line la linea da collegare.
	 */
	public void addEnteringLine(GraphicLine line)
	{
		if (entering_line != null)
		{
			line.removed.emit(line, null);
		}
		else
		{
			entering_line = line;
			entering_line.removed.connect(this, "removeEnteringLine(GraphicLine, Object)");
			entering_line.added.connect(this, "addEnteringLine(GraphicLine, Object)");
		}		
	}
	
	/**
	 * Rimuove la linea in ingresso.
	 */
	public void removeEnteringLine()
	{
		entering_line = null;
	}
	
	/**
	 Imposta la linea di collegamento alla variabile e connette i segnali.
	 * @param line la linea da collegare.
	 */
	public void setEnteringLine(GraphicLine line)
	{
		entering_line = line;
		QPoint p = getHookPoint();
		entering_line.setEndPoint(p.x(), p.y());
	}
	
	public GraphicLine getEnteringLine()
	{
		return entering_line;
	}
	
	public QMenu getMenu()
	{
		return menu;
	}
	
	/**
	 * Quando il mouse rilascia la linea sopra il triangolo viene fornito il punto di aggancio (la punta)
	 * @return le coordinate della punta del triangolo cui attaccare la linea.
	 */
	public QPoint getHookPoint()
	{
		if (to_up)
		{
			return new QPoint(x + (int)parentItem().x() + width/2, y + (int)parentItem().y() + height - 3);
		}
		if(to_down)
		{
			return new QPoint(x + (int)parentItem().x() + width/2, y + (int)parentItem().y() +3);
		}
		if(to_left)
		{
			return new QPoint(x + (int)parentItem().x() + width - 3, y + (int)parentItem().y() + height/2);
		}
		if(to_right)
		{
			return new QPoint(x + (int)parentItem().x() + 3, y + (int)parentItem().y() + height / 2);
		}
		return null;
	}
	
	public void getHookPoint(QPoint p)
	{
		p = getHookPoint();
	}
	
	public GraphicView getView()
	{
		return view;
	}

	/**
	 * Imposta il testo relativo al nome della variaible di ingresso e lo posiziona all'interno del rettangolo,
	 * accanto al triangolo e con il giusto orientamento.
	 */
	@Override
	public void setText()
	{
		if (text == null)
		{	
			text = new GraphicHookText(this, in.getName());
			orientation_changed.connect(text, "orientationChanged(String)");
		}
		else
		{
			text.setPlainText(in.getName());
			if (to_left)
				text.parentMoving();
		}
	}
	
	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Fornisce le informazioni di layout in fase di salvataggio.
	 * @param xml oggetto che si occupa di creare l'albero xml per il salvataggio del modello.
	 */
	protected void getViewInfo(XmlCreator xml)
	{
		xml.setViewObject(this);
	}
	
	/**
	 * Aggiorna la vista a seguito di cambiamenti del modello.
	 */
	protected void propertiesChanged()
	{
		setText();
	}
	
	/**
	 * Inserisce nello stack dei comandi undo/redo il comando relativo all'eliminazione di questa vista.
	 */
	protected void delete()
	{
		view.getUndoStack().push(new RemoveInputVariableCommand(in));
	}
	
	/**
	 * A seguito della duplicazione del modello di variabile di ingresso viene richiesto di duplicare
	 * la vista stessa.
	 * @param parent variabile di istanza duplicata e padre della duplicazione di questa vista.
	 * @param original_instance variabile di istanza originale.
	 */
	protected void toDuplicate(ModuleInstanceGraphicView parent, ModuleInstance original_instance)
	{
		if (original_instance.equals(((ModuleInstanceGraphicView)this.parent).getInstance()))
		{
			InputVariableHookView in = new InputVariableHookView((InputVariable)this.getInputVariable(), parent, this);
			in.parentResized();
		}
	}
	
	/**
	 * A seguito della copia del modello di variabile di ingresso viene richiesto di copiare
	 * la vista stessa.
	 * @param v variabile copiata.
	 * @param m_view vista copiata della variaible di istanza, che conterrà lacopia di questa vista.
	 */
	protected void toCopy(InputVariable v, ModuleInstanceGraphicView m_view)
	{
		InputVariableHookView in = new InputVariableHookView(v, m_view, this);
		in.parentResized();
	}
	
	/**
	 * Esegue il dialog per il cambiamento del nome della variabile di ingresso a seguito della selezione
	 * dell'azione dal menu.
	 */
	protected void rename()
	{
		new RenameInputVariableDialog(in, view.getUndoStack()).exec();
	}
	
	/**
	 * Se il rettangolo contenente questa vista viene spostato, anche la linea entrante (se esiste) deve muoversi.
	 * @param pos posizione del mouse.
	 * @param last_pos posizione precedente del mouse.
	 */
	protected void parentMoved(QPointF pos, QPointF last_pos)
	{
		double gapx = pos.x() - last_pos.x();
		double gapy = pos.y() - last_pos.y();
		
		if (entering_line != null)
		{
			entering_line.setEndPoint(entering_line.ex() + (int)gapx, entering_line.ey() + (int)gapy);
			scene().update();
		}
	}
	
	/**
	 * Se il rettangolo contenente questa vista viene scalalto, la linea entrante (se esiste) deve muoversi.
	 */
	protected void parentScaled()
	{
		if (entering_line != null)
			
			entering_line.setEndPoint(getHookPoint().x(), getHookPoint().y());
	}
	
	/**
	 * Rimuove la linea si ingresso a seguito di una cancellazione della variabile di partenza.
	 * @param line linea da eliminare.
	 * @param obj oggetto rappresentante la vista grafica della variabile di partenza per il passaggio, che è
	 * stata cancallata.
	 */
	protected void removeEnteringLine(GraphicLine line, Object obj)
	{
		if (line == entering_line && (obj == null || !obj.equals(this)))
			
			entering_line = null;
	}
	
	/**
	 * Imposta la linea del collegamento dall'oggetto obj.
	 * @param line linea entrante.
	 * @param obj oggeto da cui parte la linea.
	 */
	protected void addEnteringLine(GraphicLine line, Object obj)
	{
		entering_line = line;
	}

	/**
	 * Muove la linea.
	 */
	@Override
	protected void moveLines()
	{
		if (entering_line != null)
		{
			QPoint p = getHookPoint();
			entering_line.setEndPoint(p.x(), p.y());
		}
	}
	
	/**
	 * Emette il segnale di aggiunta di questa vista gancio. 
	 */
	@Override
	protected void triangleAdded()
	{
		((ModuleInstanceGraphicView)parent).getInstance().getInstancedModule().in_hook_added.emit(this, ((ModuleInstanceGraphicView)parent));
	}
	
	/**
	 * Se questa vista viene eliminata ed esiste una linea di collegamento emmette i dovuti segnali di notifica.
	 */
	@Override
	protected void removed()
	{
		if (entering_line != null)
		{	
			entering_line.removed.emit(entering_line, this);
			view.getScene().update();
		}
		super.removed();
	}
	
	/**
	 * Se questa vista viene aggiunta nuovamente ed esisteva una linea di collegamento 
	 * emmette i dovuti segnali di notifica.
	 */
	@Override
	protected void added()
	{
		if (entering_line != null)
		{	
			entering_line.added.emit(entering_line, this);
			view.getScene().update();
		}
		super.added();
	}
	
	@Override
	protected void showMenu()
	{
		menu.exec(QCursor.pos());		
	}
	
	/********************************************************************************
	*                                                                               *
	*  						    PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	private void connectSignals(ModuleInstanceGraphicView g_instance)
	{
		in.added.connect(this, "added()");
		in.removed.connect(this, "removed()");
		in.properties_changed.connect(this, "propertiesChanged()");
		in.to_duplicate.connect(this, "toDuplicate(ModuleInstanceGraphicView, ModuleInstance)");
		in.copy_hook.connect(this, "toCopy(InputVariable, ModuleInstanceGraphicView)");
		in.getModule().removed.connect(in.removed);
		in.getModule().added.connect(in.added);
		in.need_hook_view_info.connect(this, "getViewInfo(XmlCreator)");
		in.get_hook_point.connect(this, "getHookPoint(QPoint)");
		if (g_instance != null)
		{
			g_instance.moved.connect(this, "parentMoved(QPointF, QPointF)");
			g_instance.scaled.connect(this, "parentScaled()");
			g_instance.getInstance().removed.connect(this, "removed()");
			g_instance.getInstance().added.connect(this, "added()");
		}
	}
	
	private void createMenu()
	{
		menu = new QMenu();
		
		QAction delete = new QAction(tr("Delete"), menu);
		delete.setIcon(new QIcon("src/pixmap/delete.png"));
		delete.triggered.connect(this, "delete()");
		
		QAction rename = new QAction(tr("Rename"), menu);
		rename.triggered.connect(this, "rename()");
		
		QAction move = new QAction("Move", menu);
		move.triggered.connect(this, "moveHook()");
		
		menu.addAction(delete);
		menu.addAction(rename);
		menu.addSeparator();
		menu.addAction(move);
	}
}
