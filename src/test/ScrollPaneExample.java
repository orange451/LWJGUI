package test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.event.ButtonEvent;
import lwjgui.geometry.Insets;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.ScrollPane;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.OpenGLPane;
import lwjgui.scene.layout.VBox;

public class ScrollPaneExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Scroll Pane Example", WIDTH, HEIGHT, true, false);
		
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
		pane.setPadding(new Insets(8,8,8,8));
		scene.setRoot(pane);
		
		// Create a scrollpane
		ScrollPane p = new ScrollPane();
		p.setPrefSize(150, 150);
		pane.setCenter(p);
		
		// Fill it with elements,
		VBox test = new VBox();
		for (int i = 1; i < 30; i++) {
			Label l = new Label("Test label " + i);
			test.getChildren().add(l);
		}
		p.setContent(test);
		
		// Create a button
		Button button = new Button("Click me");
		button.setOnAction(new ButtonEvent() {
			@Override
			public void onEvent() {
				test.getChildren().add(new Label("WOAH THIS IS SOME REALLY LONG TEXT!"));
			}
		});
		pane.setBottom(button);
		
	}
}