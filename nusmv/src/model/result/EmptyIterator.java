package model.result;

import java.util.Iterator;


public class EmptyIterator implements Iterator<ModuleIfc> {

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public ModuleIfc next() {
		return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
