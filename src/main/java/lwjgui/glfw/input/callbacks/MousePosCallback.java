package lwjgui.glfw.input.callbacks;

import lwjgui.glfw.Callbacks.CursorPosCallback;

public class MousePosCallback extends CursorPosCallback {

	private double lastX, lastY, x, y, dx, dy;

	@Override
	public void invoke(long window, double xpos, double ypos) {
		this.lastX = this.x;
		this.lastY = this.y;
		this.x = xpos;
		this.y = ypos;
		this.dx = this.x - this.lastX;
		this.dy = this.y - this.lastY;
	}

	public void update() {
		this.lastX = this.x;
		this.lastY = this.y;
		this.dx = this.x - this.lastX;
		this.dy = this.y - this.lastY;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getDX() {
		return dx;
	}

	public double getDY() {
		return dy;
	}

	public void dropInput() {
		this.dx = 0;
		this.dy = 0;
	}

}
