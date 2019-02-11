package lwjgui.event.listener;

/**
 * This listener is invoked if the window is requested to close.
 */

@EventListenerType(type="WindowCloseListener")
public abstract class WindowCloseListener extends EventListener {
	public abstract void invoke(long window);
}
