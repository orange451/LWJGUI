package lwjgui;


import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Tab;
import lwjgui.scene.control.TabPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.shape.Circle;
import lwjgui.scene.shape.Rectangle;

public class TabPaneExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Hello World", WIDTH, HEIGHT, true, false);
		
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
		// Create a simple root pane
		StackPane pane = new StackPane();
		scene.setRoot(pane);
		
		// Tab Root Pane
		TabPane tabs = new TabPane();
		pane.getChildren().add(tabs);
		
		// Tab 1
		{
			Tab tab = new Tab("Tab 1");
			tab.setContent(new Circle(64));
			tabs.getTabs().add(tab);
		}
		
		// Tab 2
		{
			Tab tab = new Tab("Tab 2");
			tab.setContent(new Rectangle(96, 96, 8, Color.RED));
			tabs.getTabs().add(tab);
		}
	}
}