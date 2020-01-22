package lwjgui;


import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.WindowManager;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;

public class HelloWorldManualNoUtil {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		
		// Initialize window manager
		WindowManager.init();

		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_FLOATING, GL_FALSE);
		
		// Create the window
		long handle = glfwCreateWindow(WIDTH, HEIGHT, "Hello World", NULL, NULL);
		if ( handle == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		
		// Finalize window
		glfwMakeContextCurrent(handle);
		GL.createCapabilities();
		
		// Initialize LWJGUI for this window
		Window window = WindowManager.generateWindow(handle);
		
		// Add some components
		StackPane pane = new StackPane();
		pane.getChildren().add(new Label("Hello World!"));
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Show Window
		window.show();
		
		// Game Loop
		while (!GLFW.glfwWindowShouldClose(handle)) {
			// Set context
			GLFW.glfwMakeContextCurrent(handle);
			
			// Tick window manager for any input or windowing commands
			WindowManager.update();
			
			// Render UI
			window.render();
			
			// Swap buffers
			glfwSwapBuffers(handle);
		}
		// Clear global window resources
		WindowManager.dispose();
		// Stop GLFW
		glfwTerminate();
	}
}