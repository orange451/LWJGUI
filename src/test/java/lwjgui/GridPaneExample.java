package lwjgui;

import lwjgui.LWJGUIDialog.DialogIcon;
import lwjgui.LWJGUIDialog.DialogType;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.PasswordField;
import lwjgui.scene.control.TextField;
import lwjgui.scene.layout.GridPane;
import lwjgui.scene.layout.StackPane;

public class GridPaneExample extends LWJGUIApplication {
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
		grid.setHgap(6);
		grid.setVgap(6);
		grid.add(new Label("Username:"), 0, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(new TextField(), 1, 0);
		grid.add(new PasswordField(), 1, 1);
		Button b = new Button("Login");
		b.setAlignment(Pos.CENTER_RIGHT);
		grid.add(b, 1, 3);
		root.getChildren().add(grid);
		
		// Set the scene
		window.setScene(new Scene(root, WIDTH, HEIGHT));
		window.show();
		
		// Make dialog box when login button is pressed
		b.setOnAction((event)->{
			LWJGUIDialog.showConfirmDialog("Login", 
					"You have logged in:"
					+ "\nUsername: "+((TextField)grid.get(1,0)).getText()
					+ "\nPassword: "+((PasswordField)grid.get(1,1)).getText(),
					DialogType.OK, DialogIcon.INFORMATION, true);
		});
	}

	@Override
	protected void run() {
		//
	}
}