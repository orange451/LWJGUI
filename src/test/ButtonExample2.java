package test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.LWJGUIWindow;
import lwjgui.event.ButtonEvent;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class ButtonExample2 {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Button Example HBox", WIDTH, HEIGHT, true, false);
		
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
		StackPane pane = new StackPane();
		scene.setRoot(pane);
		
		// Create a horizontal layout
		HBox hbox = new HBox();
		hbox.setSpacing(4);
		pane.getChildren().add(hbox);
		
		// Fill the horizontal layout with 3 buttons
		for (int i = 0; i < 3; i++) {
			VBox vbox = new VBox();
			vbox.setSpacing(8);
			vbox.setPadding(new Insets(4,4,4,4));
			vbox.setAlignment(Pos.CENTER);
			hbox.getChildren().add(vbox);
	
			final Label label = new Label("");
			label.setAlignment(Pos.BOTTOM_CENTER);
			
			Button button = new Button("No Click");
			button.setOnAction(new ButtonEvent() {
				@Override
				public void onEvent() {
					label.setText("No Means No!!");
				}
			});
			
			vbox.getChildren().add(button);
			vbox.getChildren().add(label); // Needs to be added after button so its underneath
		}
		
	}
}