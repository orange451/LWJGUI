package lwjgui.event.listener;

/**
 * This listener is invoked when the window is focused/unfocused.
 */

@EventListenerType(type="WindowFocusListener")
public abstract class WindowFocusListener extends EventListener {
	public abstract void invoke(long window, boolean focus);
}
