package test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.LWJGUIWindow;
import lwjgui.scene.Scene;
import lwjgui.scene.control.ContextMenu;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.shape.Circle;
import lwjgui.scene.shape.Rectangle;

public class ContextMenuTest {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("LWJGUI Window", WIDTH, HEIGHT, false);
		
		// Initialize lwjgui for this window
		LWJGUIWindow newWindow = LWJGUI.initialize(window);
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
		// Create background pane
		StackPane background = new StackPane();
		scene.setRoot(background);
		
		// Create context menu
		ContextMenu menu = new ContextMenu();
		menu.getItems().add(new MenuItem("Option 1", new Rectangle()));
		menu.getItems().add(new MenuItem("Option 2", new Rectangle(4)));
		menu.getItems().add(new MenuItem("Option 3", new Circle()));
		menu.setAutoHide(false);
		
		// Create context node
		Label l = new Label("Don't right click me");
		l.setContextMenu(menu);
		background.getChildren().add(l);
	}
}