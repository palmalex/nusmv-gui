/**
 * 
 */
package util;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Alessio Palmieri
 *
 */
public class PrefixFilenameFilter implements FilenameFilter {

	private String prefix;
	
	public PrefixFilenameFilter(String prefix) {
		this.prefix = prefix;
	}
	
	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File arg0, String fileName) {
		return fileName.startsWith(prefix);
	}

}
