package lwjgui.event.listener;

/**
 * An identifying annotation for various EventListeners.
 *
 */
public interface EventListener {
	public enum EventListenerType {
		CURSOR_POS_LISTENER,
		KEY_LISTENER,
		MOUSE_BUTTON_LISTENER,
		MOUSE_WHEEL_LISTENER,
		WINDOW_CLOSE_LISTENER,
		WINDOW_FOCUS_LISTENER,
		WINDOW_SIZE_LISTENER;
	};
	
	public EventListenerType getEventListenerType();
	
}
