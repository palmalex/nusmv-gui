package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Classe utilizzata per il controllo dei nomi... da finire.
 * @author Silvia Lorenzini
 *
 */
public final class NuSmvKeywords
{
	/**
	 * Vettore delle parole chiave utilizzate da NuSMV e non utilizzabili come nome per le variabili o stati o moduli. 
	 */
	private final static String[] words = {"MODULE", "DEFINE", "CONSTANTS", "VAR", "IVAR", "INIT", "TRANS", "INVAR",
											  "SPEC", "CTLSPEC", "LTLSPEC", "PSLSPEC", "COMPUTE", "INVARSPEC", "FAIRNESS",
											  "JUSTICE", "COMPASSION", "ISA", "ASSIGN", "CONSTRAINT", "SIMPWFF", "CTLWFF",
											  "LTLWFF", "PSLWFF", "COMPWFF", "IN", "MIN", "MAX", "process", "array", "of",
											  "boolean", "integer", "real", "word", "word1", "bool", "EX", "AX", "EF", "AF",
											  "EG", "AG", "E", "F", "O", "G", "H", "X", "Y", "Z", "A", "U", "S", "V", "T",
											  "BU", "EBF", "ABF", "EBG", "ABG", "case", "mod", "next", "init", "union",
											  "in", "xor", "xnor", "self", "TRUE", "FALSE"};
	
	/**
	 * Utilizzato per l'ottimizzazione della ricerca.
	 */
	private static Set<String> keywords = new HashSet<String>(Arrays.asList(words));
	
	/**
	 * Metodo per il controllo dei nomi.
	 * @param key parola chiave da controllare.
	 */
	public static void checkName(String key)
	{
		if (keywords.contains(key));
	}
}
