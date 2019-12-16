package lwjgui;

import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;

public class CSSExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {
		// Create a simple root pane
		StackPane pane = new StackPane();
		
		// Create pane to be styled! :)
		StackPane styledPane = new StackPane();
		styledPane.getClassList().add("TestStyle");
		pane.getChildren().add(styledPane);
		
		Label label = new Label("Click Me!");
		label.setTextFill(Color.WHITE);
		label.setMouseTransparent(true);
		styledPane.getChildren().add(label);
		
		// Apply some style!
		pane.setStylesheet("stackpane { background-color:rgb(240,240,240) }");
		styledPane.setStylesheet(""
				+ ".TestStyle {"
				+ "		background-color: #007bff;"
				+ "		border-style: solid;"
				+ "		border-radius: 4px;"
				+ "		border-color: #0865cc;"
				+ "		border-width: 1px;"
				+ "		box-shadow: none;"
				+ "		padding: 6px 16px 6px 16px;"
				+ "}"
				+ ""
				+ ".TestStyle:hover {"
				+ "		background-color: #1e86f7;"
				+ "		box-shadow: 8px 8px 32px;"
				+ "}"
				+ ""
				+ ".TestStyle:focus {"
				+ "		box-shadow: 0px 0px 0px 4px #007bff80;"
				+ "}"
				+ ""
				+ ".TestStyle:active {"
				+ "		background-color: #0e6bd2;"
				+ "}");
		
		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}
}