package temp;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.RadioButton;
import lwjgui.scene.control.Tab;
import lwjgui.scene.control.TabPane;
import lwjgui.scene.control.ToggleGroup;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;
import lwjgui.scene.shape.Rectangle;

public class TabPaneExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Tab Pane Example", WIDTH, HEIGHT, true, false);
		
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
		// Create a simple pane as root
		StackPane root = new StackPane();
		scene.setRoot(root);
		
		// Create a tabpane
		TabPane tabPane = new TabPane();
		root.getChildren().add(tabPane);
		
		// Create a tab
		{
			Tab tab1 = new Tab();
			tab1.setText("Hello World");
			tabPane.getTabs().add(tab1);
			
			StackPane tabContent = new StackPane();
			tabContent.setAlignment(Pos.CENTER);
			tabContent.setFillToParentHeight(true);
			tabContent.setFillToParentWidth(true);
			tab1.setContent(tabContent);
			
			Button b = new Button("Hello World");
			tabContent.getChildren().add(b);
		}
		
		// Create another tab
		{
			Tab tab2 = new Tab();
			tab2.setText("Tab 2");
			tabPane.getTabs().add(tab2);
			
			StackPane tabContent = new StackPane();
			tabContent.setAlignment(Pos.CENTER);
			tabContent.setFillToParentHeight(true);
			tabContent.setFillToParentWidth(true);
			tab2.setContent(tabContent);
			
			VBox box = new VBox();
			box.setSpacing(4);
			box.setAlignment(Pos.TOP_LEFT);
			tabContent.getChildren().add(box);
			
			ToggleGroup g = new ToggleGroup();

			RadioButton b1 = new RadioButton("Male", g);
			RadioButton b2 = new RadioButton("Female", g);
			RadioButton b3 = new RadioButton("Some other gender from the list of 56", g);
			
			box.getChildren().add(b1);
			box.getChildren().add(b2);
			box.getChildren().add(b3);
		}
	}
}