package lwjgui;

import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class ButtonExample2 extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		StackPane pane = new StackPane();
		
		// Create a horizontal layout
		HBox hbox = new HBox();
		hbox.setSpacing(8);
		pane.getChildren().add(hbox);
		
		// Fill the horizontal layout with 3 buttons
		for (int i = 0; i < 3; i++) {
			VBox vbox = new VBox();
			vbox.setSpacing(8);
			vbox.setAlignment(Pos.CENTER);
			hbox.getChildren().add(vbox);
	
			final Label label = new Label("");
			label.setAlignment(Pos.BOTTOM_CENTER);
			
			Button button = new Button("No Click");
			button.setOnAction( (event)-> {
				label.setText("No Means No!!");
			});
			
			vbox.getChildren().add(button);
			vbox.getChildren().add(label); // Needs to be added after button so its underneath
		}

		// Set the scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}