package lwjgui;

import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.layout.StackPane;
import lwjgui.style.BoxShadow;

public class BoxShadowTest extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {		
		// Create a simple root pane
		StackPane pane = new StackPane();
		
		// Create yellow pane
		StackPane t = new StackPane();
		t.setBackgroundLegacy(Color.YELLOW);
		t.setPrefSize(200, 100);
		t.setBorderRadii(3);
		pane.getChildren().add(t);
		
		// Add drop shadow
		t.getBoxShadowList().add(new BoxShadow(20, 20, 50, Color.GRAY));
		
		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}
}