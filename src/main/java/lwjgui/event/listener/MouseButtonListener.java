package lwjgui.event.listener;

/**
 * This listener is invoked by mouse button inputs.
 */

public abstract class MouseButtonListener implements EventListener {
	public abstract void invoke(long window, int button, int downup, int modifier);
	
	public EventListenerType getEventListenerType() {
		return EventListenerType.MOUSE_BUTTON_LISTENER;
	}
}
