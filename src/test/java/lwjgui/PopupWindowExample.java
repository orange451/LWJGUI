package lwjgui;

import org.lwjgl.glfw.GLFW;

import lwjgui.LWJGUI;
import lwjgui.LWJGUIUtil;
import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class PopupWindowExample extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Add background pane
		StackPane pane = new StackPane();
		window.getScene().setRoot(pane);
		
		// Create vertical box
		VBox vbox = new VBox();
		vbox.setSpacing(8);
		vbox.setAlignment(Pos.CENTER);
		pane.getChildren().add(vbox);

		// Create the button for the box
		Button button = new Button("Click Me!");
		button.setOnAction((event)-> {
			popup("Test Popup");
		});
		
		// Add the components
		vbox.getChildren().add(button);
	}
	
	protected static void popup(String popup) {
		// Create a popup window
		long pWin = LWJGUIUtil.createOpenGLCoreWindow(popup, 250, 150, false, true);
		Window newWindow = LWJGUI.initialize(pWin);
		newWindow.setCanUserClose(false); // Prevent user from xing out of window
		Scene scene = newWindow.getScene();
		
		// Create root pane
		BorderPane root = new BorderPane();
		root.setPadding(new Insets(4,4,4,4));
		scene.setRoot(root);
		
		// Create a label
		Label l = new Label("Congratulations, You've won!");
		root.setCenter(l);
		
		// Create a button
		Button b = new Button("Claim prize");
		root.setBottom(b);
		b.setOnAction((event)-> {
			GLFW.glfwSetWindowShouldClose(pWin, true);
		});
	}

	@Override
	public void run() {
		//
	}

	@Override
	public String getProgramName() {
		return "Popup Example";
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