/********************************************************************************
*                                                                               *
*   Module      :   VariableGraphicView.java                                    *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import item.GraphicItem;
import item.GraphicLine;
import item.GraphicText;
import item.GraphicView;
import item.GraphicText.TextPosition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Variable;
import xml.XmlCreator;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QGraphicsSceneMouseEvent;
import com.trolltech.qt.gui.QMenu;

/**
 * Vista grafica di una variabile generica.
 * @author Silvia Lorenzini
 *
 */
public abstract class VariableGraphicView extends GraphicItem
{
	protected String name;
	protected QMenu menu;
	protected Variable variable;
	protected List<GraphicLine> exit_lines;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public VariableGraphicView(Variable v, int x, int y, int w, int h, GraphicView view)
	{
		super(x, y, w, h, view);
		
		variable = v;
		name = v.getName();
		exit_lines = new ArrayList<GraphicLine>(0);
		text = new GraphicText(this, name, TextPosition.CENTER);
		
		variable.removed.connect(this, "removed()");
		variable.added.connect(this, "added()");
		variable.properties_changed.connect(this, "propertiesChanged()");
		variable.selected.connect(this, "selected()");
		moved.connect(this, "moveObjects(QPointF, QPointF)");
		variable.to_copy.connect(this, "toCopy(Variable, ModuleWindowView, ModuleTreeView, ModuleInstanceGraphicView)");
		variable.need_view_info.connect(this, "getViewInfo(XmlCreator)");
		variable.connect_to_input.connect(this, "connectToInput(InputVariableHookView)");
		
		setZValue(2);
	}
	
	/**
	 * Verifica la posizione del mouse sull'oggetto grafico e ne imposta il relativo flag.
	 */
	public void checkMousePosition(QPointF p)
	{
		double px = p.x();
		double py = p.y();
		
		if (px > x  && px < x + width - R && (py < y + R || py > y + height - R))
		{	
			mouse_on_border = true;
			mouse_on_center = mouse_on_tl_corner = false;
		}
		else if (py > y + R && py < y + height - R && (px < x + R || px > x + width - R))
		{	
			mouse_on_border = true;
			mouse_on_center = mouse_on_tl_corner = false;
		}
		else if (px > x + width - R && py > y + height - R)
		{	
			mouse_on_tl_corner = true;
			mouse_on_center = mouse_on_border = false;
		}
		else
		{	
			mouse_on_center = true;
			mouse_on_border = mouse_on_tl_corner = false;
		}
	}
	
	/**
	 * gestisce l'azione relativa alla pressione di un pulsante del mouse sopra la vista.
	 */
	@Override
	public void mousePressEvent(QGraphicsSceneMouseEvent e)
	{
		variable.selected.emit();
		if (mouse_on_border)
		{
			GraphicLine line = new GraphicLine(x+(int)x()+width/2, y +(int)y()+ height/2, this, view);
			line.removed.connect(this, "lineDeleted(GraphicLine, Object)");
			line.added.connect(this, "lineAdded(GraphicLine, Object)");
			exit_lines.add(line);
		}
		super.mousePressEvent(e);
	}
	
	/**
	 * Elimina una linea di passaggio della variabile dalla lista. 
	 * @param line linea da eliminare.
	 * @param obj oggetto eventualmente cancellato.
	 */
	public void lineDeleted(GraphicLine line, Object obj)
	{
		if (obj == null || !obj.equals(this))
		
			exit_lines.remove(line);
	}
	
	/**
	 * Aggiunge una linea per il passaggio della variabile alla lista.
	 * @param line la linea da aggiungere.
	 */
	public void addExitLine(GraphicLine line)
	{
		line.setStartPoint(x + (int)x() + width/2, y + (int)y() + height/2);
		exit_lines.add(line);
	}
	
	/**
	 * Rimuove la linea di collegamento dalla lista.
	 * @param line linea da rimuovere.
	 */
	public void removeExitLine(GraphicLine line)
	{
		exit_lines.remove(line);
	}
	
