package lwjgui;

import lwjgui.LWJGUIApplication;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.*;
import lwjgui.scene.layout.*;
import lwjgui.scene.control.*;

public class ColorPickerExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {
		// Create a simple root pane
		StackPane pane = new StackPane();
		pane.setPadding(new Insets(8,0,0,0));
		pane.setAlignment(Pos.TOP_CENTER);
		
		// Put a label in the pane
		pane.getChildren().add(new ColorPicker());
		
		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}
}