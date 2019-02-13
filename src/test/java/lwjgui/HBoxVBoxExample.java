package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class HBoxVBoxExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window.
		long window = LWJGUIUtil.createOpenGLCoreWindow("HBox Example", WIDTH, HEIGHT, true, false);
		
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
		StackPane background = new StackPane();
		scene.setRoot(background);
		
		// Create vertical layout
		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		box.setSpacing(8);
		background.getChildren().add(box);
		
		// Fill row
		for (int j = 0; j < 3; j++) {
			HBox hbox = new HBox();
			hbox.setSpacing(8);
			box.getChildren().add(hbox);
			
			for (int i = 0; i < 3; i++) {
				hbox.getChildren().add(new Label("Label "+(i+j*3)));
			}
		}
	}
}