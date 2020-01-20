package lwjgui;

import lwjgui.font.Font;
import lwjgui.scene.Window;

public class LWJGUI {
	private static ThreadLocal<Window> window = new ThreadLocal<>();

	public static void dispose() {
		Font.dispose();
	}

	public static void runLater(Runnable runnable) {
		submitTask(new Task<Void>() {
			@Override
			protected Void call() {
				runnable.run();
				return null;
			}
		});
	}

	public static <T> Task<T> submitTask(Task<T> t) {
		return getThreadWindow().submitTask(t);
	}

	public static Window getThreadWindow() {
		return window.get();
	}

	public static void setThreadWindow(Window window) {
		LWJGUI.window.set(window);
	}

	public static void removeThreadWindow() {
		window.remove();
	}

}
