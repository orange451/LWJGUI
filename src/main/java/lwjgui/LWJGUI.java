package lwjgui;

import lwjgui.scene.Window;

public class LWJGUI {
	private static ThreadLocal<Window> window = new ThreadLocal<>();

	/**
	 * Executes code at the beginning of the next frame.
	 * <p>
	 * Runnable is scheduled in the window bound to the caller's thread.
	 * </p>
	 * 
	 * @param runnable
	 */
	public static void runLater(Runnable runnable) {
		getThreadWindow().runLater(runnable);
	}

	/**
	 * Submits a {@link Task} to be execute at the beginning of the next frame.
	 * <p>
	 * Task is scheduled in the window bound to the caller's thread.
	 * </p>
	 * 
	 * @param <T> Return value
	 * @param t   Task
	 * @return A {@link Task} with the specificed return value
	 */
	public static <T> Task<T> submitTask(Task<T> t) {
		return getThreadWindow().submitTask(t);
	}

	/**
	 * 
	 * @return The window bound to the caller's thread
	 */
	public static Window getThreadWindow() {
		return window.get();
	}

	/**
	 * Bounds a window to the caller's thread
	 * 
	 * @param window Window to be bound
	 */
	public static void setThreadWindow(Window window) {
		LWJGUI.window.set(window);
	}

	/**
	 * Removes the window in the caller's thread
	 */
	public static void removeThreadWindow() {
		window.remove();
	}

}
