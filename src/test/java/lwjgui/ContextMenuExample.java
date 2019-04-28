package lwjgui;

import lwjgui.geometry.Pos;
import lwjgui.paint.Color;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.ContextMenu;
import lwjgui.scene.control.Label;
import lwjgui.scene.control.Menu;
import lwjgui.scene.control.MenuBar;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.shape.Circle;
import lwjgui.scene.shape.Rectangle;

public class ContextMenuExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		BorderPane background = new BorderPane();
		background.setAlignment(Pos.CENTER);
		
		
		// Top part of borderpane
		{
			// Create Menu Bar
			MenuBar bar = new MenuBar();
			background.setTop(bar);
			
			// Create File Menu
			Menu file = new Menu("File");
			file.getItems().add(new MenuItem("New"));
			file.getItems().add(new MenuItem("Open"));
			file.getItems().add(new MenuItem("Save"));
			bar.getItems().add(file);
			
			// Create Edit Menu
			Menu edit = new Menu("Edit");
			edit.getItems().add(new MenuItem("Undo"));
			edit.getItems().add(new MenuItem("Redo"));
			bar.getItems().add(edit);
		}
		
		
		// Center part of borderpane
		{
			// Create context menu
			ContextMenu menu = new ContextMenu();
			menu.getItems().add(new MenuItem("Option 1", new Rectangle(16, 16, 4, Color.RED)));
			menu.getItems().add(new MenuItem("Option 2", new Rectangle(16, 16, 4, Color.BLUE)));
			menu.getItems().add(new MenuItem("Option 3", new Circle(8, Color.RED)));
			menu.setAutoHide(false);
			
			// Create context node
			Label l = new Label("Don't right click me");
			l.setContextMenu(menu);
			background.setCenter(l);
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