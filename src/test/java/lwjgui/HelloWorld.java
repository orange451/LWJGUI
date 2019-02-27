package lwjgui;

import lwjgui.LWJGUIApplication;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;

public class HelloWorld extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {		
		// Create a simple pane
		StackPane pane = new StackPane();
		
		// Put a label in the pane
		pane.getChildren().add(new Label("Hello World!"));
		
		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "Hello World Application";
	}
}