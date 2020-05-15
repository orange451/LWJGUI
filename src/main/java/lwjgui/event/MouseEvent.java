package lwjgui.event;

public class MouseEvent extends Event {
	public final double mouseX, mouseY;
	public final int button;
	public int clicks;

	public MouseEvent(double mouseX, double mouseY, int button) {
		this(mouseX, mouseY, button, 0);
	}
	public MouseEvent(double mouseX, double mouseY, int button, int clicks) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.button = button;
		this.clicks = clicks;
	}
	
	public final int getClickCount() {
		return clicks;
	}
	public double getMouseX() {
		return mouseX;
	}
	public double getMouseY() {
		return mouseY;
	}
	public int getButton() {
		return button;
	}
}
