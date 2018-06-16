package lwjgui.event;

public abstract class ScrollEvent extends Event {
	
	public abstract void onEvent( double x, double y );

	@Override
	final public void onEvent() {
	}

}
