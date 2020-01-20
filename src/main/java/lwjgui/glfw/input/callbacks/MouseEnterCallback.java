package lwjgui.glfw.input.callbacks;

import lwjgui.glfw.Callbacks.CursorEnterCallback;

public class MouseEnterCallback extends CursorEnterCallback {

	private boolean inside;

	@Override
	public void invoke(long windowID, boolean inside) {
		this.inside = inside;
	}

	public boolean isInside() {
		return inside;
	}

}
