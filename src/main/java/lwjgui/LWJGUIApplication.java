package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import org.lwjgl.glfw.GLFW;

import lwjgui.scene.Window;

/**
 * A utility class that quickly assembles a LWJGUI program. To implement this into your project, simply extend this class and call startProgram() in the main method.
 */
public abstract class LWJGUIApplication {
	/**
	 * Starts the given LWJGUI-based program.
	 * The entry point of the program is the same class that calls this method.
	 * 
	 * @param args - the args passed through the main method
	 */
	public static void launch(String[] args) {
		// Figure out the right class to call
		StackTraceElement[] cause = Thread.currentThread().getStackTrace();

		boolean foundThisMethod = false;
		String callingClassName = null;
		for (StackTraceElement se : cause) {
			// Skip entries until we get to the entry for this class
			String className = se.getClassName();
			String methodName = se.getMethodName();
			if (foundThisMethod) {
				callingClassName = className;
				break;
			} else if (LWJGUIApplication.class.getName().equals(className) && "launch".equals(methodName)) {
				foundThisMethod = true;
			}
		}

		if (callingClassName == null) {
			throw new RuntimeException("Error: unable to determine main class");
		}

		try {
			Class<?> theClass = Class.forName(callingClassName, true, Thread.currentThread().getContextClassLoader());
			launch((LWJGUIApplication) theClass.newInstance(),args);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts the given LWJGUI-based program.
	 * 
	 * @param program - a class that extends this one, meant to be the root of the program
	 * @param args - the args passed through the main method
	 */
	public static void launch(LWJGUIApplication program, String[] args) {
		//Restarts the JVM if necessary on the first thread to ensure Mac compatibility
		if (LWJGUIUtil.restartJVMOnFirstThread(true, program.getClass(), args)) {
			return;
		}
		
		//Fail to start the program if GLFW can't be initialized
		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

		//Create a standard opengl 3.2 window.
		long windowID = LWJGUIUtil.createOpenGLCoreWindow(program.getProgramName(), program.getDefaultWindowWidth(), program.getDefaultWindowHeight(), true, false);
	
		//Initialize LWJGUI for this window ID.
		Window window = LWJGUI.initialize(windowID);
		
		//Initialize the program
		program.start(args, window);
		
		//Run the program
		program.loop(window);
				
		//Stop GLFW after the window closes.
		glfwTerminate();
	}
	
	private void loop(Window window) {
		//Software loop
		while (!GLFW.glfwWindowShouldClose(window.getContext().getWindowHandle())) {
			//Run the program
			run();
			
			//Render the program
			LWJGUI.render();
		}
	}
	
	/**
	 * Called after the basic GLFW/LWJGUI initialization is completed and before the program loop is started.
	 * 
	 * @param args - the args passed through the main method
	 * @param window - the newly created LWGUI window
	 */
	public abstract void start(String[] args, Window window);

	/**
	 * Called from the program loop before rendering.
	 */
	public abstract void run();
	
	/**
	 * Gets the title for the LWJGUI window.
	 * 
	 * @return the name of this program.
	 */
	public abstract String getProgramName();
	
	/**
	 * Gets the starting width for the LWJGUI window.
	 * 
	 * @return - the desired starting window width
	 */
	public abstract int getDefaultWindowWidth();
	
	/**
	 * Gets the starting height for the LWJGUI window.
	 * 
	 * @return - the desired starting window height
	 */
	public abstract int getDefaultWindowHeight();
}
