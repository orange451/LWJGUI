package lwjgui.event.listener;

/**
 * This listener is invoked by changes in the mouse cursor position.
 *
 */

public abstract class CursorPositionListener implements EventListener {
	public abstract void invoke(long window, double x, double y);
	
	public EventListenerType getEventListenerType() {
		return EventListenerType.CURSOR_POS_LISTENER;
	}
}
