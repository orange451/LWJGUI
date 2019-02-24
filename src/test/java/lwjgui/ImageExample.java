package lwjgui;

import lwjgui.scene.Window;
import lwjgui.scene.image.Image;
import lwjgui.scene.image.ImageView;
import lwjgui.scene.layout.StackPane;

public class ImageExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create a simple pane
		StackPane pane = new StackPane();
		window.getScene().setRoot(pane);
		
		// Create an image
		Image img = new Image("lwjgui/flower.jpg");
		
		// Create a viewable pane for that image
		ImageView view = new ImageView();
		view.setImage(img);
		view.setMaintainAspectRatio(true);
		pane.getChildren().add(view);
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "Image Example";
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