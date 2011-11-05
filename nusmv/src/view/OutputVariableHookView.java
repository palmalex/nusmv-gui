/********************************************************************************
*                                                                               *
*   Module      :   OutputVariableHookView.java                                 *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicHookText;
import item.GraphicLine;
import item.GraphicView;
import item.TriangleOut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.ModuleInstance;
import model.OutputVariable;
import model.Variable;
import xml.XmlCreator;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QGraphicsSceneMouseEvent;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QMenu;
import command.RemoveOutputVariableCommand;

import dialog.VariableOptionsDialog;

/**
 * Vista (triangolino verso l'esterno del rettangolo) della variabile di uscita di 
 * un modulo per l'aggancio nel passaggio di variaibli.
 * @author Silvia Lorenzini
 *
 */
public class OutputVariableHookView extends TriangleOut
{
	private QMenu menu;
	private OutputVariable out;
	private List<GraphicLine> exit_lines;
	private ModuleInstanceGraphicView g_instance;

	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 * @param g_instance vista grafica della variabile di istanza che contiene il triangolino.
	 * @param ov variabile di uscita relativa alla vista.
	 * @param x coordinata x di posizione. 
	 * @param y coordinata y di posizione.
	 * @param view vista della scena grafica.
	 */
	public OutputVariableHookView(ModuleInstanceGraphicView g_instance, OutputVariable ov, int x, int y, GraphicView view)
	{
		super(g_instance, x, y, 8, 8, view);
		
		this.g_instance = g_instance;
		this.out = ov;
		this.exit_lines = new ArrayList<GraphicLine>(0);
		this.text = null;
		createMenu();
		
		connectSignals(g_instance);
	}
	
	/**
	 * Costruttore.
	 * @param g_instance vista grafica della variabile di istanza che contiene il triangolino.
	 * @param ov variabile di uscita relativa alla vista.
	 * @param x coordinata x di posizione. 
	 * @param y coordinata y di posizione. 
	 * @param view vista della scena grafica.
	 * @param orientation stringa con l'orientazione del triangolo ("to_up", "to_down", "to_left" oppure "to_right").
	 */
	public OutputVariableHookView(ModuleInstanceGraphicView g_instance, OutputVariable ov, int x, int y, GraphicView view, String orientation)
	{
		super(g_instance, x, y, 8, 8, view, orientation);
		
		this.g_instance = g_instance;
		this.out = ov;
		this.exit_lines = new ArrayList<GraphicLine>(0);
		createMenu();
		setText();
		
		connectSignals(g_instance);
	}

	/**
	 * Costruttore.
	 * @param v variabile di uscita relativa alla vista.
	 * @param g_instance vista grafica della variabile di istanza che contiene il triangolino.
	 * @param hook altra vista gancio.
	 */
	public OutputVariableHookView(OutputVariable v, ModuleInstanceGraphicView g_instance, OutputVariableHookView hook)
	{
		super(g_instance, hook);
		
		this.g_instance = g_instance;
		this.out = v;
		this.exit_lines = new ArrayList<GraphicLine>(0);
		this.text = null;
		setText();
		createMenu();
		
		connectSignals(g_instance);
	}
	
	public Variable getOutputVariable()
	{
		return out;
	}
	
	/**
	 * Premendo il mouse sul triangolo relativo a questa vista viene aggiunta una linea di uscita
	 * per il passaggio della variabile.
	 */
	@Override
	public void mousePressEvent(QGraphicsSceneMouseEvent e)
	{
		if (e.button() == MouseButton.LeftButton)
		{
			QPoint p = getHookPoint();
			GraphicLine line = new GraphicLine(p.x(), p.y(), this, view);
			
			line.removed.connect(this, "lineDeleted(GraphicLine, Object)");
			line.added.connect(this, "lineAdded(GraphicLine, Object)");
			exit_lines.add(line);
		}
		super.mousePressEvent(e);
	}
	
	/**
	 * Aggiunge una linea di collegamento alla lista.
	 * @param line linea per il collegamento da aggiungere.
	 */
	public void addExitLine(GraphicLine line)
	{
		QPoint p = getHookPoint();
		line.setStartPoint(p.x(), p.y());
		exit_lines.add(line);
	}
	
	public void removeExitLine(GraphicLine line)
	{
		exit_lines.remove(line);
	}
	
	public QMenu getMenu()
	{
		return menu;
	}
	
	/**
	 * Imposta il testo relativo al nome della variaible di uscita e lo posiziona all'interno del rettangolo,
	 * accanto al triangolo e con il giusto orientamento.
	 */
	public void setText()
	{
		if (text == null)
		{	
			text = new GraphicHookText(this, out.getName());
			orientation_changed.connect(text, "orientationChanged(String)");
		}
		else
		{
			text.setPlainText(out.getName());
			if (to_left)
				text.parentMoving();
		}
	}
	
