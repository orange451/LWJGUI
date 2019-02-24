package lwjgui;

import lwjgui.Color;
import lwjgui.LWJGUI;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Window;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.StackPane;

public class PanesInPanes extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		StackPane background = new StackPane();
		background.setAlignment(Pos.CENTER);
		background.setFillToParentHeight(true);
		background.setFillToParentWidth(true);
		background.setPadding(new Insets(4,4,4,4));
		window.getScene().setRoot(background);
		
		StackPane pane = new StackPane();
		pane.setFillToParentHeight(true);
		pane.setPrefWidth(150);
		pane.setMinWidth(100);
		pane.setPadding(new Insets(4,4,4,4));
		pane.setAlignment(Pos.TOP_LEFT);
		pane.setBackground(Color.LIGHT_GRAY);
		background.getChildren().add(pane);
		
		StackPane pane2 = new StackPane();
		pane2.setAlignment(Pos.BOTTOM_RIGHT);
		pane2.setPrefSize(200, 100);
		pane2.setPadding(new Insets(4,4,4,4));
		pane2.setBackground(Color.DARK_GRAY);
		pane.getChildren().add(pane2);
		
		Label label = new Label("Hello World! LONG TEXT");
		label.setTextFill(Color.WHITE_SMOKE);
		pane2.getChildren().add(label);
		
		
		LWJGUI.runLater(() -> {
			System.out.println(pane2.getWidth());
		});
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "Panes In Panes";
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