package lwjgui.event;

public class EventHelper {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean fireEvent(EventHandler event, Event e) {
		event.handle(e);
		return e.isConsumed();
	}
}
