package lwjgui;

import lwjgui.LWJGUIApplication;
import lwjgui.font.Font;
import lwjgui.paint.Color;
import lwjgui.scene.*;
import lwjgui.scene.layout.*;
import lwjgui.style.BackgroundSolid;
import lwjgui.scene.control.*;

public class FontTest extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {		
		// Create a simple root pane
		StackPane pane = new StackPane();
		
		Pane t =new StackPane();
		t.setBackground(new BackgroundSolid(Color.RED));
		Font font = new Font("lwjgui/dungeonfont.TTF");
		
		// Put a label in the pane
		Label label = new Label("Hello World!");
		label.setFontSize(32);
		label.setFont(font);
		t.getChildren().add(label);
		
		pane.getChildren().add(t);
		
		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}
}