	/**
	 * Quando viene premuto il mouse sopra il triangolo viene fornito il punto di aggancio (la punta).
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
			return new QPoint(x + (int)parentItem().x() + width/2, y + (int)parentItem().y() + 3);
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
	
	/**
	 * Dato un gancio di una variabile di ingresso viene creata la linea di collegamento.
	 * @param in vista gancio della variabile di ingresso a cui passare la variaible relativa a 
	 * questa vista.
	 */
	public void createExitLine(InputVariableHookView in)
	{
		QPoint start = getHookPoint();
		QPoint end = in.getHookPoint();
		GraphicLine line = new GraphicLine(start.x(), start.y(), end.x(), end.y(), this, in, view);
		line.removed.connect(this, "lineDeleted(GraphicLine, Object)");
		line.added.connect(this, "lineAdded(GraphicLine, Object)");
		
		if (!exit_lines.contains(line))
		
			exit_lines.add(line);
		
		in.addEnteringLine(line);
	}

	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Esegue il dialog per l'inserimento delle opzioni della variabile di uscita.	
	 */
	protected void edit()
	{
		new VariableOptionsDialog(out).exec();
	}
	
	/**
	 * Inserisce nello stack dei comandi undo/redo il comando relativo all'eliminazione di questa vista.
	 */
	protected void delete()
	{
		view.getUndoStack().push(new RemoveOutputVariableCommand(out));
	}
	
	/**
	 * A seguito della duplicazione del modello di variabile di uscita viene richiesto di duplicare
	 * la vista stessa.
	 * @param parent variabile di istanza duplicata e padre della duplicazione di questa vista.
	 * @param original_instance variabile di istanza originale.
	 */
	protected void toDuplicate(ModuleInstanceGraphicView parent, ModuleInstance original_instance)
	{
		if (original_instance.equals(((ModuleInstanceGraphicView)this.parent).getInstance()))
		{
			OutputVariableHookView out = new OutputVariableHookView((OutputVariable)getOutputVariable(), parent, this);
			out.parentResized();
		}
	}
	
	/**
	 * A seguito della copia del modello di variabile di uscita viene richiesto di copiare
	 * la vista stessa.
	 * @param v variabile copiata.
	 * @param m_view vista copiata della variaible di istanza, che conterrà la copia di questa vista.
	 */
	protected void toCopy(OutputVariable v, ModuleInstanceGraphicView m_view)
	{
		OutputVariableHookView out = new OutputVariableHookView(v, m_view, this);
		out.parentResized();
	}
	
	/**
	 * Aggiorna la vista a seguito di cambiamenti del modello e se la variabile è collegata a qualche 
	 * gancio di ingresso modifica anche le informazioni di questa.
	 */
	protected void propertiesChanged()
	{
		text.setPlainText(out.getName());
		if (to_left)
			text.parentMoving();
		
		Iterator<GraphicLine> it = exit_lines.iterator();
		
		while (it.hasNext())
		{
			GraphicLine l = it.next();
			
			assert(l.getEndObject().getClass().getName().compareTo("view.InputVariableHookView") == 0);
			
			Variable in = ((InputVariableHookView)l.getEndObject()).getInputVariable();
			in.edit(in.getName(), out.getType(), out.getValues(), out.getInitial_value());
			in.properties_changed.emit();
		}
	}

	/**
	 * Elimina una linea di uscita dalla lista se il gancio di ingresso è stato cancellato.
	 * @param line linea da eliminare
	 * @param obj gancio di ingresso (eliminato).
	 */
	protected void lineDeleted(GraphicLine line, Object obj)
	{
		if (obj == null || !obj.equals(this))
			
			exit_lines.remove(line);
	}
	
	/**
	 * Aggiunge nuovamente una linea di uscita precedentemente cancellata.
	 * @param line linea da aggiungere
	 * @param obj oggetto precedentemente cancellato e poi ripristinato.
	 */
	protected void lineAdded(GraphicLine line, Object obj)
	{
		if ((obj == null || !obj.equals(this)) && !exit_lines.contains(line))
			
			exit_lines.add(line);
	}
	
