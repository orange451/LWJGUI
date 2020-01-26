package lwjgui.glfw.input;

import org.lwjgl.glfw.GLFW;

import lwjgui.glfw.input.callbacks.KeyboardCharCallback;
import lwjgui.glfw.input.callbacks.KeyboardCharModsCallback;
import lwjgui.glfw.input.callbacks.KeyboardKeyCallback;
import lwjgui.scene.Window;

/**
 * 
 * Thread-safe object used to query Keyboard input data from a {@link Window}.
 * 
 */
public final class KeyboardHandler {

	private final KeyboardKeyCallback keyCallback;
	private final KeyboardCharCallback charCallback;
	private final KeyboardCharModsCallback modCallback;

	private long lastPress = 0l;

	public KeyboardHandler(Window window) {
		this.keyCallback = new KeyboardKeyCallback();
		this.charCallback = new KeyboardCharCallback();
		this.modCallback = new KeyboardCharModsCallback();

		window.getKeyCallback().addCallback(keyCallback);
		window.getCharCallback().addCallback(charCallback);
		window.getCharModsCallback().addCallback(modCallback);
	}

	public boolean isKeyPressed(int keycode) {
		return this.keyCallback.isKeyPressed(keycode) && !this.keyCallback.isKeyIgnored(keycode);
	}

	public void ignoreKeyUntilRelease(int keycode) {
		this.keyCallback.setKeyIgnored(keycode);
	}

	public void enableTextInput() {
		this.charCallback.setEnabled(true);
	}

	public void setTextInputEnabled(boolean flag) {
		this.charCallback.setEnabled(flag);
	}

	public void disableTextInput() {
		this.charCallback.setEnabled(false);
	}

	public void clearInputData() {
		this.charCallback.getData().clear();
	}

	public String handleInput(String input) {
		if (!this.charCallback.hasData())
			return this.handleBackspace(input);
		String result = input;

		for (String in : this.charCallback.getData())
			result += in;

		return result;
	}

	public boolean isShiftPressed() {
		return this.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) || this.isKeyPressed(GLFW.GLFW_KEY_RIGHT_SHIFT);
	}

	public boolean isCtrlPressed() {
		return this.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL) || this.isKeyPressed(GLFW.GLFW_KEY_RIGHT_CONTROL)
				|| this.isKeyPressed(GLFW.GLFW_KEY_LEFT_SUPER) || this.isKeyPressed(GLFW.GLFW_KEY_RIGHT_SUPER);
	}

	public boolean isAltPressed() {
		return this.isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT) || this.isKeyPressed(GLFW.GLFW_KEY_RIGHT_ALT);
	}

	private String handleBackspace(String input) {
		long currentPress = 0l;
		if (this.isKeyPressed(GLFW.GLFW_KEY_BACKSPACE)
				&& ((currentPress = System.currentTimeMillis()) - this.lastPress) > 100) {
			String result = input;
			this.lastPress = currentPress;
			if (!input.isEmpty())
				result = input.substring(0, input.length() - 1);

			return result;
		} else
			return input;
	}

	/**
	 * <p>
	 * This function must only be called from the main thread.
	 * </p>
	 * 
	 * @param windowID
	 * @param keycode
	 * @return
	 */
	public static boolean isKeyPressedRaw(long windowID, int keycode) {
		return GLFW.glfwGetKey(windowID, keycode) == GLFW.GLFW_PRESS;
	}

}
