package test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.image.Image;
import lwjgui.scene.image.ImageView;
import lwjgui.scene.layout.StackPane;

public class ImageExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Image Example", WIDTH, HEIGHT, true, false);
		
		// Initialize lwjgui for this window
		Window lwjguiWindow = LWJGUI.initialize(window);
		
		// Add some components
		addComponents(lwjguiWindow.getScene());
		
		// Game Loop
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Render GUI
			LWJGUI.render();
		}
		
		// Stop GLFW
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		// Create a simple pane
		StackPane pane = new StackPane();
		scene.setRoot(pane);
		
		// Create an image
		Image img = new Image("res/flower.jpg");
		
		// Create a viewable pane for that image
		ImageView view = new ImageView();
		view.setImage(img);
		view.setMaintainAspectRatio(true);
		pane.getChildren().add(view);
	}
}