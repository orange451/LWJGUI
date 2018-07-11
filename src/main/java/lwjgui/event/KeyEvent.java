package lwjgui.event;

public abstract class KeyEvent extends Event {
	
	public abstract void onEvent( int key, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown );

	@Override
	final public void onEvent() {
	}

}
