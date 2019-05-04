package lwjgui.event;

public class EventHelper {
	/**
	 * Calls the events handle() function with the given event and returns true if the event is consumed by the handler.
	 * 
	 * @param eventHandler - the event handler
	 * @param event - the event
	 * @return - true if the event is consumed
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean fireEvent(EventHandler eventHandler, Event event) {
		if ( eventHandler == null )
			return false;
		eventHandler.handle(event);
		return event.isConsumed();
	}
}
