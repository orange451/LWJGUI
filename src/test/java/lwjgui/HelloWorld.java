package lwjgui;


import lwjgui.LWJGUIProgram;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;

public class HelloWorld extends LWJGUIProgram {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init(String[] args, Window window) {
		// Get the main scene of the window
		Scene scene = window.getScene();

		// Mark window as non-resizable
		window.setResizible(false);
		
		// Create a simple pane
		StackPane pane = new StackPane();
		
		// Set the pane as the scenes root
		scene.setRoot(pane);
		
		// Put a label in the pane
		pane.getChildren().add(new Label("Hello World!"));
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "Hello World Application";
	}

	@Override
	public int getDefaultWindowWidth() {
		return WIDTH;
	}

	@Override
	public int getDefaultWindowHeight() {
		return HEIGHT;
	}
}