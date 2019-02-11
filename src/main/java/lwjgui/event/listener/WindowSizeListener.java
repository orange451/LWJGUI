package lwjgui.event.listener;

/**
 * This listener is invoked by changes in window size.
 */

public abstract class WindowSizeListener implements EventListener {
	public abstract void invoke(long window, int newWidth, int newHeight);
	
	public EventListenerType getEventListenerType() {
		return EventListenerType.WINDOW_SIZE_LISTENER;
	}
}
