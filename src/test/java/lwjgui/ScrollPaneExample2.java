package lwjgui;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.ScrollPane;
import lwjgui.scene.control.ScrollPane;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.VBox;

public class ScrollPaneExample2 extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		VBox pane = new VBox();
		
		pane.getChildren().add(new Label("TESTING 123"));
		pane.getChildren().add(new Label("TESTING 123"));
		pane.getChildren().add(new Label("TESTING 123"));
		
		// Create a scrollpane
		ScrollPane p = new ScrollPane();
		p.setPrefSize(200, 200);
		pane.getChildren().add(p);
		
		// Fill it with elements,
		VBox test = new VBox();
		test.setAlignment(Pos.TOP_LEFT);
		for (int i = 1; i < 30; i++) {
			Label l = new Label("Test label " + i);
			test.getChildren().add(l);
		}
		p.setContent(test);
		

		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}