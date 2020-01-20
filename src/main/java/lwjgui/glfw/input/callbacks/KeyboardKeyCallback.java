package lwjgui.glfw.input.callbacks;

import java.util.BitSet;

import org.lwjgl.glfw.GLFW;

import lwjgui.glfw.Callbacks.KeyCallback;

public class KeyboardKeyCallback extends KeyCallback {
	private BitSet keys = new BitSet(65536);
	private BitSet ignore = new BitSet(65536);

	@Override
	public void invoke(long window, int key, int scancode, int action, int mods) {
		if (key < 0 || key > 65535) {
			System.out.println("WARNING: Caught invalid key! (Key: " + key + ", scancode: " + scancode + ", pressed: "
					+ (action != GLFW.GLFW_RELEASE) + ")");
			return;
		}

		this.keys.set(key, (action != GLFW.GLFW_RELEASE));
		if (action == GLFW.GLFW_RELEASE && this.ignore.get(key))
			this.ignore.clear(key);
	}

	public boolean isKeyPressed(int keycode) {
		return this.keys.get(keycode);
	}

	public boolean isKeyIgnored(int keycode) {
		return this.ignore.get(keycode);
	}

	public void setKeyIgnored(int keycode) {
		this.ignore.set(keycode);
	}

}
