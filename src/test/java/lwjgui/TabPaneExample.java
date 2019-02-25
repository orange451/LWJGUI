package lwjgui;


import lwjgui.paint.Color;
import lwjgui.scene.Window;
import lwjgui.scene.control.Tab;
import lwjgui.scene.control.TabPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.shape.Circle;
import lwjgui.scene.shape.Rectangle;

public class TabPaneExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Create a simple root pane
		StackPane pane = new StackPane();
		window.getScene().setRoot(pane);
		
		// Tab Root Pane
		TabPane tabs = new TabPane();
		pane.getChildren().add(tabs);
		
		// Tab 1
		{
			Tab tab = new Tab("Tab 1");
			tab.setContent(new Circle(64));
			tabs.getTabs().add(tab);
		}
		
		// Tab 2
		{
			Tab tab = new Tab("Tab 2");
			tab.setContent(new Rectangle(96, 96, 8, Color.RED));
			tabs.getTabs().add(tab);
		}
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "Tab Pane Example";
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