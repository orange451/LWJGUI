package lwjgui.event.listener;

/**
 * This listener is invoked if the window is requested to close.
 */

public abstract class WindowCloseListener implements EventListener {
	public abstract void invoke(long window);
	
	public EventListenerType getEventListenerType() {
		return EventListenerType.WINDOW_CLOSE_LISTENER;
	}
}
