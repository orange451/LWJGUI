package lwjgui.event.listener;

/**
 * This listener is invoked when the window is focused/unfocused.
 */

public abstract class WindowFocusListener implements EventListener {
	public abstract void invoke(long window, boolean focus);
	
	public EventListenerType getEventListenerType() {
		return EventListenerType.WINDOW_FOCUS_LISTENER;
	}
}
