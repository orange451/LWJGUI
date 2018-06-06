package test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.event.ButtonEvent;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class ButtonExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
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
		
		// Stop GLFW
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		// Add background pane
		StackPane pane = new StackPane();
		scene.setRoot(pane);
		
		// Create vertical box
		VBox vbox = new VBox();
		vbox.setSpacing(8);
		vbox.setAlignment(Pos.CENTER);
		pane.getChildren().add(vbox);

		// Create the button for the box
		final Label label = new Label();
		Button button = new Button("Click Me!");
		button.setOnAction(new ButtonEvent() {
			@Override
			public void onEvent() {
				label.setText("Please don't press me :(");
			}
		});
		
		// Add the components
		vbox.getChildren().add(button);
		vbox.getChildren().add(label); 	// The reason we add the label here and not in the event
										// Is because that will affect the layout of the button.
										// Stylistically, we don't want the button to move when 
										// the text comes onto the screen.
	}
}