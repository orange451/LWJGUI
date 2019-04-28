package lwjgui;

import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;

public class HBoxExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		StackPane background = new StackPane();
		
		// Create horizontal layout
		HBox box = new HBox();
		box.setSpacing(8);
		background.getChildren().add(box);
		
		// Add some components to it
		box.getChildren().add(new Label("Label 1"));
		box.getChildren().add(new Label("Label 2"));
		box.getChildren().add(new Label("Label 3"));
		
		// Set the scene
		window.setScene(new Scene(background, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}