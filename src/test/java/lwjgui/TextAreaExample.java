package lwjgui;

import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwTerminate;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Insets;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.PasswordField;
import lwjgui.scene.control.TextArea;
import lwjgui.scene.control.TextField;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.HBox;

public class TextAreaExample {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) throws IOException {
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Create a standard opengl 3.2 window. You can do this yourself.
		long window = LWJGUIUtil.createOpenGLCoreWindow("Text Area Example", WIDTH, HEIGHT, true, false);
		
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
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(8,8,8,8));
		scene.setRoot(pane);
		
		HBox hbox = new HBox();
		hbox.setSpacing(8);
		pane.setTop(hbox);
		
		// Create an Input Field
		TextField f = new TextField();
		f.setPrompt("Text Field");
		hbox.getChildren().add(f);
		
		// Password field
		PasswordField p = new PasswordField();
		p.setPrompt("Password Field");
		hbox.getChildren().add(p);
		
		// Create a Text Area
		TextArea t = new TextArea();
		t.setWordWrap(true);
		t.setPreferredColumnCount(22);
		t.setPreferredRowCount(8);
		pane.setCenter(t);
		
		// Clear text button
		Button b = new Button("Clear Text");
		b.setOnAction((event)->{
			t.clear();
		});
		pane.setBottom(b);
	}
}