	/**
	 * Aggiunge una linea alla lista.
	 * @param l linea da aggiungere.
	 * @param obj oggetto eventualmente ripristinato.
	 */
	public void lineAdded(GraphicLine l, Object obj)
	{
		if ((obj == null || !obj.equals(this)) && !exit_lines.contains(l))
		
			exit_lines.add(l);
	}
	
	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Aggiunge l'oggetto grafico e le eventuali linee dei collegamenti.
	 */
	protected void added()
	{
		setVisible(true);
		
		Iterator<GraphicLine> it = exit_lines.iterator();
		
		while (it.hasNext())
		{
			GraphicLine l = it.next();
			l.added.emit(l, this);
		}
	}
	
	/**
	 * Rimuove l'oggetto grafico e le linee dei collegamenti di variabili.
	 */
	protected void removed()
	{
		setVisible(false);
		
		Iterator<GraphicLine> it = exit_lines.iterator();
		
		while (it.hasNext())
		{
			GraphicLine l = it.next();
			l.removed.emit(l, this);
		}
	}
	
	/**
	 * Gestisce il movimento dell'oggetto grafico e delle linee in uscita.
	 * @param pos posizione attuale del mouse.
	 * @param last_pos posizione precedente del mouse.
	 */
	protected void moveObjects(QPointF pos, QPointF last_pos)
	{
		double gapx = pos.x() - last_pos.x();
		double gapy = pos.y() - last_pos.y();
		
		Iterator<GraphicLine> it = exit_lines.iterator();
		
		while (it.hasNext())
		{
			GraphicLine line = it.next();
			line.setStartPoint(line.sx() + (int)gapx, line.sy() + (int)gapy);
			scene().update();
		}
	}
	
	/**
	 * Se la variabile è passata in ingresso all'istanza di un modulo e se ne cambiano le proprietà
	 * allora deve essere aggiornata l'informazione anche alla variabile di ingresso del modulo istanziato.
	 */
	protected void propertiesChanged()
	{
		text.setText(variable.getName());
		
		Iterator<GraphicLine> it = exit_lines.iterator();
		
		while (it.hasNext())
		{
			GraphicLine l = it.next();
			
			assert(l.getEndObject().getClass().getName().compareTo("view.InputVariableHookView") == 0);
			
			Variable in = ((InputVariableHookView)l.getEndObject()).getInputVariable();
			in.edit(in.getName(), variable.getType(), variable.getValues(), variable.getInitial_value());
			in.properties_changed.emit();
		}
	}
	
	/**
	 * Seleziona l'oggetto.
	 */
	protected void selected()
	{
		view.getScene().clearSelection();
		setSelected(true);
	}
	
	/**
	 * Fornisce le informazioni di layout al momento del salvataggio.
	 * @param xml l'oggetto utilizzato per la creazione dell'albero xml nel salvataggio.
	 */
	protected void getViewInfo(XmlCreator xml)
	{
		xml.setViewObject(this);
	}
	
	/**
	 * Imposta il passaggio della variabile verso un modulo istanziato effettuando il collegamento con la
	 * vista (gancio) della variabile di ingresso del suddetto modulo.
	 * @param in punto di aggancio (vista) per il passaggio della variabile all'istanza di un modulo.
	 */
	protected void connectToInput(InputVariableHookView in)
	{
		QPoint end = in.getHookPoint();
		GraphicLine line = new GraphicLine(x+(int)x()+width/2, y +(int)y()+ height/2, end.x(), end.y(), this, in, view);
		line.removed.connect(this, "lineDeleted(GraphicLine, Object)");
		line.added.connect(this, "lineAdded(GraphicLine, Object)");
		
		if (!exit_lines.contains(line))
		
			exit_lines.add(line);
		
		in.addEnteringLine(line);
		line.added.emit(line, this);
	}
	
	@Override
	protected void showMenu()
	{
		menu.exec(new QPoint(QCursor.pos().x(), QCursor.pos().y()));
	}
	
	/**
	 * Effettua una copia di sé.
	 * @param v variabile da copiare.
	 * @param m_view finestra contenente la variabile da copiare.
	 * @param m_tree vista nell'albero di progetto del modulo copiato.
	 * @param m_gview vista grafica della variabile di istanza copiata.
	 */
	protected abstract void toCopy(Variable v, ModuleWindowView m_view, ModuleTreeView m_tree, ModuleInstanceGraphicView m_gview);
}
