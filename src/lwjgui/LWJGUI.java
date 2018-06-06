package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;

import lwjgui.scene.Scene;

public class LWJGUI {
	private static HashMap<Long, LWJGUIWindow> windows = new HashMap<Long, LWJGUIWindow>();
	private static List<Runnable> runnables = Collections.synchronizedList(new ArrayList<Runnable>());
	
	public static Scene initialize(long window) {
		Context context = new Context(window);
		Scene scene = new Scene();
		windows.put(window, LWJGUIWindow.newWindow(context, scene));
		
		return scene;
	}

	public static void render() {
		// poll events to callbacks
		glfwPollEvents();
		
		// Render all windows
		ArrayList<Long> windowsToClose = new ArrayList<Long>();
		Iterator<Entry<Long, LWJGUIWindow>> it = windows.entrySet().iterator();
		synchronized(windows) {
			while ( it.hasNext() ) {
				// Get window information
				Entry<Long, LWJGUIWindow> e = it.next();
				long context = e.getKey();
				LWJGUIWindow window = e.getValue();
				
				// Set context
				GLFW.glfwMakeContextCurrent(context);
				
				// Close window
				if ( GLFW.glfwWindowShouldClose(context) ) {
					windowsToClose.add(context);
				}
	
				// Clear screen
				glClearColor(0,0,0,0);
				glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
				
				// Render window
				window.render();
	
				// Draw
				glfwSwapBuffers(context);
			}
		}
		
		// Close all windows that need to close
		for (int i = 0; i < windowsToClose.size(); i++) {
			GLFW.glfwDestroyWindow(windowsToClose.get(i));
			windows.remove(windowsToClose.get(i));
		}
		
		// Run runnables
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
