package lwjgui;

import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;
import lwjgui.style.BorderStyle;
import lwjgui.style.BoxShadow;

public class BoxShadowTest2 extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(String[] args, Window window) {		
		// Create a simple root pane
		StackPane pane = new StackPane();
		
		// Create blue pane
		StackPane t = new StackPane();
		t.setBackgroundLegacy(new Color("#007bff"));
		t.setPrefSize(100, 30);
		t.setBorderColor(new Color("#0f72dc"));
		t.setBorderRadii(4);
		t.setBorderWidth(1);
		t.setBorderStyle(BorderStyle.SOLID);
		pane.getChildren().add(t);
		
		// Add label
		Label l = new Label("Click me");
		l.setTextFill(Color.WHITE);
		t.getChildren().add(l);

		// Add some pretty styling
		t.getBoxShadowList().add(new BoxShadow(4, 4, 16, -1));
		t.getBoxShadowList().add(new BoxShadow(0, 0, 1, 4, new Color("#007bff").alpha(0.5f)));
		
		// Create a new scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		
		// Make window visible
		window.show();
	}
}