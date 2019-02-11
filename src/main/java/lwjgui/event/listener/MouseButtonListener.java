package lwjgui.event.listener;

/**
 * This listener is invoked by mouse button inputs.
 */

@EventListenerType(type="MouseButtonListener")
public abstract class MouseButtonListener extends EventListener {
	public abstract void invoke(long window, int button, int downup, int modifier);
}
