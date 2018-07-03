package test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Pos;
import lwjgui.scene.Parent;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.CheckBox;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.RadioButton;
import lwjgui.scene.control.SegmentedButton;
import lwjgui.scene.control.ToggleButton;
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
		long window = LWJGUIUtil.createOpenGLCoreWindow("Control Buttons", WIDTH, HEIGHT, true, false);
		
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
		
		// Create a vbox to store examples vertically
		VBox vbox = new VBox();
		vbox.setSpacing(32);
		background.getChildren().add(vbox);
		
		{
			// Segmented Button
			displaySegmentedButton(vbox);
		}
		
		// Create hbox used to store two control types
		HBox hbox = new HBox();
		hbox.setSpacing(32);
		vbox.getChildren().add(hbox);
		
		{
			// Checkboxes
			displayCheckboxes(hbox);		
			
			// Redio Buttons
			displayRadioButtons(hbox);
		}
		
	}

	private static void displaySegmentedButton(Parent parent) {
		HBox t = new HBox();
		parent.getChildren().add(t);
		
		ToggleGroup g = new ToggleGroup();
		ToggleButton b1 = new ToggleButton("Day", g);
		ToggleButton b2 = new ToggleButton("Week", g);
		ToggleButton b3 = new ToggleButton("Month", g);
		ToggleButton b4 = new ToggleButton("Year", g);
		
		SegmentedButton b = new SegmentedButton(b1, b2, b3, b4);
		t.getChildren().add(b);
	}

	private static void displayRadioButtons(Parent parent) {
		// Create a vertical box to hold radio buttons
		VBox radio = exampleBox("Radio Buttons:");
		parent.getChildren().add(radio);
		
		// Create radio options
		ToggleGroup g = new ToggleGroup(); // This prevents multiple radio options from being selected
		radio.getChildren().add(new RadioButton("Option 1", g));
		radio.getChildren().add(new RadioButton("Option 2", g));
		radio.getChildren().add(new RadioButton("Option 3", g));
		
		// Force select the third toggle
		g.selectToggle(g.getToggles().get(2));
	}

	private static void displayCheckboxes(Parent parent) {
		// Create a vertical box to hold checkboxes
		VBox pane = exampleBox("Check boxes:");
		parent.getChildren().add(pane);
		
		// Various ways to add checkboxes
		CheckBox b = new CheckBox("Hello World");
		pane.getChildren().add(b);
		pane.getChildren().add(new CheckBox("Testing"));
		pane.getChildren().add(new CheckBox("Check if you are cool!"));
		
		// Make the first checkbox selected visually and checked
		b.setChecked(true);
		LWJGUI.getWindowFromContext(GLFW.glfwGetCurrentContext()).getContext().setSelected(b);
	}
}