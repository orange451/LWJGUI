package lwjgui.event;

public class KeyEvent extends Event {
	public final int key;
	public final int mods;
	public final boolean isCtrlDown;
	public final boolean isAltDown;
	public final boolean isShiftDown;
	
	public KeyEvent( int key, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown ) {
		this.key = key;
		this.mods = mods;
		this.isCtrlDown = isCtrlDown;
		this.isAltDown = isAltDown;
		this.isShiftDown = isShiftDown;
	}

	public int getKey() {
		return key;
	}
}
