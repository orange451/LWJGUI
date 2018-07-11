package lwjgui.event;

public abstract class ChangeEvent<E> extends Event {
	
	public abstract void onEvent( E changed );

	@Override
	final public void onEvent() {
	}

}
