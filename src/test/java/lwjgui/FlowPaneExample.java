package lwjgui;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Orientation;
import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.layout.FlowPane;
import lwjgui.scene.layout.StackPane;

public class FlowPaneExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		StackPane background = new StackPane();
		background.setPadding(new Insets(32));
		
		// Add flow pane
		FlowPane flow = new FlowPane(Orientation.HORIZONTAL);
		flow.setFillToParentHeight(true);
		flow.setFillToParentWidth(true);
		background.getChildren().add(flow);
		
		// Add some kids
		for (int i = 0; i < 64; i++) {
			StackPane pane = new StackPane();
			pane.setPrefSize(18, 18);
			pane.setBackgroundLegacy(Color.RED);
			flow.getItems().add(pane);
		}
		
		// Set the scene
		window.setScene(new Scene(background, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}