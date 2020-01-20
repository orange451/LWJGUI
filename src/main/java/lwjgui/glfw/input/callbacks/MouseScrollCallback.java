package lwjgui.glfw.input.callbacks;

import lwjgui.glfw.Callbacks.ScrollCallback;

public class MouseScrollCallback extends ScrollCallback {

	private double x, y;

	@Override
	public void invoke(long windowID, double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getYWheel() {
		double yWheel = y;
		y = 0;
		return yWheel;
	}

	public double getXWheel() {
		double xWheel = x;
		x = 0;
		return xWheel;
	}

}
