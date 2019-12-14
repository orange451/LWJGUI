package lwjgui;

import lwjgui.geometry.Insets;
import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.layout.BlurPane;
import lwjgui.scene.layout.BorderPane;

public class BlurPaneExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		
		// Create a simple pane
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(16,16,16,16));
		root.setBackgroundLegacy(null);

		// Put a label in the pane
		BlurPane pane = new BlurPane();
		pane.setBackgroundLegacy(Color.GRAY);
		pane.setBorderRadii(8);
		pane.setPrefSize(150, 150);
		root.setCenter(pane);
		
		// Window will call this callback when it draws
		window.setRenderingCallback(new OpenGLExample.RenderingCallbackTest());
		
		// Set the scene
		window.setScene(new Scene(root, WIDTH, HEIGHT));
		window.show();
	}
}