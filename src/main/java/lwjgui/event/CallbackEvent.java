package lwjgui.event;

public class CallbackEvent<T> extends Event {
	public final T object;
	
	public CallbackEvent( T current ) {
		this.object = current;
	}
}
