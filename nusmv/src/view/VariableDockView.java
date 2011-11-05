/********************************************************************************
*                                                                               *
*   Module      :   VariableDockView.java                                       *
*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package view;

import model.Variable;
import widget.TreeWidget;

import com.trolltech.qt.gui.QCursor;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QTreeWidgetItem;

/**
 * Vista delle informazioni relative ad una variabile generica.
 * @author Silvia Lorenzini
 *
 */
public abstract class VariableDockView extends QTreeWidgetItem
{
	protected Variable var;
	protected TreeWidget var_tree;
	protected QMenu menu; 
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public VariableDockView(Variable var, TreeWidget tree)
	{
		this.var = var;
		this.var_tree = tree;
		
		setInfo();	
				
		var.removed.connect(this, "removed()");
		var.added.connect(this, "added()");
		var.selected.connect(this, "selected()");
		var.properties_changed.connect(this, "propertiesChanged()");
		var.to_copy.connect(this, "toCopy(Variable, ModuleWindowView, ModuleTreeView, ModuleInstanceGraphicView)");
		
		var_tree.show_menu.connect(this, "showMenu()");
		var_tree.delete_item.connect(this, "delete(QTreeWidgetItem)");
		var_tree.itemClicked.connect(this, "itemClicked()");
	}
	
	/**
	 * Esegue il monu.
	 */
	public void showMenu()
	{
		if (var_tree.currentItem().equals(this))
		{
			var.selected.emit();
			menu.exec(QCursor.pos());
		}
	}
	
	/**
	 * 
	 * @return la variabile.
	 */
	public Variable getVariable()
	{
		return var;
	}
	
	/********************************************************************************
	*                                                                               *
	*  						  PROTECTED FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Modifica le informazioni a seguito di un aggiornamento del modello della variabile.
	 */
	protected void propertiesChanged()
	{
		setText(0, "name: " + var.getName());
		
		QTreeWidgetItem type = child(0);
		
		if (var.getType() != null)
		
			type.setText(0, "type: " + var.getType().toString());
		
		else
			
			type.setText(0, "type: null");
		
		QTreeWidgetItem values = child(1);
		values.setText(0, "values: " + var.getValues());
		
		QTreeWidgetItem init = child(2);
		init.setText(0, "initial value: " + var.getInitial_value());
	}
	
	/**
	 * Aggiunge la variabile alla lista delle altre.
	 */
	protected void added()
	{
		var_tree.addTopLevelItem(this);
	}
	
	/**
	 * Rimuove la variabile dalla lista.
	 */
	protected void removed()
	{
		for (int i = 0; i < var_tree.topLevelItemCount(); i++)
		{
			if (var_tree.topLevelItem(i).equals(this))
			{
				var_tree.takeTopLevelItem(i);
				return;
			}
		}
	}
	
	/**
	 * Emette il segnale per la selezione di tutte le viste della variabile relativa alla vista corrente.
	 */
	protected void itemClicked()
	{
		if (var_tree.currentItem().equals(this))
		{
			var.selected.emit();
		}
	}
	
	/**
	 * Imposta la variabile nella vista come selezionata.
	 */
	protected void selected()
	{
		var_tree.clearSelection();
		
		setSelected(true);
	}
	
	/**
	 * Crea una copia di questa vista da aggiungere alla copia del modulo contenente questa vista.
	 * @param v la variabile da copiare.
	 * @param m_view la finestra del modulo copiato.
	 * @param m_tree la vista nell'albero di progetto del modulo copiato.
	 * @param m_gview la vista della variabile di istanza da cui si è copiato il modulo istanziato.
	 */
	protected abstract void toCopy(Variable v, ModuleWindowView m_view, ModuleTreeView m_tree, ModuleInstanceGraphicView m_gview);
	
	/**
	 * Aggiunge allo stack undo/redo il comando relativo alla cancellazione della variabile 
	 * di ingresso di questa vista.
	 * Segnale dall'albero di progetto.
	 * @param item elemento dell'albero da cui è partito il segnale.
	 */
	protected abstract void delete(QTreeWidgetItem item);
	
	/********************************************************************************
	*                                                                               *
	*  						    PRIVATE FUNCTIONS DEFINITION	                    *
	*                                                                               *
	********************************************************************************/
	
	private void setInfo()
	{
		setText(0, "name: " + var.getName());
		
		QTreeWidgetItem type = new QTreeWidgetItem();
		if (var.getType() != null)
			
			type.setText(0, "type: " + var.getType().toString());
		
		else
			
			type.setText(0, "type: null");
		
		addChild(type);
		
		QTreeWidgetItem values = new QTreeWidgetItem();
		values.setText(0, "values: " + var.getValues());
		addChild(values);
		
		QTreeWidgetItem init = new QTreeWidgetItem();
		init.setText(0, "initial value: " + var.getInitial_value());
		addChild(init);
	}
}
