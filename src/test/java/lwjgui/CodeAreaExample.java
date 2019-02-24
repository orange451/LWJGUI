package lwjgui;

import lwjgui.scene.Window;
import lwjgui.scene.control.text_input.CodeArea;
import lwjgui.scene.layout.BorderPane;

public class CodeAreaExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		BorderPane pane = new BorderPane();
		window.getScene().setRoot(pane);
		
		// Create code area
		CodeArea c = new CodeArea();
		c.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		pane.setCenter(c);
		
		// Add some text
		c.setText("printf(\"Hello World\");");
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "Text Area Example";
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