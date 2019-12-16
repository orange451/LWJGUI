package lwjgui;

import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.floating.FloatingPane;

public class FloatingPaneTest extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create a simple pane
		StackPane pane = new StackPane();
		pane.setPrefSize(512, 512);
		
		// Create a new floating pane
		FloatingPane floatPane = new FloatingPane();
		floatPane.setBackgroundLegacy(Color.GREEN);
		floatPane.setPrefSize(64, 64);
		pane.getChildren().add(floatPane);
		
		// Put pane in center of screen
		floatPane.setAbsolutePosition(WIDTH/2 - floatPane.getWidth()/2, HEIGHT/2 - floatPane.getHeight()/2);
		
		// Put a pane in to stretch it
		FloatingPane t = new FloatingPane();
		t.setBackgroundLegacy(Color.RED);
		t.setPrefSize(64, 64);
		t.setAbsolutePosition(floatPane.getX()+16, floatPane.getY()+32);
		floatPane.getChildren().add(t);
		
		// Put a label in the floating pane
		t.getChildren().add(new Label("Hello World!"));
		
		// Set the scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}