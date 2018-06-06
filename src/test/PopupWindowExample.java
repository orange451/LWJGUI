package test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import java.io.IOException;
import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.LWJGUIWindow;
import lwjgui.event.ButtonEvent;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class PopupWindowExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("LWJGUI Window", WIDTH, HEIGHT, false);
		
		// Initialize lwjgui for this window
		LWJGUIWindow newWindow = LWJGUI.initialize(window);
		Scene scene = newWindow.getScene();
		
		// Add some components
		addComponents(scene);
		
		// Game Loop
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Render GUIs
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
		Button button = new Button("Click Me!");
		button.setOnAction(new ButtonEvent() {
			@Override
			public void onEvent() {
				popup("Test Popup");
			}
		});
		
		// Add the components
		vbox.getChildren().add(button);
	}

	protected static void popup(String popup) {
		// Create a popup window
		long pWin = LWJGUIUtil.createOpenGLCoreWindow(popup, 300, 200, true);
		LWJGUIWindow newWindow = LWJGUI.initialize(pWin);
		newWindow.setCanUserClose(false); // Prevent user from xing out of window
		Scene scene = newWindow.getScene();
		
		// Create root pane
		StackPane root = new StackPane();
		scene.setRoot(root);
		
		// Create a vertical layout
		VBox vbox = new VBox();
		root.getChildren().add(vbox);
		
		// Create a label
		Label l = new Label("Congratulations, You've won!");
		vbox.getChildren().add(l);
		
		// Create a button
		Button b = new Button("Claim prize");
		vbox.getChildren().add(b);
		b.setOnAction(new ButtonEvent() {
			@Override
			public void onEvent() {
				GLFW.glfwSetWindowShouldClose(pWin, true);
			}
		});
	}
}