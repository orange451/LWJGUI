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
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.scene.layout.StackPane;

/**
 * This class showcases a bug where if a node is placed outside the parent node, its bounds are updated to reflect the change. Resulting in situations where the input 
 * of said nodes aren't detected.
 * 
 * Once the bug is fixed, t2 should print out its println() when you click on it, like t does.
 *
 */
public class BUG_FloatingPaneBounds {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Floating Pane", WIDTH, HEIGHT, true, false);
		
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
		// Create a simple pane
		StackPane pane = new StackPane();
		
		// Set the pane as the scenes root
		scene.setRoot(pane);
		
		// Create a new floating pane
		FloatingPane floatPane = new FloatingPane();
		floatPane.setBackground(Color.GREEN);
		pane.getChildren().add(floatPane);
		
		// Put pane in center of screen
		floatPane.setAbsolutePosition(WIDTH/2, HEIGHT/2);
		
		// Put a pane in to stretch it
		FloatingPane t = new FloatingPane();
		t.setOnMouseClicked(e ->{
			System.out.println("t clicked");
		});
		t.setBackground(Color.RED);
		t.setPrefSize(64, 64);
		t.setAbsolutePosition(floatPane.getX()+16, floatPane.getY()+32);
		floatPane.getChildren().add(t);
		
		//Put a pane to the left to mess up the bounding
		FloatingPane t2 = new FloatingPane();
		t2.setOnMouseClicked(e ->{
			System.out.println("t2 clicked");
		});
		t2.setBackground(Color.PINK);
		t2.setPrefSize(64, 64);
		t2.setAbsolutePosition(floatPane.getX() - 64, floatPane.getY());
		floatPane.getChildren().add(t2);
		
		// Put a label in the floating pane
		t.getChildren().add(new Label("Hello World!"));
	}
}