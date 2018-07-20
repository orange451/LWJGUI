package lwjgui.event;

public interface EventHandler<T extends Event> {
	/**
	 * Invoked when a specific event of the type for which this handler is
	 * registered happens.
	 *
	 * @param event the event which occurred
	 */
	public void handle(T event);
}
