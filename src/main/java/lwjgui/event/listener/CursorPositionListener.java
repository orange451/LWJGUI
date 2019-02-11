package lwjgui.event.listener;

/**
 * This listener is invoked by changes in the mouse cursor position.
 *
 */

@EventListenerType(type="CursorPositionListener")
public abstract class CursorPositionListener extends EventListener {
	public abstract void invoke(long window, double x, double y);
}
