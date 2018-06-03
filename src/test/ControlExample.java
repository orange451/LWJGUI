package test;

import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.control.CheckBox;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.RadioButton;
import lwjgui.scene.control.ToggleGroup;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class ControlExample {
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
			// Clear screen
			glClearColor(0,0,0,0);
			glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
			
			// poll events to callbacks
			glfwPollEvents();
			
			// Render GUI
			LWJGUI.render();
			
			// Draw
			glfwSwapBuffers(window);
		}
		
		glfwDestroyWindow(window);
		glfwTerminate();
	}
	
	private static VBox exampleBox(String title) {
		VBox pane = new VBox();
		pane.setSpacing(4);
		pane.setAlignment(Pos.CENTER_LEFT);
		
		Label t = new Label(title);
		pane.getChildren().add(t);
		
		return pane;
	}

	private static void addComponents(Scene scene) {
		// Create background pane
		StackPane background = new StackPane();
		scene.setRoot(background);
		
		// Create hbox used to store two control types
		HBox hbox = new HBox();
		hbox.setSpacing(32);
		background.getChildren().add(hbox);
		
		// Create a vertical box to hold checkboxes
		VBox pane = exampleBox("Check boxes:");
		hbox.getChildren().add(pane);
		
		// Various ways to add checkboxes
		CheckBox b = new CheckBox("Hello World");
		b.setChecked(true);
		pane.getChildren().add(b);
		pane.getChildren().add(new CheckBox("Testing"));
		pane.getChildren().add(new CheckBox("Check if you are cool!"));
		
		// Make the first checkbox selected
		LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext()).getContext().setSelected(b);
		

		
		// Create a vertical box to hold radio buttons
		VBox radio = exampleBox("Radio Buttons:");
		hbox.getChildren().add(radio);
		
		// Create radio options
		ToggleGroup g = new ToggleGroup(); // This prevents multiple radio options from being selected
		radio.getChildren().add(new RadioButton("Option 1", g));
		radio.getChildren().add(new RadioButton("Option 2", g));
		radio.getChildren().add(new RadioButton("Option 3", g));
		
		
	}
}