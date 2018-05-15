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
import lwjgui.Scene;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;

public class HBoxExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window.
		long window = LWJGUIUtil.createOpenGLCoreWindow("LWJGUI Window", WIDTH, HEIGHT);
		
		// Initialize lwjgui for this window
		Scene scene = LWJGUI.initialize(window);
		
		// Add some components
		HBox box = new HBox();
		box.setSpacing(8);
		box.getChildren().add(new Label("Label 1"));
		box.getChildren().add(new Label("Label 2"));
		box.getChildren().add(new Label("Label 3"));
		scene.getChildren().add(box);
		
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Clear screen
			glClearColor(0,0,0,0);
			glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
			
			LWJGUI.render();

			// poll events to callbacks
			glfwPollEvents();
			glfwSwapBuffers(window);
		}
		
		glfwDestroyWindow(window);
		glfwTerminate();
	}
}