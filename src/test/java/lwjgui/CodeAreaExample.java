package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.text_input.CodeArea;
import lwjgui.scene.layout.BorderPane;

public class CodeAreaExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Text Area Example", WIDTH, HEIGHT, true, false);
		
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
		// Create background pane
		BorderPane pane = new BorderPane();
		scene.setRoot(pane);
		
		// Create code area
		CodeArea c = new CodeArea();
		
		c.setOnDeselected(e -> {
			System.err.println("Deselected");
		});
		
		c.setOnSelected(e -> {
			System.err.println("Selected");
		});
		
		c.setText("printf(\"Hello World\");");
		//c.setPreferredColumnCount(28);
		//c.setPreferredRowCount(14);
		pane.setCenter(c);
	}
}