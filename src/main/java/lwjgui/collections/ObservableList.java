package lwjgui.collections;

import java.util.ArrayList;

import lwjgui.event.ChangeEvent;

public class ObservableList<E> {
	private ArrayList<E> internal;
	private ChangeEvent<E> addCallback;
	private ChangeEvent<E> removeCallback;

	public ObservableList(ObservableList<E> array) {
		this();
		
		for (int i = 0; i < array.size(); i++) {
			this.add(array.get(i));
		}
	}
	
	@SuppressWarnings("unchecked")
	public ObservableList(E... elements) {
		this();
		for (int i = 0; i < elements.length; i++) {
			this.add(elements[i]);
		}
	}

	public ObservableList() {
		this.internal = new ArrayList<E>();
	}

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
		if (internal.remove(element) && removeCallback != null) {
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

	public boolean contains(E element) {
		return internal.contains(element);
	}
}
