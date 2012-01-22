/**
 * 
 */
package model.result;

import java.util.Iterator;

/**
 * @author Alessio Palmieri
 *
 */
public interface ModuleIfc {
	public ModuleType getType();
	public String getName();
	public ModuleIfc getParent();
	public Iterator<ModuleIfc> getChilds();
	public ModuleIfc getChild(String name);
}
