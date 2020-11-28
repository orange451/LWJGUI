package lwjgui.collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import lwjgui.event.ElementCallback;

public class ObservableList<E> implements Iterable<E> {
	private List<E> internal;
	
	private List<ElementCallback<E>> addCallbacks; 
	private List<ElementCallback<E>> removeCallbacks;

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
		this.internal = new ArrayList<>();
		this.addCallbacks = new ArrayList<>();
		this.removeCallbacks = new ArrayList<>();
	}

	public void setAddCallback( ElementCallback<E> e ) {
		this.addCallbacks.add(e);
	}

	public void setRemoveCallback( ElementCallback<E> e ) {
		this.removeCallbacks.add(e);
	}
	
	public void clearAddCallbacks() {
		this.addCallbacks.clear();
	}
	
	public void clearRemoveCallbacks() {
		this.removeCallbacks.clear();
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

		if (!addCallbacks.isEmpty()) {
			for (ElementCallback<E> e : addCallbacks)
				e.onEvent(element);
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
		if (internal.remove(element) && !removeCallbacks.isEmpty()) {
			for (ElementCallback<E> e : removeCallbacks)
				e.onEvent(element);
		}
	}
	
	public void remove(int index) {
		this.remove(this.get(index));
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

	public Stream<E> stream() {
		return this.internal.stream();
	}

	public Stream<E> parallelStream() {
		return this.internal.parallelStream();
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int index;

			@Override
			public boolean hasNext() {
				return index < internal.size()-1;
			}

			@Override
			public E next() {
				return internal.get(index++);
			}
		};
	}
}
