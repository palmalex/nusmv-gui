/********************************************************************************
*                                                                               *
*   Module      :   Counter.java                                                *

*   Author      :   Silvia Lorenzini		       		     		            *
*   Tools       :   Eclipse                                                     *
********************************************************************************/
package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Classe utilizzare per indicizzare moduli e variabili.
 * @author Silvia Lorenzini
 *
 */
public class Counter
{
	private int count;
	private List<Integer> available_int;
	
	/********************************************************************************
	*                                                                               *
	*  							PUBLIC FUNCTIONS DEFINITION	                        *
	*                                                                               *
	********************************************************************************/
	
	/**
	 * Costruttore.
	 */
	public Counter()
	{
		count = 0;
		this.available_int = new ArrayList<Integer>(0);
	}
	
	/**
	 * Costruttore.
	 * @param available_int lista di interi utilizzabili per il conteggio.
	 * @param count intero di partenza.
	 */
	public Counter(List<Integer> available_int, int count)
	{
		this.count = count;
		this.available_int = available_int;
	}
	
	/**
	 * Fornisce il primo indice disponibile.
	 * @return primo intero disponibile.
	 */
	public int next()
	{
		if (available_int.isEmpty())
			
			return ++count;
		
		return available_int.remove(0).intValue();
	}
	
	/**
	 * Aggiunge alla lista un indice utilizzabile.
	 * @param number indice utilizzabile.
	 */
	public void addAvailableInt(int number)
	{
		for (int i = 0; i < available_int.size(); i++)
		{
			if (number == available_int.get(i))
			{
				return;
			}
			if (number < available_int.get(i))
			{
				available_int.add(i, number);
				return;
			}
		}
		available_int.add(number);	
	}
	
	/**
	 * Rimuove dalla lista un intero che è già stato usato come indice.
	 * @param number intero utilizzato come indice da rimuovere.
	 */
	public void removeAvailableInt(int number)
	{
		available_int.remove(Integer.valueOf(number));
	}
	
	
	/**
	 * Restituisce la lista degli interi utilizzabili.
	 * @return lista di Integer utilizzabili per l'indicizzazione.
	 */
	public List<Integer> getAvailableInt()
	{
		return available_int;
	}
	
	/**
	 * Aggiunge alla lista di interi utilizzabili una nuova lista di interi.
	 * @param index_list la lista da aggiungere.
	 */
	public void setAvailableInt(List<Integer> index_list)
	{
		Iterator<Integer> it = index_list.iterator();
		
		while (it.hasNext())
			
			available_int.add(it.next());
	}
	
	/**
	 * Imposta l'indice di partenza.
	 * @param count intero utilizzato come primo indice.
	 */
	public void setStartCount(int count)
	{
		this.count = count;
	}
	
	/**
	 * Restituisce l'indice utilizzato come primo.
	 * @return l'intero utilizzato come primo indice.
	 */
	public int getCount()
	{
		return count;
	}
	
	/**
	 * Elimina tutti gli interi utilizzabili come indici dalla lista.
	 */
	public void removeAllAvailableInt()
	{
		available_int.removeAll(available_int);
	}
}
