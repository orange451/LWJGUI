package lwjgui.event;

import org.lwjgl.glfw.GLFW;

public class KeyEvent extends Event {
	public final int key;
	public final int scancode;
	public final int action;
	public final int mods;
	public final boolean isCtrlDown;
	public final boolean isAltDown;
	public final boolean isShiftDown;
	
	public KeyEvent(int key, int scancode, int action, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown ) {
		this.key = key;
		this.scancode = scancode;
		this.action = action;
		this.mods = mods;
		this.isCtrlDown = isCtrlDown;
		this.isAltDown = isAltDown;
		this.isShiftDown = isShiftDown;
	}

	public int getKey() {
		return key;
	}
	
	public String getKeyName() {
		return GLFW.glfwGetKeyName(key, scancode);
	}
}
