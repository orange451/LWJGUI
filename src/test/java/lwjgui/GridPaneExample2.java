package lwjgui;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.PasswordField;
import lwjgui.scene.control.TextField;
import lwjgui.scene.layout.ColumnConstraint;
import lwjgui.scene.layout.GridPane;
import lwjgui.scene.layout.Priority;
import lwjgui.scene.layout.StackPane;

public class GridPaneExample2 extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create a simple pane as root
		StackPane root = new StackPane();
		
		GridPane grid = new GridPane();
		grid.setPrefWidth(200);
		grid.setColumnConstraint(1, new ColumnConstraint(16, Priority.ALWAYS));
		grid.setHgap(6);
		grid.setVgap(6);
		grid.add(new Label("Test Label 1"), 0, 0);
		grid.add(new Label("Test Label 2"), 0, 1);
		grid.add(new Label("Test Label 3"), 0, 2);
		grid.add(new Label("Test Label 4"), 0, 3);
		grid.add(new Label("Test Label 5"), 0, 4);
		
		StackPane t = new StackPane();
		t.setBackgroundLegacy(Color.BLUE);
		t.setFillToParentWidth(true);
		t.setFillToParentHeight(true);
		grid.add(t, 1, 1);
		
		Button b = new Button("Click me pls");
		b.setFillToParentWidth(true);
		b.setPrefSize(0, 0);
		b.setPadding(Insets.EMPTY);
		grid.add(b, 1, 0);
		root.getChildren().add(grid);
		
		TextField f = new TextField();
		f.setPrefSize(0, 0);
		f.setFillToParentWidth(true);
		f.setFillToParentHeight(true);
		grid.add(f, 1, 2);
		
		// Set the scene
		window.setScene(new Scene(root, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}