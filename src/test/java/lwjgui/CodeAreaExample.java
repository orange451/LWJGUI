package lwjgui;

import lwjgui.geometry.Insets;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.CodeArea;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.Pane;
import lwjgui.scene.layout.StackPane;

public class CodeAreaExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		Pane t = new StackPane();
		t.setPadding(new Insets(32));
		
		// Create code area
		CodeArea c = new CodeArea();
		c.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		c.setFillToParentHeight(true);
		c.setFillToParentWidth(true);
		t.getChildren().add(c);
		
		// Add some text
		c.setText("printf(\"Hello World\");");
		
		// Set the scene
		window.setScene(new Scene(t, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}