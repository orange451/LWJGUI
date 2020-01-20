package lwjgui.glfw.input.callbacks;

import java.util.BitSet;

import org.lwjgl.glfw.GLFW;

public class MouseButtonCallback extends lwjgui.glfw.Callbacks.MouseButtonCallback {

	private BitSet button = new BitSet(20);
	private BitSet ignore = new BitSet(20);

	@Override
	public void invoke(long windowID, int button, int action, int mods) {
		this.button.set(button, (action != GLFW.GLFW_RELEASE));
		if (action == GLFW.GLFW_RELEASE && this.ignore.get(button))
			this.ignore.clear(button);
	}

	public boolean isButtonPressed(int button) {
		return this.button.get(button);
	}

	public boolean isButtonIgnored(int button) {
		return this.ignore.get(button);
	}

	public void setButtonIgnored(int button) {
		this.ignore.set(button);
	}

}
