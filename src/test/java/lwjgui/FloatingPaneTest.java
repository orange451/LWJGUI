package lwjgui;

import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.floating.FloatingPane;
import lwjgui.scene.layout.StackPane;

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
		
		// Create a new floating pane
		FloatingPane floatPane = new FloatingPane();
		floatPane.setBackground(Color.GREEN);
		pane.getChildren().add(floatPane);
		
		// Put pane in center of screen
		floatPane.setAbsolutePosition(WIDTH/2, HEIGHT/2);
		
		// Put a pane in to stretch it
		FloatingPane t = new FloatingPane();
		t.setBackground(Color.RED);
		t.setPrefSize(64, 64);
		t.setAbsolutePosition(floatPane.getX()+16, floatPane.getY()+32);
		floatPane.getChildren().add(t);
		
		// Put a label in the floating pane
		t.getChildren().add(new Label("Hello World!"));
		
		// Set the scene
		window.setScene(new Scene(pane));
		window.show();
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "Floating Pane Test";
	}
}