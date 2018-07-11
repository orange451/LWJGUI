package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.PasswordField;
import lwjgui.scene.control.TextField;
import lwjgui.scene.layout.GridPane;
import lwjgui.scene.layout.StackPane;

public class GridPaneExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Grid Pane Example", WIDTH, HEIGHT, true, false);
		
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
		// Create a simple pane as root
		StackPane root = new StackPane();
		scene.setRoot(root);
		
		GridPane grid = new GridPane();
		grid.setHgap(6);
		grid.setVgap(6);
		grid.add(new Label("Username:"), 0, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(new TextField(), 1, 0);
		grid.add(new PasswordField(), 1, 1);
		grid.add(new Button("Login"), 1, 3);
		root.getChildren().add(grid);
	}
}