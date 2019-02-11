package lwjgui.event.listener;

/**
 * This listener is invoked when the mouse wheel is scrolled up or down.
 */

@EventListenerType(type="MouseWheelListener")
public abstract class MouseWheelListener extends EventListener {
	public abstract void invoke(long window, double dx, double dy);
}
