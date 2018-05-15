package lwjgui.event;

public abstract class MouseEvent extends Event {
	
	public abstract void onEvent( int button );

	@Override
	final public void onEvent() {
	}

}
