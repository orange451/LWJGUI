package lwjgui;

import lwjgui.gl.OffscreenBuffer;
import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.layout.StackPane;
import lwjgui.style.BackgroundNVGImage;

public class BackgroundNVGImageExample extends LWJGUIApplication {
	public static final int WIDTH = 320;
	public static final int HEIGHT = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create a simple root pane
		StackPane pane = new StackPane();

		// FBO Setup
		{
			// Create offscreen buffer
			OffscreenBuffer buffer = new OffscreenBuffer(128, 128);
			buffer.bind();
			buffer.drawClearColor(Color.VIOLET);
			buffer.unbind();

			// Setup Background NVG Image
			pane.setBackground(BackgroundNVGImage.fromOffscreenBuffer(window, buffer));
		}

		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));

		// Make window visible
		window.show();
	}
}