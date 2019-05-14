package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;

import lwjgui.scene.Context;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.layout.StackPane;

public class LWJGUI {
	private static HashMap<Long, Window> windows = new HashMap<Long, Window>();
	private static List<Runnable> runnables = Collections.synchronizedList(new ArrayList<Runnable>());
	protected static Context currentContext;
	
	/**
	 * Initializes a LWJGUI window from a GLFW window handle. The window contains a Scene class.<br>
	 * Rendering components can be added to the scene. However, to set initial
	 * rendering, the scene's root node must first be set.
	 * @param window
	 * @return Returns a LWJGUI Window that contains a rendering Context and a Scene.
	 */
	public static Window initialize(long window) {
		if ( windows.containsKey(window) ) {
			System.err.println("Failed to initialize this LWJGUI Window. Already initialized.");
			return null;
		}
		Context context = new Context(window);
		Scene scene = new Scene(new StackPane());
		Window wind = new Window(context, scene);
		windows.put(window, wind);
		
		currentContext = context;
		
		return wind;
	}

	/**
	 * This method renders all of the initialized LWJGUI windows.<br>
	 * It will loop through each window, set the context, and then render.<br>
	 * <br>
	 * You can set a rendering callback to a window if you want your own rendering
	 * at the start of a render-pass.
	 */
	public static void render() {
		// poll events to callbacks
		glfwPollEvents();
		
		// Render all windows
		ArrayList<Long> windowsToClose = new ArrayList<Long>();
		Iterator<Entry<Long, Window>> it = windows.entrySet().iterator();
		synchronized(windows) {
			while ( it.hasNext() ) {
				// Get window information
				Entry<Long, Window> e = it.next();
				long context = e.getKey();
				Window window = e.getValue();
				
				// Set context
				GLFW.glfwMakeContextCurrent(context);
				
				// Close window
				if ( GLFW.glfwWindowShouldClose(context) ) {
					windowsToClose.add(context);
				}
				
				// Render window
				try {
					window.render();
				}catch(Exception ex) {
					
				}
			}
		}
		
		// Close all windows that need to close
		for (int i = 0; i < windowsToClose.size(); i++) {
			GLFW.glfwDestroyWindow(windowsToClose.get(i));
			windows.remove(windowsToClose.get(i));
		}
		
		// Run runnables
		List<Runnable> newRunnable = new ArrayList<Runnable>();
		synchronized(runnables) {
			while(runnables.size()>0) { 
				newRunnable.add(runnables.get(0));
				runnables.remove(0);
			}
		}
		for (int i = 0; i < newRunnable.size(); i++) {
			newRunnable.get(i).run();
		}
		newRunnable.clear();
	}

	public static Window getWindowFromContext(long context) {
		return windows.get(context);
	}

	public static void runLater(Runnable runnable) {
		synchronized(runnables) {
			runnables.add(runnable);
		}
	}

	public static Context getCurrentContext() {
		return currentContext;
	}

	public static void setCurrentContext(Context context) {
		currentContext = context;
	}
}
