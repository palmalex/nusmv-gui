/********************************************************************************
*                                                                               *
*   Module      :   StateGraphicView.java                                       *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicItem;
import item.GraphicText;
import item.GraphicView;
import item.LinePosition;
import item.GraphicText.TextPosition;

import java.util.Iterator;

import model.State;
import xml.XmlCreator;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QGraphicsItemInterface;
import com.trolltech.qt.gui.QGraphicsPixmapItem;
import com.trolltech.qt.gui.QGraphicsSceneHoverEvent;
import com.trolltech.qt.gui.QGraphicsSceneMouseEvent;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QKeyEvent;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPixmap;
import command.RemoveStateCommand;

import dialog.AddStateActionDialog;
import dialog.ModifyStateActionDialog;
import dialog.RemoveStateActionDialog;
import dialog.StateRenameDialog;

/**
 * Vista grafica del modello di stato di una FSM.
 * @author Silvia Lorenzini
 *
 */
public class StateGraphicView extends GraphicItem
{
	private QMenu menu;
	private State state;
	private QGraphicsPixmapItem initial_pixmap;
	private GraphicText actions;
	private boolean drawing_transition;
	private final int INITIAL_WIDTH;
	private final int INITIAL_HEIGHT;
	
	public Signal4<StateGraphicView, Integer, Integer, LinePosition> state_released;
	public Signal4<StateGraphicView, Integer, Integer, LinePosition> state_clicked;
	public Signal2<Integer, Integer> state_moved;
	public Signal2<Integer, Integer> state_resized;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 * @param state modello di stato.
	 * @param x coordinata x di posizione dello stato.
	 * @param y coordinata y di posizione dello stato.
	 * @param view vista della scena grafica.
	 */
	public StateGraphicView(State state, int x, int y, GraphicView view)
	{
		super(x, y, 120, 100, view);
		this.INITIAL_WIDTH = 120;
		this.INITIAL_HEIGHT = 100;
		this.state = state;
		this.text = new GraphicText(this, state.getName(), TextPosition.TOP);
		this.drawing_transition = false;
		mouse_on_tl_corner = false;
		
		state_released = new Signal4<StateGraphicView, Integer, Integer, LinePosition>();
		state_clicked = new Signal4<StateGraphicView, Integer, Integer, LinePosition>();
		state_moved = new Signal2<Integer, Integer>();
		state_resized = new Signal2<Integer, Integer>();

		setHandlesChildEvents(false);
		
		setInitialPixmap();
		setActions();
		connectSignals();
		createMenu();
		resized();
	}
	
	public StateGraphicView(State state, int x, int y,int w, int h, GraphicView view)
	{
		super(x, y, 120, 100, view);
		this.INITIAL_WIDTH = w;
		this.INITIAL_HEIGHT = h;
		this.state = state;
		this.text = new GraphicText(this, state.getName(), TextPosition.TOP);
		this.drawing_transition = false;
		mouse_on_tl_corner = false;
		
		state_released = new Signal4<StateGraphicView, Integer, Integer, LinePosition>();
		state_clicked = new Signal4<StateGraphicView, Integer, Integer, LinePosition>();
		state_moved = new Signal2<Integer, Integer>();
		state_resized = new Signal2<Integer, Integer>();

		setHandlesChildEvents(false);
		
		setInitialPixmap();
		setActions();
		connectSignals();
		createMenu();
		resized();
	}
		
	/**
	 * Flag che indica che si sta aggiungendo una transizione.
	 * @param drawing true se si sta aggiungendo la transizione, false altrimenti.
	 */
	public void drawingTransition(boolean drawing)
	{
		drawing_transition = drawing;
	}
	
	public State getState()
	{
		return state;
	}
	
	public QMenu getMenu()
	{
		return menu;
	}
	
