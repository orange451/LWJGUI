package lwjgui;

import lwjgui.scene.Window;
import lwjgui.scene.WindowHandle;
import lwjgui.scene.WindowManager;

public class ManagedThread extends Thread {

	private Window window;
	private WindowHandle handle;

	public ManagedThread(int width, int height, String title) {
		this(width, height, title, false);
	}

	public ManagedThread(int width, int height, String title, boolean legacyGL) {
		handle = WindowManager.generateHandle(width, height, title, legacyGL);
		handle.isVisible(false);
		setupHandle(handle);
		window = WindowManager.generateWindow(handle);
	}

	protected void setupHandle(WindowHandle handle) {
	}

	protected void init(Window window) {
	}

	protected void update() {
	}

	protected void dispose() {
	}

	@Override
	public void run() {
		LWJGUI.setThreadWindow(window);
		WindowManager.createWindow(handle, window, true);
		init(window);
		while (!window.isCloseRequested()) {
			update();
			window.render();
			window.updateDisplay(0);
		}
		dispose();
		window.dispose();
		LWJGUI.removeThreadWindow();
	}

	public void runLater(Runnable runnable) {
		window.runLater(runnable);
	}

	public <T> Task<T> submitTask(Task<T> t) {
		return window.submitTask(t);
	}

	public Window getWindow() {
		return window;
	}

}
