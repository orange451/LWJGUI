package lwjgui.event;

public class ChangeEvent<T> extends Event {
	public final T previous;
	public final T current;
	
	public ChangeEvent( T previous, T current ) {
		this.previous = previous;
		this.current = current;
	}
}
