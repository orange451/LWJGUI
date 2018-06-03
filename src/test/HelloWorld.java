package test;

import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Scene;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;

public class HelloWorld {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("LWJGUI Window", WIDTH, HEIGHT);
		
		// Initialize lwjgui for this window
		Scene scene = LWJGUI.initialize(window);
		
		// Add some components
		addComponents(scene);
		
		// Game Loop
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Clear screen
			glClearColor(0,0,0,0);
			glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
			
			// poll events to callbacks
			glfwPollEvents();
			
			// Render GUI
			LWJGUI.render();

			// Draw
			glfwSwapBuffers(window);
		}
		
		glfwDestroyWindow(window);
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		// Create a simple pane
		StackPane pane = new StackPane();
		
		// Add the pane to the scene
		scene.setRoot(pane);
		
		// Put a label in the pane
		pane.getChildren().add(new Label("Hello World!"));
	}
}