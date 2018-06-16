package test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.LWJGUIWindow;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;

public class PanesInPanes {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Panes in Panes", WIDTH, HEIGHT, true, false);
		
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
		StackPane background = new StackPane();
		background.setFillToParentHeight(true);
		background.setFillToParentWidth(true);
		background.setPadding(new Insets(4,4,4,4));
		scene.setRoot(background);
		
		StackPane pane = new StackPane();
		pane.setFillToParentHeight(true);
		pane.setPrefWidth(150);
		pane.setMinWidth(100);
		pane.setPadding(new Insets(4,4,4,4));
		pane.setAlignment(Pos.TOP_LEFT);
		pane.setBackground(Color.LIGHT_GRAY);
		background.getChildren().add(pane);
		
		StackPane pane2 = new StackPane();
		pane2.setAlignment(Pos.BOTTOM_RIGHT);
		pane2.setPrefSize(200, 100);
		pane2.setPadding(new Insets(4,4,4,4));
		pane2.setBackground(Color.DARK_GRAY);
		pane.getChildren().add(pane2);
		
		Label label = new Label("Hello World!");
		label.setTextFill(Color.WHITE_SMOKE);
		pane2.getChildren().add(label);
		
		LWJGUI.runLater(() -> {
			System.out.println(pane2.getWidth());
		});
	}
}