	/**
	 * Se il rettangolo contenente questa vista viene spostato, anche le linee uscenti (se esiste) devono muoversi.
	 * @param pos posizione del mouse.
	 * @param last_pos posizione precedente del mouse.
	 */
	protected void parentMoved(QPointF pos, QPointF last_pos)
	{
		double gapx = pos.x() - last_pos.x();
		double gapy = pos.y() - last_pos.y();
		
		Iterator<GraphicLine> it = exit_lines.iterator();
		
		while (it.hasNext())
		{
			GraphicLine line = it.next();
			line.setStartPoint(line.sx() + (int)gapx, line.sy() + (int)gapy);
		}
		scene().update();
	}
	
	/**
	 * Se il rettangolo contenente questa vista viene scalalto, la linea entrante (se esiste) deve muoversi.
	 */
	protected void parentScaled()
	{
		Iterator<GraphicLine> it = exit_lines.iterator();
		
		while (it.hasNext())
		{
			GraphicLine line = it.next();
			line.setStartPoint(getHookPoint().x(), getHookPoint().y());
		}
		scene().update();
	}
	
	/**
	 * Dato un gancio, se instance coincide con la lvariaible di istanza di questa vista 
	 * allora crea la linea di collegamento.	
	 * @param in gancio entrante
	 * @param instance istanza di modulo da cui parte il collegamento. 
	 */
	protected void createExitLine(InputVariableHookView in, ModuleInstance instance)
	{
		if (g_instance.getInstance().equals(instance))
		{
			createExitLine(in);
		}
	}
	
	/**
	 * Fornisce le informazioni di layout in fase di salvataggio.
	 * @param xml oggetto che si occupa di creare l'albero xml per il salvataggio del modello.
	 */
	protected void getViewInfo(XmlCreator xml)
	{
		xml.setViewObject(this);
	}

	@Override
	protected void showMenu()
	{
		menu.exec(QCursor.pos());		
	}
	
	/**
	 * Muove le linee.
	 */
	@Override
	protected void moveLines()
	{
		Iterator<GraphicLine> it = exit_lines.iterator();
		
		while (it.hasNext())
		{
			GraphicLine l = it.next();
			QPoint p = getHookPoint();
			l.setStartPoint(p.x(), p.y());
		}
	}
	
	/**
	 * Gancio aggiunto.
	 */
	@Override
	protected void added()
	{
		Iterator<GraphicLine> it = exit_lines.iterator();
		
		while (it.hasNext())
		{
			GraphicLine l = it.next();
			l.added.emit(l, this);
		}
		super.added();
	}
	
	/**
	 * Gancio rimosso.
	 */
	@Override
	protected void removed()
	{
		Iterator<GraphicLine> it = exit_lines.iterator();
		
		while (it.hasNext())
		{
			GraphicLine l = it.next();
			l.removed.emit(l, this);
		}
		super.removed();
	}
	
	/**
	 * Emette il segnale di aggiunta di questa vista gancio. 
	 */
	@Override
	protected void triangleAdded()
	{
		((ModuleInstanceGraphicView)parent).getInstance().getInstancedModule().out_hook_added.emit(this, ((ModuleInstanceGraphicView)parent));
	}
	
	/********************************************************************************
	*                                                                               *
	*  						    PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	private void connectSignals(ModuleInstanceGraphicView g_instance)
	{
		out.added.connect(this, "added()");
		out.removed.connect(this, "removed()");
		out.properties_changed.connect(this, "propertiesChanged()");
		g_instance.moved.connect(this, "parentMoved(QPointF, QPointF)");
		g_instance.getInstance().removed.connect(this, "removed()");
		g_instance.getInstance().added.connect(this, "added()");
		out.getModule().removed.connect(out.removed);
		out.getModule().added.connect(out.added);
		out.copy_hook.connect(this, "toCopy(OutputVariable, ModuleInstanceGraphicView)");
		out.need_hook_view_info.connect(this, "getViewInfo(XmlCreator)");
		out.add_input.connect(this, "createExitLine(InputVariableHookView)");
		out.add_input_to_instance.connect(this, "createExitLine(InputVariableHookView, ModuleInstance)");
		out.to_duplicate.connect(this, "toDuplicate(ModuleInstanceGraphicView, ModuleInstance)");
		g_instance.scaled.connect(this, "parentScaled()");
	}
	
	private void createMenu()
	{
		menu = new QMenu();
		
		QAction delete = new QAction(tr("Delete"), menu);
		delete.setIcon(new QIcon("src/pixmap/delete.png"));
		delete.triggered.connect(this, "delete()");
		
		QAction edit = new QAction(tr("Edit"), menu);
		edit.triggered.connect(this, "edit()");
		
		QAction move = new QAction("Move", menu);
		move.triggered.connect(this, "moveHook()");
		
		menu.addAction(delete);
		menu.addAction(edit);
		menu.addSeparator();
		menu.addAction(move);
	}
}
