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
import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.Scene;
import lwjgui.geometry.Pos;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;

public class HBoxExample2 {
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
		box.setAlignment(Pos.TOP_LEFT);
		StackPane p1 = new StackPane();
		p1.setFillToParentWidth(false);
		p1.setMaxWidth(64);
		p1.setMinWidth(64);
		p1.setBackground(Color.GREEN);
		StackPane p2 = new StackPane();
		p2.setFillToParentWidth(true); // Default: true
		p2.setMinWidth(32);
		p2.setBackground(Color.PINK);
		StackPane p3 = new StackPane();
		p3.setFillToParentWidth(false);
		p3.setMaxWidth(64);
		p3.setMinWidth(64);
		p3.setBackground(Color.CYAN);
		box.getChildren().add(p1);
		box.getChildren().add(p2);
		box.getChildren().add(p3);
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