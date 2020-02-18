package lwjgui.scene;

import lwjgui.LWJGUI;
import lwjgui.Task;

public class WindowThread extends Thread {

	private Window window;
	private WindowHandle handle;
	
	public WindowThread() {
		this("Window");
	}
	
	public WindowThread(String title) {
		this(100, 100, title);
	}

	public WindowThread(int width, int height, String title) {
		this(width, height, title, false);
	}

	public WindowThread(int width, int height, String title, boolean legacyGL) {
		handle = WindowManager.generateHandle(width, height, title, legacyGL);
		handle.isVisible(false);
		setupHandle(handle);
		window = WindowManager.generateWindow(handle);
	}

	/**
	 * Called before window generation, allows for modifying GLFW window hints.
	 * 
	 * @param handle The {@link WindowHandle} object used to create
	 */
	protected void setupHandle(WindowHandle handle) {
	}

	/**
	 * Called before thread loop. Use it to setup the UI.
	 * 
	 * @param window
	 */
	protected void init(Window window) {
	}

	/**
	 * Called before {@link Window#render()}
	 */
	protected void update() {
	}

	/**
	 * Called before {@link Window#dispose()}. Use it to unload any OpenGL or NanoVG
	 * resources.
	 */
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

	/**
	 * Executes code at the beginning of the next frame.
	 * 
	 * @param runnable
	 */
	public void runLater(Runnable runnable) {
		window.runLater(runnable);
	}

	/**
	 * Submits a {@link Task} to be execute at the beginning of the next frame.
	 * 
	 * @param <T> Return value
	 * @param t   Task
	 * @return A {@link Task} with the specificed return value
	 */
	public <T> Task<T> submitTask(Task<T> t) {
		return window.submitTask(t);
	}

	public Window getWindow() {
		return window;
	}

}