	/**
	 * Se si sta aggiungendo una transizione ed il mouse è rilasciato sullo stato allora questo
	 * viene impostato come stato finale della transizione.
	 * @param p posizione di rilascio del mouse.
	 * @param t linea rappresentante la vista grafica della transizione.
	 */
	public void setEndTransition(QPointF p, TransitionGraphicView t)
	{
		if (new QRectF(x + x(), y + y() + R, R, height - 2*R).contains(p))
		{
			t.setEndPosition(LinePosition.LEFT);
			t.setEndPoint(new QPoint(x + (int)x(), (int)p.y()));
		}
		else if (new QRectF(x + x() + R, y + y(), width - 2*R, R).contains(p))
		{
			t.setEndPosition(LinePosition.TOP);
			t.setEndPoint(new QPoint((int)p.x(), y + (int)y()));
		}
		else if(new QRectF(x + x() + width, y + y() + R, -R, height - 2*R).contains(p))
		{
			t.setEndPosition(LinePosition.RIGHT);
			t.setEndPoint(new QPoint(x + (int)x() + width, (int)p.y()));
		}
		else
		{
			t.setEndPosition(LinePosition.BOTTOM);
			t.setEndPoint(new QPoint((int)p.x(), y + (int)y() + height));
		}
	}
	
	/**
	 * 
	 * @param pos posizione del mouse.
	 * @return true se il mouse è sul bordo dello stato.
	 */
	public boolean mouseIsOnBorder(QPointF pos)
	{
		checkMousePosition(pos);
		
		return mouse_on_border;
	}
	
	@Override
	public void mouseMoveEvent(QGraphicsSceneMouseEvent e)
	{
		if (!drawing_transition)
		{
			QPointF p1 = e.scenePos();
			QPointF p2 = e.lastScenePos();
			
			int gap_x = (int)(p1.x() - p2.x());
			int gap_y = (int)(p1.y() - p2.y());
			
			state_moved.emit(gap_x, gap_y);
		}
		super.mouseMoveEvent(e);
	}
	
	@Override
	public void hoverEnterEvent(QGraphicsSceneHoverEvent event)
	{
		if (drawing_transition)
		{
			if (mouse_on_border)
				setSelected(true);
			else
				setSelected(false);
			scene().update();
		}
		super.hoverEnterEvent(event);
	}
	
	@Override
	public void hoverLeaveEvent(QGraphicsSceneHoverEvent event)
	{
		if (drawing_transition)
		{
			setSelected(false);
			scene().update();
		}
		super.hoverLeaveEvent(event);
	}
	
	@Override
	public void hoverMoveEvent(QGraphicsSceneHoverEvent e)
	{
		if (drawing_transition)
		{
			checkMousePosition(e.pos());
			if (mouse_on_border)
				setSelected(true);
			else
				setSelected(false);
			scene().update();
		}
		super.hoverMoveEvent(e);
	}
	
	@Override
	public void mousePressEvent(QGraphicsSceneMouseEvent e)
	{
		Iterator<QGraphicsItemInterface> it = scene().selectedItems().iterator();
		
		while (it.hasNext())
		{
			QGraphicsItemInterface item = it.next();
			
			if (item.getClass().getName().compareTo("view.StateGraphicView") == 0)
			{
				item.setSelected(false);
			}
			scene().update();
		}
		if (mouse_on_border)
		{
			getHookPoint(e.pos());
		}
		else
		{
			state_clicked.emit(null, 0, 0, null);
		}
		super.mousePressEvent(e);
		
		if (!drawing_transition)
		
			setFlag(GraphicsItemFlag.ItemIsMovable);
	}
	
	@Override
	public void keyPressEvent(QKeyEvent e)
	{
		if (e.key() == Qt.Key.Key_Delete.value())
		{
			remove();
		}
		super.keyPressEvent(e);
	}
	
