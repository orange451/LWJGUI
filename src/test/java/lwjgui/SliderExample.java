package lwjgui;

import lwjgui.LWJGUIApplication;
import lwjgui.geometry.Orientation;
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
		Label label = new Label("(50.0, 50.0)");
		Slider sliderH = new Slider();
		sliderH.setBlockIncrement(10);
		
		Slider sliderV = new Slider();
		sliderV.setOrientation(Orientation.VERTICAL);
		sliderV.setBlockIncrement(2.0);
		
		// Update the label
		sliderH.setOnValueChangedEvent((event)-> {
			double x = ((int) (sliderH.getValue() * 100)) / 100d;
			double y = ((int) (sliderV.getValue() * 100)) / 100d;
			label.setText("(" + x + ", " + y + ")");
		});
		sliderV.setOnValueChangedEvent((event)-> {
			double x = ((int) (sliderH.getValue() * 100)) / 100d;
			double y = ((int) (sliderV.getValue() * 100)) / 100d;
			label.setText("(" + x + ", " + y + ")");
		});
		
		vbox.getChildren().add(sliderH);
		vbox.getChildren().add(sliderV);
		vbox.getChildren().add(label);
		
		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}