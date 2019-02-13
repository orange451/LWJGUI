package lwjgui;


import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.floating.DraggablePane;
import lwjgui.scene.layout.floating.PannablePane;
import lwjgui.scene.shape.Rectangle;

public class PannableExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Hello World", WIDTH, HEIGHT, true, false);
		
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
		// Root pane
		StackPane root = new StackPane();
		root.setAlignment(Pos.TOP_CENTER);
		scene.setRoot(root);
		
		// Create a pannable pane in the root
		PannablePane pane = new PannablePane();
		root.getChildren().add(pane);
		
		// Create a draggable pane
		DraggablePane floatPane = new DraggablePane();
		floatPane.setBackground(Color.green);
		floatPane.setPrefSize(64, 64);
		floatPane.getChildren().add(new Label("I'm draggable!"));
		pane.getChildren().add(floatPane);
		
		// Center the floating pane
		LWJGUI.runLater(()->{
			floatPane.offset(-floatPane.getWidth()/2, -floatPane.getHeight()/2);
		});

		// Create a second draggable pane
		DraggablePane d = new DraggablePane();
		d.setAbsolutePosition(32, 48);
		d.setBackground(Color.red);
		d.setPrefSize(64, 64);
		d.getChildren().add(new Label("Me too!"));
		pane.getChildren().add(d);
		
		// Overlapping UI
		{
			HBox ui = new HBox();
			ui.setBackground(null);
			ui.setPadding(new Insets(8, 8, 8, 8));
			root.getChildren().add(ui);
			
			Button b = new Button("Center");
			b.setBackground(Color.GREEN);
			ui.getChildren().add(b);
			
			b.setOnAction((event)->{
				pane.center();
			});
		}
		
		// This crosshair represents the center
		pane.getChildren().add(new Rectangle(2, 2));
		pane.getChildren().add(new Rectangle(1, 10));
		pane.getChildren().add(new Rectangle(10, 1));
	}
}