	/**
	 * Verifica la posizione del mouse e imposta correttamente i flag.
	 */
	@Override
	public void checkMousePosition(QPointF p)
	{
		double px = p.x();
		double py = p.y();
		
		if (px > x + R  && px < x + width - R && (py < y + R || py > y + height - R))
		{	
			if (drawing_transition)
			{
				mouse_on_border = true;
			}
			else
			{
				mouse_on_border = false;
			}
			mouse_on_center = false;
		}
		else if (py > y + R && py < y + height - R && (px < x + R || px > x + width - R))
		{	
			if (drawing_transition)
			{
				mouse_on_border = true;
			}
			else
			{
				mouse_on_border = false;
			}
			mouse_on_center = false;
		}
		else if (px > x + R  && px < x + width - R && py > y + R && py < y + height - R)
		{	
			mouse_on_center = true;
			mouse_on_border = false;
		}
	}
	
	/********************************************************************************
	*                                                                               *
	*  							PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Aggiorna la vista quando il nome dello stato è modificato.
	 */
	protected void renamed()
	{
		text.setText(state.getName());
	}
	
	/**
	 * Esegue il dialog per la modifica del nome dello stato.
	 */
	protected void rename()
	{
		new StateRenameDialog(state, view.getUndoStack()).exec();
	}
	
	/**
	 * Aggiunge allo stack undo/redo il comando relativo alla cancellazione dello stato.
	 */
	protected void remove()
	{
		view.getUndoStack().push(new RemoveStateCommand(state));
	}
	
	/**
	 * Se lo stato è iniziale mostra il simbolo corrispondente, altrimenti lo nasconde.
	 */
	protected void setInitial()
	{
		initial_pixmap.setVisible(state.isInitial());		
	}
	
	/**
	 * Ridimensiona lo stato in base al testo delle azioni e del nome.
	 */
	protected void resized()
	{
		actions.setTextPosition();
		width = Math.max(INITIAL_WIDTH, actions.getTextWidth() + MIN_X_GAP);
		height = Math.max(INITIAL_HEIGHT, actions.getTextHeight() + MIN_Y_GAP);		
		text.setTextPosition();
		state_resized.emit(x + (int)x() + width, y + (int)y() + height);
	}	
	
	/**
	 * Fornisce le informazioni di layout in fase di salvataggio.
	 * @param xml oggetto che costruisce l'albero xml di salvataggio.
	 */
	protected void getViewInfo(XmlCreator xml)
	{
		xml.setViewObject(this);
	}
	
	/**
	 * Aggiorna la vista delle azioni in seguito ad una modifica, aggiunta o rimozione.
	 */
	protected void actionsChanged()
	{
		String entry_actions  = "";
		
		Iterator<String> it1 = state.getOnentry().iterator();
		while (it1.hasNext())
		{
			entry_actions += "\n     " + it1.next();
		}
		
		String during_action  = "";
		
		Iterator<String> it2 = state.getDuring().iterator();
		while (it2.hasNext())
		{
			during_action += "\n     " + it2.next();
		}
		
		String exit_actions  = "";
		
		Iterator<String> it3 = state.getOnexit().iterator();
		while (it3.hasNext())
		{
			exit_actions += "\n     " + it3.next();
		}
		
		String text = "- Onentry:" + entry_actions + "\n- During:" + during_action + "\n- Onexit:" + exit_actions;
		actions.setText(text);
		resized.emit();
	}
	
	/**
	 * Esegue il dialog per l'aggiunta di un'azione sullo stato.
	 */
	protected void addAction()
	{
		new AddStateActionDialog(state).exec();
	}
	
	/**
	 * Esegue il dialog per la modifica di un'azione sullo stato.
	 */
	protected void modifyAction()
	{
		new ModifyStateActionDialog(state).exec();
	}
	
	/**
	 * Esegue il dialog per l'eliminazione di un'azione sullo stato.
	 */
	protected void removeAction()
	{
		new RemoveStateActionDialog(state).exec();
	}
	
	/**
	 * Nasconde o visualizza le azioni.
	 * @param hidden se true nasconde le azioni, se false le visualizza.
	 */
	protected void hideActions(boolean hidden)
	{
		actions.setVisible(!hidden);
	}	

