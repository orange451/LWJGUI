package lwjgui.glfw;

public class CustomCursor {

	private String path;
	private int hotX, hotY;

	public CustomCursor(String path, int hotX, int hotY) {
		this.path = path;
		this.hotX = hotX;
		this.hotY = hotY;
	}

	public String getPath() {
		return path;
	}

	public int getHotX() {
		return hotX;
	}

	public int getHotY() {
		return hotY;
	}
}
