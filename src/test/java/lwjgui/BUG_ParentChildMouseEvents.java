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
 * This class showcases a bug where onMouseExited is called on a parent node when the mouse enters a child node. In JavaFX, onMouseExited isn't called unless the mouse truly leaves the parents
 * bounds. 
 * 
 * The best solution to this issue is likely to create a -list- of hovered nodes, rather than just forcing only one at a time. That way you can manage onMouseEntered/Exited calls for multiple
 * nodes at a time.
 *
 */
public class BUG_ParentChildMouseEvents {
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
		t.setOnMouseEntered(e -> {
			System.out.println("t entered");
		});
		t.setOnMouseExited(e -> {
			System.out.println("t exited");
		});
		t.setBackground(Color.RED);
		t.setPrefSize(64, 64);
		t.setAbsolutePosition(floatPane.getX()+16, floatPane.getY()+32);
		floatPane.getChildren().add(t);
		
		// Put a label in the floating pane
		Label label = new Label("Hello World!");
		label.setOnMouseEntered(e -> {
			System.out.println("label entered");
		});
		label.setOnMouseExited(e -> {
			System.out.println("label exited");
		});
		
		t.getChildren().add(label);
	}
}