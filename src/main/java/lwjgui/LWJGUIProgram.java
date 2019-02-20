package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import org.lwjgl.glfw.GLFW;

import lwjgui.scene.Window;

/**
 * A utility class that quickly assembles a LWJGUI program. To implement this into your project, simply extend this class and call startProgram() in the main method.
 */
public abstract class LWJGUIProgram {
	/**
	 * Starts the given LWJGUI-based program.
	 * 
	 * @param program - a class that extends this one, meant to be the root of the program
	 * @param args - the args passed through the main method
	 */
	public static void start(LWJGUIProgram program, String[] args) {
		//Restarts the JVM if necessary on the first thread to ensure Mac compatibility
		if (LWJGUIUtil.restartJVMOnFirstThread(true, args)) {
			return;
		}
		
		//Fail to start the program if GLFW can't be initialized
		if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

		//Create a standard opengl 3.2 window.
		long windowID = LWJGUIUtil.createOpenGLCoreWindow(program.getProgramName(), program.getDefaultWindowWidth(), program.getDefaultWindowHeight(), true, false);
	
		//Initialize LWJGUI for this window ID.
		Window window = LWJGUI.initialize(windowID);
		
		//Initialize the program
		program.init(args, window);
		
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
	public abstract void init(String[] args, Window window);

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
