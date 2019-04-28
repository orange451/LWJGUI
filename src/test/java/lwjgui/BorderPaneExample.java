package lwjgui;

import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.BorderPane;

public class BorderPaneExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create a simple pane
		BorderPane pane = new BorderPane();
		
		// Put labels in the pane
		pane.setTop(new Label("top"));
		pane.setBottom(new Label("bottom"));
		pane.setCenter(new Label("center"));
		pane.setLeft(new Label("left"));
		pane.setRight(new Label("right"));
		
		// Set the scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}