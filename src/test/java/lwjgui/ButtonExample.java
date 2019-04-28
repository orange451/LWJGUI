package lwjgui;

import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class ButtonExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {
		// Create the root pane
		StackPane pane = new StackPane();
		
		// Create vertical box
		VBox vbox = new VBox();
		vbox.setSpacing(8);
		vbox.setAlignment(Pos.CENTER);
		pane.getChildren().add(vbox);

		// Create the button for the box
		final Label label = new Label();
		Button button = new Button("Click Me!");
		button.setOnAction( (event)-> {
			label.setText("Please don't press me :(");
		});
		
		// Add the components
		vbox.getChildren().add(button);
		vbox.getChildren().add(label); 	// The reason we add the label here and not in the event
										// Is because that will affect the layout of the button.
										// Stylistically, we don't want the button to move when 
										// the text comes onto the screen.
		
		// Set the scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		// TODO Auto-generated method stub
		
	}
}