package lwjgui;

import lwjgui.Color;
import lwjgui.scene.Window;
import lwjgui.scene.layout.HBox;
import lwjgui.scene.layout.StackPane;

public class HBoxExample2 extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create a hbox that fills the screen
		HBox box = new HBox();
		window.getScene().setRoot(box);
		
		// This is the left pane
		StackPane p1 = new StackPane();
		p1.setFillToParentHeight(true);
		p1.setMaxWidth(64);
		p1.setMinWidth(64);
		p1.setBackground(Color.GREEN);
		box.getChildren().add(p1);
		
		// This is the middle pane. It fills the whole width
		StackPane p2 = new StackPane();
		p2.setFillToParentWidth(true); // default: true
		p2.setFillToParentHeight(true);
		p2.setMinWidth(32);
		p2.setBackground(Color.PINK);
		box.getChildren().add(p2);
		
		// This is the right pane
		StackPane p3 = new StackPane();
		p3.setFillToParentHeight(true);
		p3.setMaxWidth(64);
		p3.setMinWidth(64);
		p3.setBackground(Color.CYAN);
		box.getChildren().add(p3);
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "HBox Example 2";
	}

	@Override
	public int getDefaultWindowWidth() {
		return WIDTH;
	}

	@Override
	public int getDefaultWindowHeight() {
		return HEIGHT;
	}
}