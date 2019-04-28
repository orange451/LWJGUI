package lwjgui;

import lwjgui.geometry.Insets;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.ScrollPane;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.VBox;

public class ScrollPaneExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(8,8,8,8));
		
		// Create a scrollpane
		ScrollPane p = new ScrollPane();
		p.setPrefSize(150, 150);
		pane.setCenter(p);
		
		// Fill it with elements,
		VBox test = new VBox();
		for (int i = 1; i < 30; i++) {
			Label l = new Label("Test label " + i);
			test.getChildren().add(l);
		}
		p.setContent(test);
		
		// Create a button
		Button button = new Button("Click me");
		button.setOnAction((event)->{
			test.getChildren().add(new Label("WOAH THIS IS SOME REALLY LONG TEXT!"));
		});
		pane.setBottom(button);
		

		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}