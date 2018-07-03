package test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;

public class HBoxExample2 {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window.
		long window = LWJGUIUtil.createOpenGLCoreWindow("HBox Example 2", WIDTH, HEIGHT, true, false);

		// Initialize lwjgui for this window
		Window newWindow = LWJGUI.initialize(window);
		Scene scene = newWindow.getScene();

		// Add some components
		addComponents(scene);
		
		// Game Loop
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Render GUI
			LWJGUI.render();
		}

		// Stop GLFW
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		// Create a hbox that fills the screen
		HBox box = new HBox();
		scene.setRoot(box);
		
		// This is the left pane
		StackPane p1 = new StackPane();
		p1.setFillToParentHeight(true);
		p1.setMaxWidth(64);
		p1.setMinWidth(64);
		p1.setBackground(Color.GREEN);
		box.getChildren().add(p1);
		
		// This is the middle pane. It fills the whole width
		StackPane p2 = new StackPane();
		p2.setFillToParentWidth(true); // default: true
		p2.setFillToParentHeight(true);
		p2.setMinWidth(32);
		p2.setBackground(Color.PINK);
		box.getChildren().add(p2);
		
		// This is the right pane
		StackPane p3 = new StackPane();
		p3.setFillToParentHeight(true);
		p3.setMaxWidth(64);
		p3.setMinWidth(64);
		p3.setBackground(Color.CYAN);
		box.getChildren().add(p3);
		
	}
}