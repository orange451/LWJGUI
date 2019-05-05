package lwjgui;

import lwjgui.geometry.Insets;
import lwjgui.scene.Scene;
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
		pane.setPadding(new Insets(8));
		
		// Create an image
		Image img = new Image("lwjgui/flower.jpg");
		
		// Create a viewable pane for that image
		ImageView view = new ImageView();
		view.setPrefSize(128, 128);
		view.setImage(img);
		view.setMaintainAspectRatio(true);
		pane.getChildren().add(view);
		
		// Set the scene
		window.setScene(new Scene(pane));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}