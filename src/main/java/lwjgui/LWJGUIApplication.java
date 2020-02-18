package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import lwjgui.glfw.ClientSync;
import lwjgui.scene.Window;
import lwjgui.scene.WindowManager;
import lwjgui.scene.WindowThread;

/**
 * A utility class that quickly assembles a LWJGUI program. To implement this into your project, simply extend this class and call startProgram() in the main method.
 */
public abstract class LWJGUIApplication {
	/**
	 * Flag for whether or not the application will try to start using OpenGL 3.2
	 */
	public static boolean ModernOpenGL = true;

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
		Object object = null;
		try {
			Class<?> theClass = Class.forName(callingClassName, true, Thread.currentThread().getContextClassLoader());
			object = theClass.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		launch((LWJGUIApplication) object,args);
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
		
		WindowManager.init();

		WindowThread thread = new WindowThread(100, 100, "lwjgui", !ModernOpenGL) {
			@Override
			protected void init(Window window) {
				super.init(window);
				//Initialize the program
				program.start(args, window);
			}
			@Override
			protected void update() {
				super.update();
				program.run();
			}
		};
		thread.start();
		
		//Run the program
		program.loop();

		program.dispose();

		//Stop GLFW after the window closes.
		glfwTerminate();
	}
	
	private void loop() {
		//Software loop
		ClientSync sync = new ClientSync();
		while (!WindowManager.isEmpty()) {
			WindowManager.update();
			sync.sync(120);
		}
	}

	private void dispose() {
		WindowManager.dispose();
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
	protected void run() {
		//
	}
}
