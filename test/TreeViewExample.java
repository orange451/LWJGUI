package test;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.ScrollPane;
import lwjgui.scene.control.TreeItem;
import lwjgui.scene.control.TreeView;
import lwjgui.scene.layout.StackPane;

public class TreeViewExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Tree View Example", WIDTH, HEIGHT, true, false);
		
		// Initialize lwjgui for this window
		Window lwjguiWindow = LWJGUI.initialize(window);
		
		// Add some components
		addComponents(lwjguiWindow.getScene());
		
		// Game Loop
		while (!GLFW.glfwWindowShouldClose(window)) {
			// Render GUI
			LWJGUI.render();
		}
		
		// Stop GLFW
		glfwTerminate();
	}

	private static void addComponents(Scene scene) {
		// Create a simple pane
		StackPane pane = new StackPane();
		scene.setRoot(pane);
		
		// Create a scroll pane (for the tree)
		ScrollPane scroll = new ScrollPane();
		scroll.setPrefSize(200, 200);
		pane.getChildren().add(scroll);
		
		// Create a tree
		TreeView<String> tree = new TreeView<String>();
		scroll.setContent(tree);
		
		// Add a item to the tree
		tree.getItems().add(new TreeItem<String>("Hello World"));
		
		// Add a item with children
		TreeItem<String> test = new TreeItem<String>("I have children");
		test.getItems().add(new TreeItem<String>("Child 1"));
		test.getItems().add(new TreeItem<String>("Child 2"));
		test.getItems().add(new TreeItem<String>("Child 3"));
		tree.getItems().add(test);
		
		// Recursive testing
		TreeItem<String> last = test;
		for (int i = 1; i <= 10; i++) {
			TreeItem<String> item = new TreeItem<String>("Recursive " + i);
			last.getItems().add(item);
			last = item;
		}
		
		// Add another item
		tree.getItems().add(new TreeItem<String>("Last item"));
	}
}