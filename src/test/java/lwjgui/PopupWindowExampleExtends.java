package lwjgui;

import lwjgui.geometry.Insets;
import lwjgui.geometry.Pos;
import lwjgui.scene.Scene;
import lwjgui.scene.Window;
import lwjgui.scene.WindowHandle;
import lwjgui.scene.WindowManager;
import lwjgui.scene.WindowThread;
import lwjgui.scene.control.Button;
import lwjgui.scene.control.Label;
import lwjgui.scene.layout.BorderPane;
import lwjgui.scene.layout.StackPane;
import lwjgui.scene.layout.VBox;

public class PopupWindowExampleExtends extends LWJGUIApplication {
	public static final int WIDTH   = 320;
	public static final int HEIGHT  = 240;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(String[] args, Window window) {
		// Add background pane
		StackPane pane = new StackPane();
		
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
		
		// Set the scene
		window.setScene(new Scene(pane, WIDTH, HEIGHT));
		window.show();
	}
	
	static class Popup extends WindowThread {

		public Popup(String title) {
			super(300, 100, title);
		}
		
		@Override
		protected void setupHandle(WindowHandle handle) {
			super.setupHandle(handle);
			handle.canResize(false);
		}

		@Override
		protected void init(Window window) {
			super.init(window);
			window.setCanUserClose(false);
			// Create root pane
			BorderPane root = new BorderPane();
			root.setPadding(new Insets(4,4,4,4));
			
			// Create a label
			Label l = new Label("Congratulations, You've won!");
			root.setCenter(l);
			
			// Create a button
			Button b = new Button("Claim prize");
			root.setBottom(b);
			b.setOnAction((event)-> {
				window.close();
			});
			
			// Display window
			window.setScene(new Scene(root, 250, 75));
			window.show();
		}
		
	}
	
	protected static void popup(String popup) {
		// Create a popup window
		WindowManager.runLater(() -> {
			new Popup(popup).start(); // Create and start thread
		});
	}

	@Override
	protected void run() {
		//
	}
}