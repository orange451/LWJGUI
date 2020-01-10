package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

import lwjgui.font.Font;
import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.layout.StackPane;

public class LWJGUI {
	private static HashMap<Long, Window> windows = new HashMap<Long, Window>();
	private static Queue<Task<?>> tasks = new ConcurrentLinkedQueue<>();
	protected static Context currentContext;
	
	/**
	 * Initializes a LWJGUI window from a GLFW window handle. The window contains a Scene class.<br>
	 * Rendering components can be added to the scene. However, to set initial
	 * rendering, the scene's root node must first be set.
	 * @param window
	 * @return Returns a LWJGUI Window that contains a rendering Context and a Scene.
	 */
	public static Window initialize(long window) {
		return initialize(window, false);
	}

	/**
	 * Initializes a LWJGUI window from a GLFW window handle. The window contains a Scene class.<br>
	 * Rendering components can be added to the scene. However, to set initial
	 * rendering, the scene's root node must first be set.
	 * @param window
	 * @param external Set true if window object's creation/destruction is managed outside of LWJGUI
	 * @return Returns a LWJGUI Window that contains a rendering Context and a Scene.
	 */
	public static Window initialize(long window, boolean external) {
		if ( windows.containsKey(window) ) {
			System.err.println("Failed to initialize this LWJGUI Window. Already initialized.");
			return null;
		}
		Context context = new Context(window);
		Scene scene = new Scene(new StackPane());
		Window wind = new Window(context, scene, external);
		windows.put(window, wind);
		
		currentContext = context;
		
		return wind;
	}

	public static void init() {

	}

	/**
	 * This method renders all of the initialized LWJGUI windows.<br>
	 * It will loop through each window, set the context, and then render.<br>
	 * <br>
	 * You can set a rendering callback to a window if you want your own rendering
	 * at the start of a render-pass.
	 */
	public static void render() {
		render(true);
	}

	public static void render(boolean pollEvents) {
		// poll events to callbacks
		if (pollEvents)
			glfwPollEvents();
		long currentContext = GLFW.glfwGetCurrentContext();
		
		// Render all windows
		ArrayList<Long> windowsToClose = new ArrayList<Long>();
		Iterator<Entry<Long, Window>> it = windows.entrySet().iterator();
		while (it.hasNext()) {
			// Get window information
			Entry<Long, Window> e = it.next();
			long context = e.getKey();
			Window window = e.getValue();

			// Set context
			GLFW.glfwMakeContextCurrent(context);

			// Render window
			try {
				window.render();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// Close window
			if (GLFW.glfwWindowShouldClose(context)) {
				windowsToClose.add(context);
			}
		}

		// Close all windows that need to close
		for (int i = 0; i < windowsToClose.size(); i++) {
			long handle = windowsToClose.get(i);
			Window win = windows.remove(handle);
			GLFW.glfwMakeContextCurrent(handle);
			win.dispose();
			if (!win.isExternalWindow()) {
				Callbacks.glfwFreeCallbacks(handle);
				GLFW.glfwDestroyWindow(handle);
			}
		}
		
		// Execute Runnables
		while (!tasks.isEmpty())
			tasks.poll().callI();

		GLFW.glfwMakeContextCurrent(currentContext);
	}

	public static void dispose() {
		Iterator<Entry<Long, Window>> it = windows.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Long, Window> e = it.next();
			long handle = e.getKey();
			Window win = e.getValue();
			GLFW.glfwMakeContextCurrent(handle);
			win.dispose();
			if (!win.isExternalWindow()) {
				Callbacks.glfwFreeCallbacks(handle);
				GLFW.glfwDestroyWindow(handle);
			}
		}
		Font.dispose();
	}

	public static Window getWindowFromContext(long context) {
		return windows.get(context);
	}

	/**
	 * Runs the specified runnable object at the end of the current LWJGUI frame.
	 * @param runnable
	 */
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
		if (t == null)
			return null;
		tasks.add(t);
		return t;
	}

	/**
	 * Returns the context of the current LWJGUI window.
	 * @return
	 */
	public static Context getCurrentContext() {
		return currentContext;
	}

	/**
	 * Sets which context LWJGUI is currently modifying.
	 * @param context
	 */
	public static void setCurrentContext(Context context) {
		currentContext = context;
	}
}
