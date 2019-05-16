package lwjgui;

import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.CodeArea;
import lwjgui.scene.control.ContextMenu;
import lwjgui.scene.control.MenuItem;
import lwjgui.scene.control.SeparatorMenuItem;
import lwjgui.scene.layout.BorderPane;

public class CodeAreaContextMenuExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create background pane
		BorderPane pane = new BorderPane();
		
		// Create code area
		CodeArea c = new CodeArea();
		c.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
		pane.setCenter(c);
		
		// Context menu stuff
		{
			ContextMenu menu = new ContextMenu();
			c.setContextMenu(menu);
			
			MenuItem cut = new MenuItem("Cut");
			cut.setOnAction((event)->{
				c.cut();
			});
			menu.getItems().add(cut);
			
			MenuItem copy = new MenuItem("Copy");
			copy.setOnAction((event)->{
				c.copy();
			});
			menu.getItems().add(copy);

			MenuItem paste = new MenuItem("Paste");
			paste.setOnAction((event)->{
				c.paste();
			});
			menu.getItems().add(paste);
			
			menu.getItems().add(new SeparatorMenuItem());
			
			MenuItem clear = new MenuItem("Clear");
			clear.setOnAction((event)->{
				c.clear();
			});
			menu.getItems().add(clear);
		}
		
		// Add some text
		c.setText("print(\"Hello World\")\n"
				+ "\n"
				+ "var a = 10\n"
				+ "var test = \"I'm a string\"");
		
		// Set the scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		window.show();
	}

	@Override
	protected void run() {
		//
	}
}