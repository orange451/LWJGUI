package lwjgui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import lwjgui.scene.Scene;

public class LWJGUI {
	private static HashMap<Long, LWJGUIWindow> windows;
	private static boolean initialized = false;
	private static List<Runnable> runnables = Collections.synchronizedList(new ArrayList<Runnable>());
	
	public static Scene initialize(long window) {
		if ( !initialized ) {
			initialized = true;
			windows = new HashMap<Long, LWJGUIWindow>();
		}
		
		Context context = new Context(window);
		Scene scene = new Scene();
		windows.put(window, LWJGUIWindow.newWindow(context, scene));
		
		return scene;
	}

	public static void render() {
		long contextId = GLFW.glfwGetCurrentContext();
		LWJGUIWindow window = getWindowFromContext(contextId);
		if ( window != null ) {
			window.render();
		}
		
		synchronized(runnables) {
			for (int i = 0; i < runnables.size(); i++) {
				runnables.get(i).run();
			}
			runnables.clear();
		}
	}

	public static LWJGUIWindow getWindowFromContext(long context) {
		return windows.get(context);
	}

	public static void runLater(Runnable runnable) {
		synchronized(runnables) {
			runnables.add(runnable);
		}
	}
}
