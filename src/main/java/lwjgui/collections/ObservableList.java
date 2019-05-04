package lwjgui.collections;

import java.util.ArrayList;

import lwjgui.event.ElementCallback;

public class ObservableList<E> {
	private ArrayList<E> internal;
	private ElementCallback<E> addCallback;
	private ElementCallback<E> removeCallback;

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

	public void setAddCallback( ElementCallback<E> e ) {
		this.addCallback = e;
	}
	
	public void setRemoveCallback( ElementCallback<E> e ) {
		this.removeCallback = e;
	}

	@SuppressWarnings("unchecked")
	public void addAll(E... elements) {
		for (int i = 0; i < elements.length; i++) {
			add(elements[i]);
		}
	}
	
	public void add(int index, E element) {
		if (element == null) {
			System.err.println("WARNING: Attempted to add null element to ObservableList.");
			Thread.dumpStack();
			return;
		}
		
		internal.add(index, element);

		if (addCallback != null) {
			addCallback.onEvent(element);
		}
	}
	
	public void add(E element) {
		add( internal.size(), element );
	}
	
	@SuppressWarnings("unchecked")
	public void removeAll(E... elements) {
		for (int i = 0; i < elements.length; i++) {
			remove(elements[i]);
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
		while(internal.size()>0) {
			E obj = internal.get(0);
			remove(obj);
		}
	}

	public boolean contains(E element) {
		return internal.contains(element);
	}
}
