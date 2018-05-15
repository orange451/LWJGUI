package lwjgui.geometry;

import java.util.ArrayList;

import lwjgui.event.ChangeEvent;
import lwjgui.event.Event;

public class ObservableList<E> {
	private ArrayList<E> internal = new ArrayList<E>();
	private ChangeEvent<E> addCallback;
	private ChangeEvent<E> removeCallback;

	public void setAddCallback( ChangeEvent<E> e ) {
		this.addCallback = e;
	}
	
	public void setRemoveCallback( ChangeEvent<E> e ) {
		this.removeCallback = e;
	}
	
	public void add(E element) {
		internal.add(element);

		if ( addCallback != null ) {
			addCallback.onEvent(element);
		}
	}
	
	public void remove(E element) {
		internal.remove(element);

		if ( removeCallback != null ) {
			removeCallback.onEvent(element);
		}
	}

	public int size() {
		return internal.size();
	}

	public E get(int i) {
		return internal.get(i);
	}

	public void clear() {
		this.internal.clear();
	}
}
