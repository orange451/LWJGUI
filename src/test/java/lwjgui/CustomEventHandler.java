package lwjgui;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUIApplication;
import lwjgui.event.listener.KeyListener;
import lwjgui.scene.*;
import lwjgui.scene.layout.*;
import lwjgui.scene.control.*;

public class CustomEventHandler extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {		
		// Create a simple root pane
		StackPane pane = new StackPane();
		
		// Put a label in the pane
		Label l = new Label("Hello World");
		pane.getChildren().add(l);
		
		// Add custom key listener to window
		window.addEventListener(new KeyListener() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods, boolean isCtrlDown, boolean isAltDown, boolean isShiftDown) {
				System.out.println("User pressed " + GLFW.glfwGetKeyName(key, scancode) + "(" + key + ")" + " key!");
				
				if ( action == GLFW.GLFW_PRESS )
					l.setText(l.getText() + (char)key);
			}
		});
		
		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}
}