package lwjgui;

import lwjgui.geometry.Orientation;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.SplitPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class SplitPaneExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		StackPane pane = new StackPane();
		
		// Create vertical layout
		VBox box = new VBox();
		box.setAlignment(Pos.CENTER);
		box.setFillToParentHeight(true);
		box.setFillToParentWidth(true);
		pane.getChildren().add(box);
		
		// Title label
		Label b = new Label("Split Pane Test");
		b.setFontSize(32);
		box.getChildren().add(b);
		
		// Create split pane
		SplitPane split = new SplitPane();
		split.setFillToParentHeight(true);
		split.setFillToParentWidth(true);
		split.setOrientation(Orientation.VERTICAL);
		box.getChildren().add(split);
		
		// Add some content
		for (int i = 0; i < 3; i++) {
			StackPane p = new StackPane();
			p.setAlignment(Pos.CENTER);
			split.getItems().add(p);
			
			p.getChildren().add(new Button("Hello World"));
		}

		// After it's all setup, make sizes not rely on window size.
		LWJGUI.runLater(()->{
			SplitPane.setResizableWithParent(split.getItems().get(0), false);
			SplitPane.setResizableWithParent(split.getItems().get(2), false);
		});
		
		// Set the scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}