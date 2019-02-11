package lwjgui.event.listener;

/**
 * This listener is invoked by keyboard inputs.
 */

public abstract class KeyListener implements EventListener {
	public abstract void invoke(long window, int key, int scancode, int action, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown);
	
	public EventListenerType getEventListenerType() {
		return EventListenerType.KEY_LISTENER;
	}
}