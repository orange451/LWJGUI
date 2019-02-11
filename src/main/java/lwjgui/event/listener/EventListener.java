package lwjgui.event.listener;

/**
 * An identifying annotation for various EventListeners.
 *
 */
public abstract class EventListener {
	public String getType() {
		return getClass().getAnnotation(EventListenerType.class).type();
	}
}
