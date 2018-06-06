package test;

import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import java.io.IOException;
import org.lwjgl.glfw.GLFW;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Scene;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;

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
		addComponents(scene);
		
		// Game Loop
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Render GUI
			LWJGUI.render();
		}
		
		glfwDestroyWindow(window);
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		// Create background pane
		StackPane background = new StackPane();
		scene.setRoot(background);
		
		// Create horizontal layout
		HBox box = new HBox();
		box.setSpacing(8);
		background.getChildren().add(box);
		
		// Add some components to it
		box.getChildren().add(new Label("Label 1"));
		box.getChildren().add(new Label("Label 2"));
		box.getChildren().add(new Label("Label 3"));
	}
}