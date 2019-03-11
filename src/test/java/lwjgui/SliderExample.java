package lwjgui;

import lwjgui.LWJGUIApplication;
import lwjgui.geometry.Pos;
import lwjgui.scene.*;
import lwjgui.scene.layout.*;
import lwjgui.scene.control.*;

public class SliderExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {		
		// Create a simple pane
		StackPane pane = new StackPane();
		
		// Create vbox
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.CENTER);
		vbox.setSpacing(8);
		pane.getChildren().add(vbox);
		
		// Add slider/label combo
		Label label = new Label("50");
		Slider slider = new Slider();
		slider.setOnValueChangedEvent((event)-> {
			double val = ((int)(slider.getValue() * 100))/100d;
			label.setText(""+val);
		});
		vbox.getChildren().add(slider);
		vbox.getChildren().add(label);
		
		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "Hello World Application";
	}
}