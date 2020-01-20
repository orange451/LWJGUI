package lwjgui.glfw;

public class DisplayUtils {

	private ClientSync clientSync;

	public DisplayUtils() {
		clientSync = new ClientSync();
	}

	public void sync(int fps) {
		clientSync.sync(fps);
	}

}
