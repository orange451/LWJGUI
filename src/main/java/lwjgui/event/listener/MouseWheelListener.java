package lwjgui.event.listener;

/**
 * This listener is invoked when the mouse wheel is scrolled up or down.
 */

public abstract class MouseWheelListener implements EventListener {
	public abstract void invoke(long window, double dx, double dy);
	
	public EventListenerType getEventListenerType() {
		return EventListenerType.MOUSE_WHEEL_LISTENER;
	}
}
