package lwjgui.glfw.input.callbacks;

import lwjgui.glfw.Callbacks.CharModsCallback;

public class KeyboardCharModsCallback extends CharModsCallback {

	private int mods;

	@Override
	public void invoke(long window, int codepoint, int mods) {
		this.mods = mods;
	}

	public int getMods() {
		return this.mods;
	}
}
