package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
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

public class ContextMenuExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Context Menu Example", WIDTH, HEIGHT, true, false);
		
		// Initialize lwjgui for this window
		Window newWindow = LWJGUI.initialize(window);
		Scene scene = newWindow.getScene();
		
		// Add some components
		addComponents(scene);
		
		// Game Loop
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Render GUI
			LWJGUI.render();
		}
		
		// Stop GLFW
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		// Create background pane
		BorderPane background = new BorderPane();
		scene.setRoot(background);
		
		
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
			menu.getItems().add(new MenuItem("Option 3", new Circle(Color.RED, 8)));
			menu.setAutoHide(false);
			
			// Create context node
			Label l = new Label("Don't right click me");
			l.setContextMenu(menu);
			background.setCenter(l);
		}
	}
}