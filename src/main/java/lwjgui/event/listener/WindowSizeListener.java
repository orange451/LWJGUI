package lwjgui.event.listener;

/**
 * This listener is invoked by changes in window size.
 */

@EventListenerType(type="WindowSizeListener")
public abstract class WindowSizeListener extends EventListener {
	public abstract void invoke(long window, int newWidth, int newHeight);
}
