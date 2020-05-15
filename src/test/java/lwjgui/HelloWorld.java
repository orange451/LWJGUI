package lwjgui;

import lwjgui.LWJGUIApplication;
import lwjgui.scene.*;
import lwjgui.scene.layout.*;
import lwjgui.scene.control.*;

public class HelloWorld extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {		
		// Create a simple root pane
		StackPane pane = new StackPane();
		
		// Put a label in the pane
		//pane.getChildren().add(new Label("Hello World!"));
		
		VBox a = new VBox();
		a.setOnKeyPressed((event) -> {
		    System.out.println("a=" + event + ", consumed=" + event.isConsumed());
		    event.consume();
		});
		TextField b = new TextField();
		b.setOnKeyPressed((event) -> {
		    System.out.println("b=" + event + ", consumed=" + event.isConsumed());
		    event.consume();
		});
		a.getChildren().add(b);
		pane.getChildren().add(a);
		
		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}
}