	/**
	 * Disegna l'oggetto relativo alla vista dello stato.
	 */
	@Override
	protected void paintItem(QPainter painter)
	{
		painter.setBrush(new QColor(225, 255, 185));
		painter.drawRoundedRect(x, y, width, height, R, R);
		painter.drawLine(x, (int)(y+R), x+width, (int)(y+R));
	}

	@Override
	protected void showMenu()
	{
		menu.exec(QCursor.pos());
	}
	
	/********************************************************************************
	*                                                                               *
	*  					       PRIVATE FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	private void getHookPoint(QPointF p)
	{
		p = mapToScene(p);
		
		if (new QRectF(x + x(), y + y() + R, R, height - 2*R).contains(p))
		{
			state_clicked.emit(this, x + (int)x(), (int)p.y(), LinePosition.LEFT);
		}
		else if (new QRectF(x + x() + R, y + y(), width - 2*R, R).contains(p))
		{
			state_clicked.emit(this, (int)p.x(), y + (int)y(), LinePosition.TOP);
		}
		else if(new QRectF(x + x() + width, y + y() + R, -R, height - 2*R).contains(p))
		{
			state_clicked.emit(this, x + (int)x() + width, (int)p.y(), LinePosition.RIGHT);
		}
		else
		{
			state_clicked.emit(this, (int)p.x(), y + (int)y() + height, LinePosition.BOTTOM);
		}
	}
	
	private void connectSignals()
	{
		state.added.connect(this, "added()");
		state.removed.connect(this, "removed()");
		state.renamed.connect(this, "renamed()");
		state.change_initial.connect(this, "setInitial()");
		state.actions_changed.connect(this, "actionsChanged()");
		state.need_view_info.connect(this, "getViewInfo(XmlCreator)");
		resized.connect(this, "resized()");		
		state_moved.connect(state.state_moved);
		state_resized.connect(state.state_resized);
	}
	
	private void setActions()
	{
		String text = "- Onentry: \n- During: \n- Onexit: ";
		actions = new GraphicText(this, text, 15, (int)R + 2);
		actionsChanged();
	}
	
	private void setInitialPixmap()
	{
		initial_pixmap = new QGraphicsPixmapItem(new QPixmap("src/pixmap/initial.png"));
		initial_pixmap.setParentItem(this);
		initial_pixmap.setPos(x+R-35, y-35);
		initial_pixmap.setVisible(state.isInitial());
	}
	
	private void createMenu()
	{
		this.menu = new QMenu();
		
		QAction rename = new QAction("Rename", menu);
		rename.triggered.connect(this, "rename()");
		
		QAction remove = new QAction("Delete", menu);
		remove.setIcon(new QIcon("src/pixmap/delete.png"));
		remove.triggered.connect(this, "remove()");
		
		QAction initial = new QAction("Initial", menu);
		initial.setCheckable(true);
		initial.setChecked(state.isInitial());
		initial.triggered.connect(state, "changeInitial()");
		state.change_initial.connect(initial, "setChecked(boolean)");
		
		QAction add_action = new QAction("Add action", menu);
		add_action.triggered.connect(this, "addAction()");
		
		QAction mod_action = new QAction("Modify action", menu);
		mod_action.triggered.connect(this, "modifyAction()");		
		
		QAction rem_action = new QAction("Remove action", menu);
		rem_action.triggered.connect(this, "removeAction()");
		
		QAction hide_actions = new QAction("Hide actions", menu);
		hide_actions.setCheckable(true);
		hide_actions.triggered.connect(this, "hideActions(boolean)");
		
		menu.addAction(rename);
		menu.addAction(initial);
		menu.addAction(remove);
		menu.addSeparator();
		menu.addAction(add_action);
		menu.addAction(mod_action);
		menu.addAction(rem_action);
		menu.addAction(hide_actions);
	}
}
